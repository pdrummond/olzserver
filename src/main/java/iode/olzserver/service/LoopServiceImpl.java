package iode.olzserver.service;

import iode.olzserver.data.ListRepository;
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

	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private LoopRepository loopRepo;

	@Autowired
	private ListRepository listRepo;
	
	@Override
	public Loop getLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId = " + loopId + ")");
		}
		return loopRepo.getLoop(loopId, 1L);
	}
	
	@Override
	public List<Loop> findLoopsByQuery(String query) {
		if(log.isDebugEnabled()) {
			log.debug("findLoopsByQuery(query = " + query + ")");
		}

		List<Loop> loops = new ArrayList<Loop>();
		for(Loop loop : loopRepo.findLoopsByQuery(query, 1L)) {
			loops.add(loop.copyWithNewLists(listRepo.getListsForLoop(loop.getId())));
		}

		if(log.isDebugEnabled()) {
			log.debug("innerLoops=" + loops);
		}

		return loops;
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
		
		/*if(!loop.getContent().contains(":")) {
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

	@Override
	public List<Loop> getAllLoops() {
		if(log.isDebugEnabled()) {
			log.debug("getAllLoops()");
		}

		List<Loop> loops = loopRepo.getAllLoops();

		if(log.isDebugEnabled()) {
			log.debug("innerLoops=" + loops);
		}

		return loops;	
	}
}
