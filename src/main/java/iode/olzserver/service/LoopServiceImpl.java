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
	public Loop getLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId = " + loopId + ")");
		}
		return loopRepo.getLoop(loopId, 1L);
	}

	@Override
	public List<Loop> findLoopsByQuery(String query, Long since, String parentLoopId, String userId) {
		if(log.isDebugEnabled()) {
			log.debug("findLoopsByQuery(query = " + query + ", since=" + since + ")");
		}
		List<Loop> loops = processOutgoingLoops(loopRepo.findLoopsByQuery(query, 1L, since), parentLoopId, userId);
		return loops;
	}
	
	private Loop processIncomingLoop(Loop loop) {
		return loop.copyWithNewContent(new HtmlifyTags(loop.getContent()).execute());
	}

	private List<Loop> processOutgoingLoops(List<Loop> loops, String parentLoopId, String userId) {
		List<Loop> processedLoops = new ArrayList<Loop>();
		for(Loop loop : loops) {
			processedLoops.add(processOutgoingLoop(loop, parentLoopId, userId));
		}
		return Lists.newArrayList(Iterables.filter(processedLoops, Predicates.notNull()));
	}

	private Loop processOutgoingLoop(Loop loop, String parentLoopId, String userId) {
		boolean loopOk = false;
		loop = loop.copyWithNewLists(listRepo.getListsForLoop(loop.getId()));
		if(parentLoopId == null || !loop.getId().equals(parentLoopId)) { //if parentLoopId, then only include loop if it's not parent loop.
			boolean hasOwner = loop.hasOwner();
			if(hasOwner) {
				loop = loop.copyWithNewOwner(getLoopOwner(loop));
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
				List<Loop> listLoops = loopRepo.findLoopsByQuery(list.getQuery(), 1L, null);
				listLoops.remove(loop); //the main loop cannot be included in the lists.
				List<Loop> newListLoops = new ArrayList<Loop>();
				for(Loop listLoop: listLoops) {
					boolean hasOwner = listLoop.hasOwner();
					if(hasOwner) {
						newListLoops.add(listLoop.copyWithNewOwner(getLoopOwner(loop)));
					}
				}
				list = list.copyWithNewLoops(newListLoops);
				lists.add(list);
			}
			loop = loop.copyWithNewLists(lists);
		}
		return loopOk?loop:null;
	}

	private User getLoopOwner(Loop loop) {
		String ownerName = loop.findOwner();
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
		loop = loopRepo.createLoop(processIncomingLoop(loop));

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

		return processOutgoingLoop(loop, null, userId);
	}

	@Override
	@Transactional
	public Loop updateLoop(Loop loop, String userId) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}
		loop = loopRepo.updateLoop(processIncomingLoop(loop));

		Loop dbLoop = loopRepo.getLoop(loop.getId(), 1L);
		List<String> dbLoopRefs = dbLoop.findTags();
		List<String> newLoopRefs = loop.findTags();

		for(String loopRef : newLoopRefs) {
			if(!dbLoopRefs.contains(loopRef)) {
				broadcastLoopChange(loopRef, loop, LoopStatus.ADDED);
			}
		}

		return processOutgoingLoop(loop, null, userId);
	}

	private void broadcastLoopChange(String loopRef, Loop loop, LoopStatus status) {
		this.template.convertAndSend("/topic/loop-changes/" + loopRef, loop.copyWithNewStatus(status));		
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
	public List<Loop> getAllLoops(String userId, Long since) {
		if(log.isDebugEnabled()) {
			log.debug("getAllLoops(userId=" + userId + ", since=" + since + ")");
		}
		List<Loop> loops = processOutgoingLoops(loopRepo.getAllLoops(since), null, userId);
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
