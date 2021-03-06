package iode.olzserver.controller;

import iode.olzserver.domain.Loop;
import iode.olzserver.service.LoopService;

import java.security.Principal;
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
	public @ResponseBody Loop getLoop(@PathVariable("loopId") String loopId, Principal principal) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId=" + String.valueOf(loopId) + ")");
		}
		return loopService.getLoop(loopId).convertLoopToHtml();
	}
	
	@RequestMapping(value="/loops", method=RequestMethod.GET)
	public @ResponseBody List<Loop> getLoops(@RequestParam(value="query", required=false) String query, @RequestParam(value="showOuterLoop", required=false) Boolean showOuterLoop, Principal principal) {
		if(log.isDebugEnabled()) {
			log.debug("getLoops(query=" + String.valueOf(query) + ")");
		}
		if(query != null) {
			return loopService.findLoopsByQuery(query);
		} else {
			return loopService.getAllLoops();
		}
	}
	
	@RequestMapping(value="/loops", method=RequestMethod.POST) 
	public @ResponseBody Loop createLoop(@RequestBody Loop loop, @RequestParam(value="parentLoopId", required=false) String parentLoopHandle) {		
		if(log.isDebugEnabled()) {
			log.debug("createLoop(loop=" + loop + ", parentLoopId=" + String.valueOf(parentLoopHandle) + ")");
		}		
		loop = loopService.createLoop(loop.convertLoopToMd(), parentLoopHandle);
		return loop.convertLoopToHtml();
	}

	@RequestMapping(value="/loops/{loopId}", method=RequestMethod.PUT) 
	public @ResponseBody Loop updateLoop(@PathVariable("loopId") String loopId, @RequestBody Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}		
		return loopService.updateLoop(loop.convertLoopToMd()).convertLoopToHtml();
	}
	
	@RequestMapping(value="/loop/field", method=RequestMethod.POST)
	public @ResponseBody String updateLoopField(
			@RequestParam(value="loopId", required=true) String loopId,
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
