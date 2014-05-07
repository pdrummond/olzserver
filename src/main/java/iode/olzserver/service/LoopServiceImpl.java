package iode.olzserver.service;

import iode.olzserver.data.LoopRepository;
import iode.olzserver.data.SliceRepository;
import iode.olzserver.domain.Loop;
import iode.olzserver.domain.Slice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoopServiceImpl extends AbstractLoopService implements LoopService {
	private static final String NEW_LOOP_CONTENT = "*%s*";

	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private LoopRepository loopRepo;

	@Autowired
	private SliceRepository sliceRepo;
	
	@Override
	public Loop getLoop(String loopHandle) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopHandle = " + loopHandle + ")");
		}
		
		String loopId = null;
		String sliceName = null;
		if(loopHandle.contains("#") && loopHandle.contains("@")) {
			loopId = loopHandle.split("@")[0];
			sliceName = loopHandle.split("@")[1];
		} else if(loopHandle.contains("#")) {
			loopId = loopHandle;
		} else if(loopHandle.contains("@")) {
			sliceName = loopHandle;
		}
		
		if(loopId == null) {
			loopId = sliceName;
		}
		
		if(sliceName == null) {
			sliceName = "@iode"; //TEMP: 'CURRENT SLICE' HARDCODDED FOR NOW;
		}
		
		Slice slice = null;
		try {
			slice = sliceRepo.getSliceByName(sliceName);
		} catch(SliceNotFoundException e) {
			slice = sliceRepo.createSlice(sliceName);
		}
		
		Loop loop = null;
		try {
			loop = loopRepo.getLoop(loopId, slice.getId());
		} catch(LoopNotFoundException e) {
			return createLoop(new Loop(loopId, slice.getId(), String.format(NEW_LOOP_CONTENT, loopId)));	
		}
		
		if(log.isDebugEnabled()) {
			log.debug("loop=" + loop);
		}
		
		List<Loop> innerLoops = null;
		if(loopId.equals(sliceName)) {
			innerLoops = loopRepo.findAllLoopsForSlice(slice);
		} else {
			innerLoops = loopRepo.findInnerLoops(loopId, slice.getId());
		}
		
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

		Slice slice = null;
		if(loop.getSliceId() == null) {			
			slice = getCurrentSlice();
			loop = loop.copyWithNewSliceId(Long.valueOf(slice.getId()));
		} else {
			slice = sliceRepo.getSlice(loop.getSliceId());
		}

		if(loop.getId() == null) {
			String loopId = "#" + String.valueOf(sliceRepo.getAndUpdateSliceNextNumber(slice.getId()));
			loop = loop.copyWithNewId(loopId);
		}
		

		if(parentLoopId != null && !parentLoopId.equals(slice.getName())) { 
			loop = loop.copyWithNewContent(loop.getContent() + " " + parentLoopId);
		}
		loop = loopRepo.createLoop(loop);		

		List<String> loopRefs = loop.getTags();

		for(String loopRef : loopRefs) {
			broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
		}
		broadcastLoopChange(slice.getName(), loop, LoopStatus.ADDED); //broadcast change for slice.

		return loop;
	}


	@Override
	@Transactional
	public Loop updateLoop(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}
		
		Long sliceId = loop.getSliceId();
		if(sliceId == null) {
			sliceId = getCurrentSlice().getId();
		}
		
		Loop dbLoop = loopRepo.getLoop(loop.getId(), sliceId);
		loop = loopRepo.updateLoop(loop);
		List<String> dbLoopRefs = dbLoop.getTags();
		List<String> newLoopRefs = loop.getTags();

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

	private Slice getCurrentSlice() {
		return sliceRepo.getSliceByName("@iode");
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
