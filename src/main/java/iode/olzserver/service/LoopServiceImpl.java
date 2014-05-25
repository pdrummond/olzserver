package iode.olzserver.service;

import iode.olzserver.data.ListRepository;
import iode.olzserver.data.LoopRepository;
import iode.olzserver.domain.Loop;
import iode.olzserver.domain.LoopList;
import iode.olzserver.domain.User;
import iode.olzserver.utils.MD5Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
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
			processedLoops.add(processLoop(loop, parentLoopId, userId));
		}
		return processedLoops;
	}

	private Loop processLoop(Loop loop, String parentLoopId, String userId) {
		boolean loopOk = false;
		loop = loop.copyWithNewLists(listRepo.getListsForLoop(loop.getId()));
		if(parentLoopId == null || !loop.getId().equals(parentLoopId)) { //if parentLoopId, then only include loop if it's not parent loop.
			boolean hasOwner = loop.hasOwner();
			if(hasOwner) {
				loop = loop.copyWithNewOwnerImageUrl(generateOwnerImageUrl(loop));
			}

			if(userId != null && hasOwner) {
				List<String> userTags = loop.findUserTags_();
				if(userTags.contains(userId)) {
					loopOk = true;
				}
			} else {		
				loopOk = true;
			}
		}
		
		if(loopOk) {
			ArrayList<LoopList> lists = new ArrayList<LoopList>();
			for(LoopList list : loop.getLists()) {
				List<Loop> listLoops = loopRepo.findLoopsByQuery(list.getQuery(), 1L);
				listLoops.remove(loop); //the main loop cannot be included in the lists.
				list = list.copyWithNewLoops(listLoops);
				lists.add(list);
			}
			loop = loop.copyWithNewLists(lists);
		}
		return loopOk?loop:null;
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
	public Loop createLoop(Loop loop, String userId) {
		if(loop.getId() == null) {
			String loopId = "#" + UUID.randomUUID().toString();//String.valueOf(podRepo.getAndUpdatePodNextNumber(pod.getId()));
			loop = loop.copyWithNewId(loopId);
		}
		loop = loopRepo.createLoop(loop);

		String query = StringUtils.join(loop.findTags(), ' ');
		if(!StringUtils.isEmpty(query)) {
			LoopList relatedLoopsList = new LoopList(UUID.randomUUID().toString(), loop.getId(), "Related Loops", query, new Date(), loop.getCreatedBy()); 
			List<LoopList> lists = new ArrayList<>();		
			lists.add(listRepo.createList(relatedLoopsList));		
			loop = loop.copyWithNewLists(lists);

			List<String> loopRefs = loop.findTags();

			for(String loopRef : loopRefs) {
				broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
			}
		}
		//broadcastLoopChange(pod.getName(), loop, LoopStatus.ADDED); //broadcast change for pod.

		return processLoop(loop, null, userId);
	}

	@Override
	@Transactional
	public Loop updateLoop(Loop loop, String userId) {
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

		return processLoop(loop, null, userId);
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
