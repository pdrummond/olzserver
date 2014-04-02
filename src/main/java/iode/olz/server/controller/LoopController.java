package iode.olz.server.controller;

import iode.olz.server.domain.Loop;
import iode.olz.server.service.LoopService;
import iode.olz.server.service.Transform;

import java.util.List;

import javax.xml.crypto.dsig.TransformException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoopController {
	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private LoopService loopService;

	@RequestMapping("/loops")
	public @ResponseBody List<Loop> getLoops() {
		return loopService.getLoops();
	}

	@RequestMapping(value="/loops/{loopId}", method=RequestMethod.GET)
	public @ResponseBody Loop getLoop(@PathVariable String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getLoop(" + loopId + ")");
		}		
		return convertLoopToHtml(loopService.getLoop(loopId));
		
	}

	private Loop convertLoopToHtml(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("convertLoopToHtml(" + loop + ")");
		}	
		try {
			loop = loop.copyWithNewContent(Transform.getInstance().transform("loop-xml-to-html", loop.getContent()));
			if(log.isDebugEnabled()) {
				log.debug("loop=" + loop);
			}	
		} catch (TransformException e) {
			throw new RuntimeException("Error converting loop to HTML", e);
		}
		return loop;
	}

	private Loop convertLoopToXml(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("convertLoopToXml(" + loop + ")");
		}
		try {
			loop = loop.copyWithNewContent(Transform.getInstance().transform("loop-html-to-xml", loop.getContent()));
			if(log.isDebugEnabled()) {
				log.debug("loop=" + loop);
			}
		} catch (TransformException e) {
			throw new RuntimeException("Error converting loop to XML", e);
		}
		return loop;
	}

	@RequestMapping(value="/loops", method=RequestMethod.POST) 
	public @ResponseBody Loop createLoop(@RequestBody Loop loop) {		
		if(log.isDebugEnabled()) {
			log.debug("createLoop(" + loop + ")");
		}
		loop = loopService.createLoop(convertLoopToXml(loop));		
		return loop;
	}
	
	@RequestMapping(value="/loops/{loopId}", method=RequestMethod.PUT) 
	public @ResponseBody Loop updateLoop(@RequestBody Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(" + loop + ")");
		}
		return convertLoopToHtml(loopService.updateLoop(convertLoopToXml(loop)));
	}
}
