package iode.olzserver.service;

import iode.olzserver.data.LoopRepository;
import iode.olzserver.data.RefRepository;
import iode.olzserver.domain.Loop;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoopServiceImpl extends AbstractLoopService implements LoopService {
	private static final String NEW_LOOP_CONTENT = "<loop><body><p>%s</p></body><tags-box/></loop>";

	private static final Long TEMP_SLICE_ID = 1L; //Slice is hardcoded for now.

	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private LoopRepository loopRepo;

	@Autowired
	private RefRepository refRepo;

	@Override
	public Loop createLoop(Loop loop) {
		return createLoop(loop, null);
	}

	@Override
	public Loop createLoop(Loop loop, String parentLoopId) {
		if(loop.getId() == null) {
			String loopId = "#" + String.valueOf(loopRepo.getAndUpdateSliceNextNumber(1));
			
			loop = loop.copyWithNewId(loopId);
		}

		if(loop.getSliceId() == null) {
			loop = loop.copyWithNewSliceId(Long.valueOf(TEMP_SLICE_ID)); 
		}

		if(parentLoopId != null) {
			loop = loop.xml().addTag(parentLoopId).loopWithUpdatedContent();
		}
		loop = loopRepo.createLoop(loop);		

		List<String> loopRefs = loop.xml().getLoopRefs();

		for(String loopRef : loopRefs) {
			broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
		}

		return loop;
	}

	@Override
	public Loop getLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(" + loopId + ")");
		}
		
		Loop loop = null;
		try {
			loop = loopRepo.getLoop(loopId, TEMP_SLICE_ID);
		} catch(LoopNotFoundException e) {
			return createLoop(new Loop(loopId, String.format(NEW_LOOP_CONTENT, loopId)));	
		}
		
		if(log.isDebugEnabled()) {
			log.debug("loop=" + loop);
		}

		List<Loop> innerLoops = loopRepo.findInnerLoops(loopId, TEMP_SLICE_ID);
		if(log.isDebugEnabled()) {
			log.debug("innerLoops=" + innerLoops);
		}
		return loop.copyWithNewInnerLoops(innerLoops);
	}

	@Override
	@Transactional
	public Loop updateLoop(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}

		Loop dbLoop = loopRepo.getLoop(loop.getId(), TEMP_SLICE_ID);
		loop = loopRepo.updateLoop(loop);
		List<String> dbLoopRefs = dbLoop.xml().getLoopRefs();
		List<String> newLoopRefs = loop.xml().getLoopRefs();

		for(String loopRef : newLoopRefs) {
			if(!dbLoopRefs.contains(loopRef)) {
				broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
			}
		}
		
		List<Loop> innerLoops = new ArrayList<Loop>();
		for(Loop innerLoop : loop.getLoops()) {
			innerLoops.add(updateLoop(innerLoop));
		}
		return loop.copyWithNewInnerLoops(innerLoops);
	}

	private void broadcastLoopChange(String loopRef, Loop loop, LoopStatus status) {
		this.template.convertAndSend("/topic/loop-changes/" + loopRef, loop.copyWithNewStatus(status).convertLoopToHtml());		
	}

	@Override
	public void updateFilterText(String loopId, String filterText) {
		loopRepo.updateFilterText(loopId, filterText);		
	}

	@Override
	public void updateShowInnerLoops(String loopId, Boolean showInnerLoops) {
		loopRepo.updateShowInnerLoops(loopId, showInnerLoops);
		
	}
}
