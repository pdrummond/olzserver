package iode.olzserver.service;

import iode.olzserver.data.LoopRepository;
import iode.olzserver.data.PodRepository;
import iode.olzserver.domain.Loop;
import iode.olzserver.domain.Pod;

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

	@Autowired
	private PodRepository podRepo;

	@Override
	public Loop getLoop(String handle) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopHandle = " + handle + ")");
		}

		/*LoopHandle loopHandle = new LoopHandle(handle);

		Pod pod = null;
		try {
			pod = podRepo.getPodByName(loopHandle.getPodName());
		} catch(PodNotFoundException e) {
			pod = podRepo.createPod(loopHandle.getPodName());
		}*/

		Loop loop = null;
		try {
			loop = loopRepo.getLoop(handle, null);
		} catch(LoopNotFoundException e) {
			return createLoop(new Loop(handle, 0L, NEW_LOOP_CONTENT));	
		}

		if(log.isDebugEnabled()) {
			log.debug("loop=" + loop);
		}

		List<Loop> innerLoops = null;
		if(handle.equals("#outerloop")) {
			innerLoops = loopRepo.getAllLoops();
		} else {
			innerLoops = loopRepo.findInnerLoops(handle, 1L);
		}

		//if(loopHandle.getLoopId().equals(loopHandle.getPodName())) {
		//innerLoops = loopRepo.findAllLoopsInPod(pod);
		//} else {
		//innerLoops = loopRepo.findInnerLoops(handle, 1L);
		//}

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
		

		/*Pod pod = null;
		if(loop.getPodId() == null) {			
			pod = getCurrentPod();
			loop = loop.copyWithNewPodId(Long.valueOf(pod.getId()));
		} else {
			pod = podRepo.getPod(loop.getPodId());
		}*/

		if(loop.getId() == null) {
			String loopId = "#" + UUID.randomUUID().toString();//String.valueOf(podRepo.getAndUpdatePodNextNumber(pod.getId()));
			loop = loop.copyWithNewId(loopId);
		}
		
		if(!loop.getContent().contains(":")) {
			loop = loop.copyWithNewContent(loop.getId() + ": " + loop.getContent());
		}

		if(parentLoopId != null ) { //&& !parentLoopId.equals(pod.getName())) { 
			loop = loop.copyWithNewContent(loop.getContent() + " " + parentLoopId);
		}

		loop = loopRepo.createLoop(loop);		

		List<String> loopRefs = loop.findBodyTags();

		for(String loopRef : loopRefs) {
			broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
		}
		//broadcastLoopChange(pod.getName(), loop, LoopStatus.ADDED); //broadcast change for pod.

		return loop;
	}

	@Override
	@Transactional
	public Loop updateLoop(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}

		Long podId = loop.getPodId();
		if(podId == null) {
			podId = getCurrentPod().getId();
		}

		Loop dbLoop = loopRepo.getLoop(loop.getId(), podId);
		loop = loopRepo.updateLoop(loop);
		List<String> dbLoopRefs = dbLoop.findBodyTags();
		List<String> newLoopRefs = loop.findBodyTags();

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
	public void updateFilterText(String loopHandle, String filterText) {
		//LoopHandle handle = new LoopHandle(loopHandle);
		//Pod pod = podRepo.getPodByName(handle.getPodName());
		loopRepo.updateFilterText(loopHandle, 1L, filterText);		
	}

	@Override
	public void updateShowInnerLoops(String loopHandle, Boolean showInnerLoops) {
		//LoopHandle handle = new LoopHandle(loopHandle);
		//Pod pod = podRepo.getPodByName(handle.getPodName());
		loopRepo.updateShowInnerLoops(loopHandle, 1L, showInnerLoops);
	}

	private Pod getCurrentPod() {
		return podRepo.getPodByName("@iode");
	}
}
