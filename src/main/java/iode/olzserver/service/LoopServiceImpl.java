package iode.olzserver.service;

import iode.olzserver.data.LoopRepository;
import iode.olzserver.data.RefRepository;
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
	public Loop createLoop(Loop loop, String parentSid) {
		if(loop.getSid() == null) {
			loop = loop.copyWithNewSid("#" + UUID.randomUUID().toString());
		}
				
		if(parentSid != null) {			
			Loop parentLoop = getLoop(parentSid);
			List<String> allTags = parentLoop.extractSidTags();			
			loop = loop.xml().ensureTagsExist(allTags);
			
			String parentOwner = parentLoop.extractSidOwner();			
			if(parentOwner != null) {
				loop = loop.copyWithNewSid(loop.getSid() + parentOwner);	
			}
		}
		loop = loopRepo.createLoop(loop);		

		List<String> tags = loop.xml().getTags();

		for(String tag : tags) {
			broadcastHashtagChange(tag, loop);
		}

		return loop;
	}


	@Override
	public Loop getLoop(String sid) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(" + sid + ")");
		}
		
		Loop loop = null;
		try {
			loop = loopRepo.getLoop(sid);
		} catch(LoopNotFoundException e) {
			return createLoop(new Loop(sid, String.format(NEW_LOOP_CONTENT)));	
		}

		if(log.isDebugEnabled()) {
			log.debug("loop=" + loop);
		}

		String owner = loop.extractSidOwner();
		List<String> tags = loop.extractSidTags();		
		if(log.isDebugEnabled()) {
			log.debug("tags=" + tags);
		}
		List<Loop> innerLoops = loopRepo.getInnerLoops(tags, owner);
		if(log.isDebugEnabled()) {
			log.debug("innerLoops=" + innerLoops);
		}
		return loop.copyWithNewInnerLoops(innerLoops);

	}

	@Override
	@Transactional
	public Loop updateLoop(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}

		Loop dbLoop = loopRepo.getLoop(loop.getSid());
		loop = loopRepo.updateLoop(loop);
		List<String> dbTags = dbLoop.xml().getTags();
		List<String> newTags = loop.xml().getTags();

		for(String tag : newTags) {
			if(!dbTags.contains(tag)) {
				broadcastHashtagChange(tag, loop);
			}
		}

		return loop;
	}

	private void broadcastHashtagChange(String tag, Loop loop) {
		this.template.convertAndSend("/topic/hashtag/" + tag, loop.convertLoopToHtml());		
	}
}
