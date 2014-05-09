package iode.olzserver.service;

import iode.olzserver.data.LoopRepository;
import iode.olzserver.domain.Loop;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoopServiceImpl extends AbstractLoopService implements LoopService {
	private static final String NEW_LOOP_CONTENT = "*New Loop*";

	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private LoopRepository loopRepo;

	@Override
	public Loop getLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId = " + loopId + ")");
		}

		Loop loop = null;
		try {
			loop = loopRepo.getLoop(loopId);
		} catch(LoopNotFoundException e) {
			return createLoop(new Loop(loopId, String.format(NEW_LOOP_CONTENT, loopId)));	
		}

		if(log.isDebugEnabled()) {
			log.debug("loop=" + loop);
		}

		List<Loop> innerLoops = null;
		innerLoops = loopRepo.findInnerLoops(loopId);

		if(log.isDebugEnabled()) {
			log.debug("innerLoops=" + innerLoops);
		}

		return loop.copyWithNewInnerLoops(innerLoops);
	}

	@Override
	public Loop createLoop(Loop loop) {
		return createLoop(loop, null);
	}

	@Override
	public Loop createLoop(Loop loop, String parentLoopId) {

		if(loop.getId() == null) {
			loop = loop.copyWithNewId("@" + UUID.randomUUID().toString());
		}

		if(parentLoopId != null) { 
			loop = loop.copyWithNewContent(loop.getContent() + " " + parentLoopId);
		}
		loop = loopRepo.createLoop(loop);		

		List<String> loopRefs = loop.findLoopRefs();

		for(String loopRef : loopRefs) {
			broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
		}
		return loop;
	}

	@Override
	@Transactional
	public Loop updateLoop(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}
		Loop dbLoop = null;
		List<String> dbLoopRefs = new ArrayList<String>();
		try {
			dbLoop = loopRepo.getLoop(loop.getId());
			dbLoopRefs = dbLoop.findLoopRefs();
		} catch(LoopNotFoundException e) {
			log.debug("Cannot find loop for id " + loop.getId());
		}
		if(dbLoop == null) {
			loop = loopRepo.createLoop(loop);
		} else {
			loop = loopRepo.updateLoop(loop);
		}

		List<String> newLoopRefs = loop.findLoopRefs();
		for(String loopRef : newLoopRefs) {
			if(!dbLoopRefs.contains(loopRef)) {
				broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
			}
		}
		return loop;
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
