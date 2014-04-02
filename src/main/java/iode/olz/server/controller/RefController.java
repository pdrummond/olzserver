package iode.olz.server.controller;

import iode.olz.server.data.RefRepository;
import iode.olz.server.domain.Ref;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RefController {
	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private RefRepository refRepo;

	@RequestMapping("/refs")
	public @ResponseBody List<Ref> getRefs() {
		return refRepo.getRefs();
	}

	@RequestMapping(value="/refs/loop/{loopId}", method=RequestMethod.GET)
	public @ResponseBody List<Ref> getRefsForLoop(@PathVariable String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getRefsForLoop(" + loopId + ")");
		}		
		return refRepo.getRefsForLoop(loopId);
	}

	@RequestMapping(value="/refs/{id}", method=RequestMethod.GET)
	public @ResponseBody Ref getRef(@PathVariable String id) {
		if(log.isDebugEnabled()) {
			log.debug("getRef(" + id + ")");
		}		
		Ref ref = refRepo.getRef(id);
		return ref;
	}

	@RequestMapping(value="/refs", method=RequestMethod.POST) 
	public @ResponseBody Ref createRef(@RequestBody Ref ref) {		
		if(log.isDebugEnabled()) {
			log.debug("createRef(" + ref + ")");
		}
		ref = refRepo.createRef(ref);
		return ref;
	}
	
}
