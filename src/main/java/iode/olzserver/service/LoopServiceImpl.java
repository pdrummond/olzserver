package iode.olzserver.service;

import iode.olzserver.data.ListRepository;
import iode.olzserver.data.LoopRepository;
import iode.olzserver.domain.Loop;
import iode.olzserver.domain.LoopList;
import iode.olzserver.domain.User;
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
	private LoopRepository loopRepo;

	@Autowired
	private ListRepository listRepo;

	@Override
	public Loop getLoop(String loopId, String userId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId = " + loopId + ")");
		}
		return processOutgoingLoop(loopRepo.getLoop(loopId, 1L), null, userId, true);
	}

	@Override
	public List<Loop> findLoopsByQuery(String query, Long since, Boolean detailed, String parentLoopId, String userId) {
		if(log.isDebugEnabled()) {
			log.debug("findLoopsByQuery(query = " + query + ", since=" + since + ")");
		}
		List<Loop> loops = processOutgoingLoops(loopRepo.findLoopsByQuery(query, 1L, since), parentLoopId, userId, detailed);
		return loops;
	}

	private Loop processIncomingLoop(Loop loop, String userId) {
		if(loop.isIncomingProcessingDone()) {
			return loop;
		}

		loop = loop.copyWithNewContent(new HtmlifyTags(loop.getContent()).execute());

		String owner = loop.xml().findOwnerTag_();
		List<String> userTags = loop.xml().findUserTags_();
		for(String userTag : userTags) {		
			User user = userService.getUser(userTag);
			if(user != null) {
				Loop notificationLoop = Loop.createWithContent(String.format("@%s has mentioned you in a loop", owner), null, "@!" + userTag + " #notification").incomingProcessingDone();				
				createLoop(notificationLoop, userId); 
			}
		}

		return loop.incomingProcessingDone();
	}

	private List<Loop> processOutgoingLoops(List<Loop> loops, String parentLoopId, String userId, Boolean detailed) {
		List<Loop> processedLoops = new ArrayList<Loop>();
		for(Loop loop : loops) {
			processedLoops.add(processOutgoingLoop(loop, parentLoopId, userId, detailed));
		}
		return Lists.newArrayList(Iterables.filter(processedLoops, Predicates.notNull()));
	}

	private Loop processOutgoingLoop(Loop loop, String parentLoopId, String userId, Boolean detailed) {
		boolean loopOk = false;
		loop = loop.copyWithNewLists(listRepo.getListsForLoop(loop.getId()));
		if(parentLoopId == null || !loop.getId().equals(parentLoopId)) { //if parentLoopId, then only include loop if it's not parent loop.
			String owner = loop.xml().findOwnerTag_();
			if(owner != null) {
				loop = loop.copyWithNewOwner(getLoopOwner(loop));
			}

			if(userId != null && owner != null) {
				List<String> userTags = loop.xml().findUserTags_();
				if(userId.equals(owner) || userTags.contains(userId)) {
					loopOk = true;
				}
			} else {		
				loopOk = true;
			}
		}

		if(loopOk) {
			ArrayList<LoopList> lists = new ArrayList<LoopList>();
			for(LoopList list : loop.getLists()) {
				if(detailed) {
					List<Loop> listLoops = loopRepo.findLoopsByQuery(list.getQuery(), 1L, null);
					listLoops.remove(loop); //the main loop cannot be included in the lists.
					List<Loop> newListLoops = new ArrayList<Loop>();
					for(Loop listLoop: listLoops) {
						String owner = loop.xml().findOwnerTag_();
						if(owner != null) {
							newListLoops.add(listLoop.copyWithNewOwner(getLoopOwner(loop)));
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

	private User getLoopOwner(Loop loop) {
		String ownerName = loop.xml().findOwnerTag_();
		User owner = null;

		if(ownerName != null) {
			owner = userService.getUser(ownerName);
			if(owner != null) {
				String hash = MD5Util.md5Hex(owner.getEmail().toLowerCase());
				owner = owner.copyWithNewImageUrl(String.format("http://www.gravatar.com/avatar/%s?s=40", hash));
			}
		} 
		return owner;
	}

	@Override
	public Loop createLoop(Loop loop, String userId) {
		if(loop.getId() == null) {
			String loopId = "#" + UUID.randomUUID().toString();//String.valueOf(podRepo.getAndUpdatePodNextNumber(pod.getId()));
			loop = loop.copyWithNewId(loopId);
		}
		loop = processIncomingLoop(loop, userId);

		//If loop has some hashtags, cool.  But if not, add the UID
		//to give it some identity.
//		List<String> hashTags = loop.xml().findHashTags();
//		if(hashTags.isEmpty()) {
//			loop = loop.withTagAddedToFooter(loop.getId().substring(0, 8), Loop.HASHTAG);
//		}
		//loop = loop.withTagAddedToFooter(loop.getId().substring(0, 5), Loop.HASHTAG);

		loop = loopRepo.createLoop(loop);

		List<String> loopTags = loop.xml().findAllTags();
		String query = StringUtils.join(loopTags, ' ') + " #comment";
		if(!StringUtils.isEmpty(query)) {
			LoopList relatedLoopsList = new LoopList(UUID.randomUUID().toString(), loop.getId(), "Comments", query, "createdAt", "descending", new Date(), loop.getCreatedBy()); 
			List<LoopList> lists = new ArrayList<>();		
			lists.add(listRepo.createList(relatedLoopsList));		
			loop = loop.copyWithNewLists(lists);

			/*List<String> loopRefs = loop.findTags();

			for(String loopRef : loopRefs) {
				broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
			}*/
		}
		//broadcastLoopChange(pod.getName(), loop, LoopStatus.ADDED); //broadcast change for pod.

		return processOutgoingLoop(loop, null, userId, true);
	}

	@Override
	@Transactional
	public Loop updateLoop(Loop loop, String userId) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}
		loop = loopRepo.updateLoop(processIncomingLoop(loop, userId));

		/*Loop dbLoop = loopRepo.getLoop(loop.getId(), 1L);
		List<String> dbLoopRefs = dbLoop.findTags();
		List<String> newLoopRefs = loop.findTags();

		for(String loopRef : newLoopRefs) {
			if(!dbLoopRefs.contains(loopRef)) {
				broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
			}
		}*/

		return processOutgoingLoop(loop, null, userId, true);
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
	public List<Loop> getAllLoops(String userId, Long since, Boolean detailed) {
		if(log.isDebugEnabled()) {
			log.debug("getAllLoops(userId=" + userId + ", since=" + since + ")");
		}
		List<Loop> filteredLoops = new ArrayList<Loop>();
		for(Loop loop : loopRepo.getAllLoops(since)) {
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
