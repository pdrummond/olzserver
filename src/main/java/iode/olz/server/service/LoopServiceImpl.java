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

@Service
public class LoopServiceImpl implements LoopService {
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
	public Loop createLoop(Loop loop, String parentLid) {
		if(loop.getId() == null) {			
			loop = loop.copyWithNewId(UUID.randomUUID().toString());
		}
		if(parentLid != null) {
			Loop parentLoop = getLoop(parentLid);
			List<String> allTags = parentLoop.xml().getUsertags();
			allTags.add(parentLid);
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
	public Loop getLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(" + loopId + ")");
		}
		
		Loop loop = loopRepo.getLoop(loopId);
		if(log.isDebugEnabled()) {
			log.debug("loop=" + loop);
		}
		if(loop == null) {
			loop = createLoop(new Loop(loopId));
			return loop;
		} else {
			XmlLoop xmlLoop = new XmlLoop(loop);
			List<String> usertags = xmlLoop.getUsertags();			
			if(log.isDebugEnabled()) {
				log.debug("usertags=" + usertags);
			}
			List<Loop> innerLoops = loopRepo.getInnerLoops(loopId, usertags);
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
		Loop historyLoop = loopRepo.getLoop(loop.getId());
		loopRepo.changeLoopId(historyLoop, historyLoop.getId() + "_rev_" + UUID.randomUUID().toString());
		loop = createLoop(loop);
		
		List<String> historyTags = historyLoop.xml().getTags();
		List<String> newTags = loop.xml().getTags();
		
 		for(String tag : newTags) {
			if(!historyTags.contains(tag)) {
				broadcastHashtagChange(tag, loop);
			}
		}
		
		return loop;
	}

	@Override
	public List<Loop> getLoops() {
		return loopRepo.getLoops();
	}
	
	private void broadcastHashtagChange(String tag, Loop loop) {
		this.template.convertAndSend("/topic/hashtag/" + tag, loop.convertLoopToHtml());		
	}
}
