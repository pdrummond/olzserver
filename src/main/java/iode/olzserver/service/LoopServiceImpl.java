package iode.olzserver.service;

import iode.olzserver.data.LoopRepository;
import iode.olzserver.data.PodRepository;
import iode.olzserver.domain.Loop;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoopServiceImpl extends AbstractLoopService implements LoopService {

	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private LoopRepository loopRepo;

	@Autowired
	private PodRepository podRepo;
<<<<<<< HEAD

<<<<<<< HEAD
=======
	@Override
	public Loop getLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId = " + loopId + ")");
		}

		Loop loop = null;

		try {
			loop = loopRepo.getLoop(loopId, 1L);
		} catch(LoopNotFoundException e) {
			loop = createLoop(new Loop(loopId, 1L, "New Loop"));	
		}
		
		List<Loop> innerLoops = null;
		if(loopId.equals("#outerloop")) {
			innerLoops = loopRepo.getAllLoops();
		} else {
			innerLoops = loopRepo.findInnerLoops(loop);
		}

		if(log.isDebugEnabled()) {
			log.debug("innerLoops=" + innerLoops);
		}

		return loop.copyWithNewInnerLoops(innerLoops);
	}

>>>>>>> exp-single-loop
=======
	
>>>>>>> parent of ff2bce4... Revert 39cd058..244d737
	@Override
	public Loop getLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId = " + loopId + ")");
		}
		return loopRepo.getLoop(loopId, 1L);
	}
	
	@Override
	public Loop getLoopByQuery(String query) {
		if(log.isDebugEnabled()) {
			log.debug("getLoopByQuery(query = " + query + ")");
		}

		Loop loop = null;
		try {
			loop = loopRepo.findLoopByContents(query);
		} catch(LoopNotFoundException e) {
			loop = createLoop(new Loop(UUID.randomUUID().toString(), 1L, query));	
		}

<<<<<<< HEAD
<<<<<<< HEAD
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
=======
		List<Loop> innerLoops = null;//loopRepo.findInnerLoops(query, 1L);
>>>>>>> exp-single-loop
=======
		List<Loop> innerLoops = loopRepo.findInnerLoops(query, 1L);
>>>>>>> parent of ff2bce4... Revert 39cd058..244d737

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
			String loopId = UUID.randomUUID().toString();//String.valueOf(podRepo.getAndUpdatePodNextNumber(pod.getId()));
			loop = loop.copyWithNewId(loopId);
		}
<<<<<<< HEAD
		
<<<<<<< HEAD
		if(!loop.getContent().contains(":")) {
=======

		/*if(!loop.getContent().contains(":")) {
>>>>>>> exp-single-loop
=======
		/*if(!loop.getContent().contains(":")) {
>>>>>>> parent of ff2bce4... Revert 39cd058..244d737
			loop = loop.copyWithNewContent(loop.getId() + ": " + loop.getContent());
		}*/

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

		Loop dbLoop = loopRepo.getLoop(loop.getId(), 1L);
		loop = loopRepo.updateLoop(loop);
		List<String> dbLoopRefs = dbLoop.findBodyTags();
		List<String> newLoopRefs = loop.findBodyTags();

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

<<<<<<< HEAD
	private Pod getCurrentPod() {
		return podRepo.getPodByName("@iode");
=======
	@Override
	public Loop getOuterLoop() {
		if(log.isDebugEnabled()) {
			log.debug("getOuterLoop()");
		}

		Loop loop = null;
		try {
			loop = loopRepo.getLoop("outerloop", 1L);
		} catch(LoopNotFoundException e) {
			return createLoop(new Loop("outerloop", 0L, "This place is special"));	
		}

		List<Loop> innerLoops = loopRepo.getAllLoops();

		if(log.isDebugEnabled()) {
			log.debug("innerLoops=" + innerLoops);
		}

		return loop.copyWithNewInnerLoops(innerLoops);	
>>>>>>> exp-single-loop
	}

	@Override
	public Loop getOuterLoop() {
		if(log.isDebugEnabled()) {
			log.debug("getOuterLoop()");
		}

		Loop loop = null;
		try {
			loop = loopRepo.getLoop("outerloop", 1L);
		} catch(LoopNotFoundException e) {
			return createLoop(new Loop("outerloop", 0L, "This place is special"));	
		}

		List<Loop> innerLoops = loopRepo.getAllLoops();

		if(log.isDebugEnabled()) {
			log.debug("innerLoops=" + innerLoops);
		}

		return loop.copyWithNewInnerLoops(innerLoops);	
	}
}
