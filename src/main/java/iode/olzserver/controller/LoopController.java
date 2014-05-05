package iode.olzserver.controller;

import iode.olzserver.domain.Loop;
import iode.olzserver.service.LoopService;

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
		return loopService.getLoop(loopId).convertLoopToHtml();		
	}
	
	@RequestMapping(value="/loops", method=RequestMethod.POST) 
	public @ResponseBody Loop createLoop(@RequestBody Loop loop, @RequestParam(value="parentLoopId") String parentLoopId) {		
		if(log.isDebugEnabled()) {
			log.debug("createLoop(loop=" + loop + ", parentLoopId=" + String.valueOf(parentLoopId) + ")");
		}		
		loop = loopService.createLoop(loop.convertLoopToXml(), parentLoopId);
		return loop.convertLoopToHtml();
	}
	
	@RequestMapping(value="/loops/{loopId}", method=RequestMethod.PUT) 
	public @ResponseBody Loop updateLoop(@RequestBody Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}		
		return loopService.updateLoop(loop.convertLoopToXml()).convertLoopToHtml();
	}
	
	@RequestMapping(value="/loop/{loopId}", method=RequestMethod.POST)
	public @ResponseBody String updateLoopField(
			@PathVariable("loopId") String loopId,
			@RequestParam(value="filterText", required=false) String filterText,
			@RequestParam(value="showInnerLoops", required=false) Boolean showInnerLoops) {
		if(log.isDebugEnabled()) {
			//log.debug("updateLoopField(loopId=" + loopId + ", filterText = " + String.valueOf(filterText) + ", showInnerLoops=" +  Boolean.valueOf(showInnerLoops) + ")");
		}		
		if(filterText != null) {
			loopService.updateFilterText(loopId, filterText);
		}
		if(showInnerLoops != null) {
			loopService.updateShowInnerLoops(loopId, showInnerLoops);
		}
		return "ok";
	}
	
}
