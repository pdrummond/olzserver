package iode.olz.server.service;

import iode.olz.server.data.LoopRepository;
import iode.olz.server.data.RefRepository;
import iode.olz.server.domain.Loop;
import iode.olz.server.domain.Ref;
import iode.olz.server.xml.utils.XmlLoop;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jdom2.Element;
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
		if(loop.getId() == null) {			
			loop = loop.copyWithNewId(UUID.randomUUID().toString());
		}
		loop = loopRepo.createLoop(loop);		
		//loop = updateTags(loop);
		return loop;
	}

	@Override
	public Loop getLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(" + loopId + ")");
		}
		
		loopId = "#" + loopId;
		
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
		/*Loop historyLoop = */loopRepo.changeLoopId(loop, loop.getId() + "_rev_" + UUID.randomUUID().toString());
		Loop newLoop = createLoop(loop);
		//refRepo.createRef(historyLoop, "history");  
		//refRepo.createRef(historyLoop, loop.getId());
		return newLoop;
	}

	@Override
	public List<Loop> getLoops() {
		return loopRepo.getLoops();
	}
	
	private Loop updateTags(Loop loop) {
		//Make sure all '#' are replaced with hashtags tags	
		loop = new UpdateTags().execute(loop);
		log.debug("BOOM");
		
		List<Ref> refs = refRepo.getRefsForLoop(loop.getId());
		
		//Get a list of all hashtags.
		List<Element> elements = new XmlLoop(loop).evaluate("//span[@class='hashtag']");
		for(Element element : elements) {
			String hashtagName = element.getText();
			Ref ref = refRepo.getRef(loop.getId(), hashtagName);
			if(ref == null) {
				refRepo.createRef(new Ref(loop.getId(), hashtagName));
				this.template.convertAndSend("/topic/hashtag/" + hashtagName, loop);
			} else {
				refs.remove(ref);
			}
		}
		
		for(Ref ref : refs) {
			refRepo.deleteRef(ref.getId());
		}
		return loop;
	}
}
