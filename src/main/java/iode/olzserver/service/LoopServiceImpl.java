package iode.olzserver.service;

import iode.olzserver.data.LoopRepository;
import iode.olzserver.data.RefRepository;
import iode.olzserver.domain.Loop;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoopServiceImpl extends AbstractLoopService implements LoopService {
	private static final String NEW_LOOP_CONTENT = "<loop><body><p>New Loop</p></body><tags-box/></loop>";

	private static final Long TEMP_SLICE_ID = 1L; //Slice is hardcoded for now.

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
	public Loop createLoop(Loop loop, String parentLoopId) {
		if(loop.getId() == null) {
			//String loopId = "@" + UUID.randomUUID().toString();
			
			String loopId = "@" + String.valueOf(loopRepo.getAndUpdateSliceNextNumber(1));
			
			loop = loop.copyWithNewId(loopId);
		}

		if(loop.getSliceId() == null) {
			loop = loop.copyWithNewSliceId(Long.valueOf(TEMP_SLICE_ID)); 
		}

		if(parentLoopId != null) {
			loop = loop.xml().addTagToTagsBox(parentLoopId).loopWithUpdatedContent();
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
		
		Loop loop = null;
		try {
			loop = loopRepo.getLoop(loopId, TEMP_SLICE_ID);
		} catch(LoopNotFoundException e) {
			return createLoop(new Loop(loopId, String.format(NEW_LOOP_CONTENT)));	
		}
		
		if(log.isDebugEnabled()) {
			log.debug("loop=" + loop);
		}

		List<Loop> innerLoops = loopRepo.findLoopsContainingTags(new String[]{loopId}, TEMP_SLICE_ID);
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

		Loop dbLoop = loopRepo.getLoop(loop.getId(), TEMP_SLICE_ID);
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
