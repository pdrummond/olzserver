package iode.olzserver.service;

import iode.olzserver.data.ListRepository;
import iode.olzserver.data.LoopRepository;
import iode.olzserver.domain.Loop;
import iode.olzserver.domain.LoopList;
import iode.olzserver.domain.User;
import iode.olzserver.utils.MD5Util;

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
	private UserService userService;

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
	public List<Loop> findLoopsByQuery(String query, String parentLoopId, String userId) {
		if(log.isDebugEnabled()) {
			log.debug("findLoopsByQuery(query = " + query + ")");
		}
		List<Loop> loops = processLoops(loopRepo.findLoopsByQuery(query, 1L), parentLoopId, userId);
		return loops;
	}


	private List<Loop> processLoops(List<Loop> loops, String parentLoopId, String userId) {
		List<Loop> processedLoops = new ArrayList<Loop>();
		for(Loop loop : loops) {
			if(parentLoopId == null || !loop.getId().equals(parentLoopId)) { //if parentLoopId, then only include loop if it's not parent loop.
				boolean hasOwner = loop.hasOwner();
				if(hasOwner) {
					loop = loop.copyWithNewOwnerImageUrl(generateOwnerImageUrl(loop));
				}

				if(userId != null && hasOwner) {
					List<String> userTags = loop.findUserTags_();
					if(userTags.contains(userId)) {
						processedLoops.add(loop.copyWithNewLists(listRepo.getListsForLoop(loop.getId())));
					}
				} else {		
					processedLoops.add(loop.copyWithNewLists(listRepo.getListsForLoop(loop.getId())));
				}
			}

		}		
		return processedLoops;
	}

	private String generateOwnerImageUrl(Loop loop) {
		String owner = loop.findOwner();
		String url = null;

		if(owner != null) {

			User user = userService.getUser(owner);
			if(user != null) {
				String hash = MD5Util.md5Hex(user.getEmail().toLowerCase());
				url = String.format("http://www.gravatar.com/avatar/%s?s=40", hash);
			}
		} else {
			url = "??";
		}
		return url;
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

		/*if(!loop.getContent().contains(":")) {
			loop = loop.copyWithNewContent(loop.getId() + ": " + loop.getContent());
		}*/

		if(parentLoopId != null ) { //&& !parentLoopId.equals(pod.getName())) { 
			loop = loop.copyWithNewContent(loop.getContent() + " " + parentLoopId);
		}
		


		loop = loopRepo.createLoop(loop);
		
		
		/*String query = StringUtils.join(loop.findTags(), ' ') + " #comment";
		LoopList commentList = new LoopList(UUID.randomUUID().toString(), loop.getId(), "Comments", query, loop.getCreatedBy()); 
		List<LoopList> lists = new ArrayList<>();		
		lists.add(listRepo.createList(commentList));		
		loop = loop.copyWithNewLists(lists);*/

		List<String> loopRefs = loop.findTags();

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
		List<String> dbLoopRefs = dbLoop.findTags();
		List<String> newLoopRefs = loop.findTags();

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
	public List<Loop> getAllLoops(String userId) {
		if(log.isDebugEnabled()) {
			log.debug("getAllLoops(userId=" + userId + ")");
		}
		List<Loop> loops = processLoops(loopRepo.getAllLoops(), null, userId);
		return loops;	
	}

	@Override
	public LoopList createList(LoopList list) {
		if(log.isDebugEnabled()) {
			log.debug("createList(list=" + list + ")");
		}
		if(list.getId() == null) {
			list = list.copyWithNewId(UUID.randomUUID().toString());
		}
		return listRepo.createList(list);
	}
}
