package iode.olzserver.controller;

import iode.olzserver.domain.Loop;
import iode.olzserver.domain.LoopList;
import iode.olzserver.domain.User;
import iode.olzserver.service.LoopService;
import iode.olzserver.service.Transform;
import iode.olzserver.service.UserService;
import iode.olzserver.utils.MD5Util;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.dsig.TransformException;

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
public class LoopController extends AbstractLoopController {
	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private LoopService loopService;

	@Autowired
	private UserService userService;

	@RequestMapping(value="/loops/{loopId}", method=RequestMethod.GET)
	public @ResponseBody Loop getLoop(@PathVariable("loopId") String loopId, @RequestParam(value="pods", required=false) String pods, Principal principal) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId=" + String.valueOf(loopId) + ",pods=" + String.valueOf(pods) + ")");
		}
		pods = "1";
		return convertLoopToHtml(loopService.getLoop(loopId, pods, principal.getName()));
	}

	@RequestMapping(value="/loops", method=RequestMethod.GET)
	public @ResponseBody List<Loop> findLoopsByQuery(
			@RequestParam(value="query", required=false) String query, 
			@RequestParam(value="pods", required=false) String pods,
			@RequestParam(value="detail", required=false) Boolean detailed, 
			@RequestParam(value="parentLoopId", required=false) String parentLoopId,
			@RequestParam(value="since", required=false) Long since,
			Principal principal) {
		if(log.isDebugEnabled()) {
			log.debug("findLoopsByQuery(query=" + String.valueOf(query) + ")");
		}
		if(detailed == null) {
			detailed = Boolean.FALSE;
		}
		pods = "1";
		List<Loop> loops = null;
		if(query != null) {
			loops = loopService.findLoopsByQuery(query, pods, since, detailed, parentLoopId, principal.getName());
		} else {
			loops = loopService.getAllLoops(principal.getName(), pods, since, detailed);			
		}
		List<Loop> htmlLoops = new ArrayList<Loop>();
		for(Loop loop : loops) {
			htmlLoops.add(convertLoopToHtml(loop));
		}
		return htmlLoops;
	}

	@RequestMapping(value="/loops", method=RequestMethod.POST) 
	public @ResponseBody Loop createLoop(@RequestBody Loop loop, @RequestParam(value="parentLoopId", required=false) String parentLoopHandle, Principal principal) {		
		if(log.isDebugEnabled()) {
			log.debug("createLoop(loop=" + loop + ", parentLoopId=" + String.valueOf(parentLoopHandle) + ")");
		}
		if(principal != null) {
			loop.copyWithNewCreatedBy(principal.getName());
		}
		loop = loopService.createLoop(convertLoopToXml(loop), principal.getName());
		sleep(5000);
		return convertLoopToHtml(loop);
	}

	private void sleep(int ms) {
		log.debug("SLEEPING...");
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("SLEEP DONE");
	}

	@RequestMapping(value="/loops/{loopId}", method=RequestMethod.PUT) 
	public @ResponseBody Loop updateLoop(@PathVariable("loopId") String loopId, @RequestBody Loop loop, Principal principal) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}		
		if(principal != null) {
			loop.copyWithNewUpdatedBy(principal.getName());
		}
		loop = loopService.updateLoop(convertLoopToXml(loop), principal.getName());
		sleep(5000);
		return convertLoopToHtml(loop);
	}

	@RequestMapping(value="/loop/field", method=RequestMethod.POST)
	public @ResponseBody String updateLoopField(
			@RequestParam(value="loopId", required=true) String loopId,
			@RequestParam(value="filterText", required=false) String filterText,
			@RequestParam(value="showInnerLoops", required=false) Boolean showInnerLoops) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoopField(loopId=" + String.valueOf(loopId) + ", filterText = " + String.valueOf(filterText) + ", showInnerLoops=" +  Boolean.valueOf(showInnerLoops) + ")");
		}		
		if(filterText != null) {
			loopService.updateFilterText(loopId, filterText);
		}
		if(showInnerLoops != null) {
			loopService.updateShowInnerLoops(loopId, showInnerLoops);
		}
		return "ok";
	}
	
	@RequestMapping(value="/loops/{loopId}/lists", method=RequestMethod.DELETE)
	public @ResponseBody String deleteAllListsForLoop(@PathVariable("loopId") String loopId, Principal principal) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(loopId=" + String.valueOf(loopId) + ")");
		}
		loopService.deleteAllListsForLoop(loopId);
		return "ok";
	}


	@RequestMapping(value="/lists", method=RequestMethod.POST) 
	public @ResponseBody LoopList createList(@RequestBody LoopList list) {		
		if(log.isDebugEnabled()) {
			log.debug("createList(loopList=" + list + ")");
		}		
		return loopService.createList(list);
	}

	@RequestMapping(value="/user/current", method = RequestMethod.GET)   
	public @ResponseBody User showResults(Principal principal) {
		User user = userService.getUser(principal.getName());
		if(user != null) {
			String hash = MD5Util.md5Hex(user.getEmail().toLowerCase());
			user = user.copyWithNewImageUrl(String.format("http://www.gravatar.com/avatar/%s?s=40", hash));
		}
		return user;
	}

	public Loop convertLoopToHtml(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("convertLoopToHtml(loop=" + loop + ")");
		}	
		try {
			if(loop.xml().containsTag("#notification")) {
				loop = loop.copyWithNewContent(Transform.getInstance().transform("loop-notification-xml-to-html", loop.getContent()));
			} else {
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("userService", userService);
				loop = loop.copyWithNewContent(Transform.getInstance().transform("loop-xml-to-html", loop.getContent(), model));
			}
			if(log.isDebugEnabled()) {
				log.debug("HTML content: " + loop.getContent());
			}

		} catch (TransformException e) {
			throw new RuntimeException("Error converting loop to HTML", e);
		}
		
		ArrayList<LoopList> newLists = new ArrayList<LoopList>();			
		for(LoopList list : loop.getLists()) {
			List<Loop> newListLoops = new ArrayList<Loop>();
			for(Loop l : list.getLoops()) {
				newListLoops.add(convertLoopToHtml(l));
			}
			newLists.add(list.copyWithNewLoops(newListLoops));
		}			
		return loop.copyWithNewLists(newLists);
	}

	public Loop convertLoopToXml(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("convertLoopToXml(loop=" + loop + ")");
		}
		try {
			loop = loop.copyWithNewContent(Transform.getInstance().transform("loop-html-to-xml", loop.getContent()));
			if(log.isDebugEnabled()) {
				log.debug("XML content: " + loop.getContent());
			}
		} catch (TransformException e) {
			throw new RuntimeException("Error converting loop to XML", e);
		}
		return loop;
	}
}
