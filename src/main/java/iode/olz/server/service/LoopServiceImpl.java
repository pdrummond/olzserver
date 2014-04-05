package iode.olz.server.service;

import iode.olz.server.data.LoopRepository;
import iode.olz.server.data.RefRepository;
import iode.olz.server.domain.Loop;
import iode.olz.server.xml.utils.XmlLoop;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableSet;

@Service
public class LoopServiceImpl implements LoopService {
	private static final String NEW_LOOP_CONTENT = "<loop><body><p></p></body><tags-box/></loop>";

	private final Logger log = Logger.getLogger(getClass());
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@Autowired
	private LoopRepository loopRepo;
	
	@Autowired
	private RefRepository refRepo;

	@Override
	public Loop createLoop(Loop loop) {
		return createLoop(loop, null);
	}
	@Override
	public Loop createLoop(Loop loop, String parentUid) {
		if(loop.getLid() == null) {
			loop = loop.copyWithNewLid(UUID.randomUUID().toString());
		}
		if(parentUid != null) {
			Loop parentLoop = getLoop(parentUid);
			List<String> allTags = parentLoop.xml().getUsertags();
			allTags.add(parentLoop.getLid());		
			allTags = ImmutableSet.copyOf(allTags).asList(); //ensure no duplicates
			loop = loop.xml().ensureTagsExist(allTags);
		}
		loop = loopRepo.createLoop(loop);		
		
		List<String> tags = loop.xml().getTags();
		
 		for(String tag : tags) {
 			broadcastHashtagChange(tag, loop);
		}
		
		return loop;
	}

	
	@Override
	public Loop getLoop(String uid) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(" + uid + ")");
		}
		
		Loop loop = loopRepo.getLoop(uid);
		if(log.isDebugEnabled()) {
			log.debug("loop=" + loop);
		}
		if(loop == null) {
			loop = createLoop(new Loop(uid, String.format(NEW_LOOP_CONTENT)));
			return loop;
		} else {
			XmlLoop xmlLoop = new XmlLoop(loop);
			List<String> tags = xmlLoop.getUsertags();
			tags.add(loop.getLid());
			tags = ImmutableSet.copyOf(tags).asList(); //ensure no duplicates
			if(log.isDebugEnabled()) {
				log.debug("tags=" + tags);
			}
			List<Loop> innerLoops = loopRepo.getInnerLoops(tags);
			if(log.isDebugEnabled()) {
				log.debug("innerLoops=" + innerLoops);
			}
			return loop.copyWithNewInnerLoops(innerLoops);
		}
	}

	@Override
	@Transactional
	public Loop updateLoop(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}
		
		Loop dbLoop = loopRepo.getLoop(loop.getUid());
		loop = loopRepo.updateLoop(loop);
		List<String> historyTags = dbLoop.xml().getTags();
		List<String> newTags = loop.xml().getTags();
		
 		for(String tag : newTags) {
			if(!historyTags.contains(tag)) {
				broadcastHashtagChange(tag, loop);
			}
		}
		
		return loop;
	}

	private void broadcastHashtagChange(String tag, Loop loop) {
		this.template.convertAndSend("/topic/hashtag/" + tag, loop.convertLoopToHtml());		
	}
}
