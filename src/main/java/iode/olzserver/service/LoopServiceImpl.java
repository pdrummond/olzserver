package iode.olzserver.service;

import iode.olzserver.data.ListRepository;
import iode.olzserver.data.LoopRepository;
import iode.olzserver.data.UserRepository;
import iode.olzserver.domain.Loop;
import iode.olzserver.domain.LoopList;
import iode.olzserver.domain.User;
import iode.olzserver.error.LoopPermissionException;
import iode.olzserver.transform.HtmlifyTags;
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

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Service
public class LoopServiceImpl extends AbstractLoopService implements LoopService {

	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private LoopRepository loopRepo;

	@Autowired
	private ListRepository listRepo;

	@Override
	public Loop getLoop(String loopId, String pods, String userId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId = " + loopId + ")");
		}
		
		if(loopId.split("@").length == 0) {
			throw new IllegalArgumentException("Loop must have an owner");
		}

		Loop loop = null;
		loop = null;
		try {
			loop = processOutgoingLoop(loopRepo.getLoop(loopId, 1L), pods, null, userId, true);
		} catch(LoopNotFoundException e) {
			loop = createLoop(new Loop(loopId, "<loop><loop-header></loop-header><loop-body></loop-body><loop-footer></loop-footer></loop>"), userId);
		}
		return loop;
	}

	@Override
	public List<Loop> findLoopsByQuery(String query, String pods, Long since, Boolean detailed, String parentLoopId, String userId) {
		if(log.isDebugEnabled()) {
			log.debug("findLoopsByQuery(query = " + query + ", since=" + since + ")");
		}
		List<Loop> loops = processOutgoingLoops(loopRepo.findLoopsByQuery(query, pods, 1L, since), parentLoopId, userId, detailed);
		return loops;
	}

	private Loop processIncomingLoop(Loop loop, String userId) {
		if(loop.isIncomingProcessingDone()) {
			return loop;
		}

		loop = loop.copyWithNewContent(new HtmlifyTags(loop.getContent()).execute());

		/*String owner = loop.xml().findOwnerTag_();
		List<String> userTags = loop.xml().findUserTags_();
		for(String userTag : userTags) {		
			User user = userService.getUser(userTag);
			if(user != null) {
				Loop notificationLoop = Loop.createWithContent(String.format("@%s has mentioned you in a loop", owner), null, "@!" + userTag + " #notification").incomingProcessingDone();				
				createLoop(notificationLoop, userId); 
			}
		}*/

		return loop.incomingProcessingDone();
	}

	private List<Loop> processOutgoingLoops(List<Loop> loops, String parentLoopId, String userId, Boolean detailed) {
		List<Loop> processedLoops = new ArrayList<Loop>();
		for(Loop loop : loops) {
			processedLoops.add(processOutgoingLoop(loop, "1", parentLoopId, userId, detailed));
		}
		return Lists.newArrayList(Iterables.filter(processedLoops, Predicates.notNull()));
	}

	private Loop processOutgoingLoop(Loop loop, String pods, String parentLoopId, String currentUserId, Boolean detailed) {
		boolean loopOk = false;
		
		loop = loop.copyWithNewTags(loop.xml().findAllTags());		
		loop = loop.copyWithNewLists(listRepo.getListsForLoop(loop.getId()));
		loop = getLoopWithOwner(loop);
		if(parentLoopId == null || !loop.getId().equals(parentLoopId)) { //if parentLoopId, then only include loop if it's not parent loop.
			loopOk = true;
		}
		
		if(loopOk) {
			loopOk = userHasAccessToLoop(loop, currentUserId);
		}

		if(loopOk) {
			ArrayList<LoopList> lists = new ArrayList<LoopList>();
			for(LoopList list : loop.getLists()) {
				if(detailed) {
					pods = "1";
					List<Loop> listLoops = loopRepo.findLoopsByQuery(list.getQuery(), pods, 1L, null);
					listLoops.remove(loop); //the main loop cannot be included in the lists.
					List<Loop> newListLoops = new ArrayList<Loop>();
					for(Loop listLoop: listLoops) {
						if(userHasAccessToLoop(listLoop, currentUserId)) {
							newListLoops.add(getLoopWithOwner(listLoop));
						}
					}
					list = list.copyWithNewLoops(newListLoops);
				}
				lists.add(list);
			}
			loop = loop.copyWithNewLists(lists);
		}
		
		return loopOk?loop:null;
	}

	private boolean userHasAccessToLoop(Loop loop, String currentUserId) {
		String ownerName = loop.extractOwnerTagFromId();
		return loop.xml().findHashTags().contains("#public@openloopz") || 
				currentUserId.equals(ownerName) || 
				loop.xml().findUserTags().contains("@" + currentUserId);
	}

	private Loop getLoopWithOwner(Loop loop) {
		String ownerName = loop.getId().split("@")[1];
		User owner = null;

		if(ownerName != null) {
			owner = userService.getUser(ownerName);
			if(owner != null) {
				String hash = MD5Util.md5Hex(owner.getEmail().toLowerCase());
				owner = owner.copyWithNewImageUrl(String.format("http://www.gravatar.com/avatar/%s?s=40", hash));
				loop = loop.copyWithNewOwner(owner);
			}
		} 
		return loop;
	}

	@Override
	public Loop createLoop(Loop loop, String currentUserId) {
		if(loop.getId() == null) {
			String loopId = "#" + userRepo.getAndUpdateNextLoopId(currentUserId) + "@" + currentUserId;
			loop = loop.copyWithNewId(loopId);
		}
		loop = processIncomingLoop(loop, currentUserId);
		loop = loopRepo.createLoop(loop);

//		//If loop has some hashtags, then give it a default list.
//		List<String> hashtags = loop.xml().findHashTags();
//		if(hashtags.size() > 0) {
//
			//List<String> loopTags = loop.xml().findAllTags();
			//String query = StringUtils.join(loopTags, ' ');
			//if(!StringUtils.isEmpty(query)) {
				LoopList relatedLoopsList = new LoopList(UUID.randomUUID().toString(), loop.getId(), "Related Loops", loop.getId(), "createdAt", "descending", new Date(), loop.getCreatedBy()); 
				List<LoopList> lists = new ArrayList<>();		
				lists.add(listRepo.createList(relatedLoopsList));		
				loop = loop.copyWithNewLists(lists);
			//}

				/*List<String> loopRefs = loop.findTags();

			for(String loopRef : loopRefs) {
				broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
			}*/
			//}
//		}
		//broadcastLoopChange(pod.getName(), loop, LoopStatus.ADDED); //broadcast change for pod.

		return processOutgoingLoop(loop, "1", null, currentUserId, true);
	}

	@Override
	@Transactional
	public Loop updateLoop(Loop loop, String currentUserId) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}
		
		if(!StringUtils.isEmpty(loop.getNewId())) {
			loop = loopRepo.renameLoop(loop);
		}

		loop = processIncomingLoop(loop, currentUserId);
		
		String ownerTag = loop.extractOwnerTagFromId_();
		if(!ownerTag.equals(currentUserId)) {
			throw new LoopPermissionException("You do not have permissions to update this loop");
		}
		
		if(loopRepo.loopExists(loop)) {		
			loop = loopRepo.updateLoop(loop);
		} else {
			userRepo.getAndUpdateNextLoopId(currentUserId);
			loop = createLoop(loop, currentUserId);
		}

		/*Loop dbLoop = loopRepo.getLoop(loop.getId(), 1L);
		List<String> dbLoopRefs = dbLoop.findTags();
		List<String> newLoopRefs = loop.findTags();

		for(String loopRef : newLoopRefs) {
			if(!dbLoopRefs.contains(loopRef)) {
				broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
			}
		}*/

		return processOutgoingLoop(loop, "1", null, currentUserId, true);
	}

	/*private void broadcastLoopChange(String loopRef, Loop loop, LoopStatus status) {
		this.template.convertAndSend("/topic/loop-changes/" + loopRef, loop.copyWithNewStatus(status));		
	}*/

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
	public List<Loop> getAllLoops(String userId, String pods, Long since, Boolean detailed) {
		if(log.isDebugEnabled()) {
			log.debug("getAllLoops(userId=" + userId + ", since=" + since + ")");
		}
		//pods = "1";
		List<Loop> filteredLoops = new ArrayList<Loop>();
		for(Loop loop : loopRepo.getAllLoops(pods, since)) {
			List<String> tags = loop.xml().findAllTags();
			if(!tags.contains("#notification") && !tags.contains("#deleted")) {
				filteredLoops.add(loop);
			}
		}
		return processOutgoingLoops(filteredLoops, null, userId, detailed);	
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

	@Override
	public void deleteAllListsForLoop(String loopId) {
		listRepo.deleteListsForLoop(loopId);
	}
}
