package iode.olzserver.controller;

import iode.olzserver.domain.Loop;
import iode.olzserver.service.LoopService;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoopController {
	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private LoopService loopService;

	@RequestMapping(value="/loops/{loopId}", method=RequestMethod.GET)
	public @ResponseBody Loop getLoop(@PathVariable String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(" + loopId + ")");
		}		
		Loop loop = loopService.getLoop(loopId);
		List<Loop> innerLoops = new ArrayList<Loop>();
		for(Loop innerLoop : loop.getLoops()) {
			innerLoops.add(innerLoop.convertLoopToHtml());
		}
		return loop.copyWithNewInnerLoops(innerLoops).convertLoopToHtml();
		
	}

	@RequestMapping(value="/loops", method=RequestMethod.POST) 
	public @ResponseBody Loop createLoop(@RequestBody Loop loop, @RequestParam(value="parentSid") String parentSid) {		
		if(log.isDebugEnabled()) {
			log.debug("createLoop(loop=" + loop + ", parentSid=" + String.valueOf(parentSid) + ")");
		}		
		loop = loopService.createLoop(loop.convertLoopToXml(), parentSid);
		return loop.convertLoopToHtml();
	}
	
	@RequestMapping(value="/loops/{sid}", method=RequestMethod.PUT) 
	public @ResponseBody Loop updateLoop(@RequestBody Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}
		return loopService.updateLoop(loop.convertLoopToXml()).convertLoopToHtml();
	}
	
	@RequestMapping(value="/admin/resetDb", method=RequestMethod.GET)
	public void resetDb() {
		if(log.isDebugEnabled()) {
			log.debug("resetDb()");
		}
		loopService.resetDb();
	}
}
