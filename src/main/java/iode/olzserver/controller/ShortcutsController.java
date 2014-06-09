package iode.olzserver.controller;

import iode.olzserver.data.ShortcutsRepository;
import iode.olzserver.domain.Loop;
import iode.olzserver.domain.Shortcut;
import iode.olzserver.service.LoopService;
import iode.olzserver.service.Transform;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.crypto.dsig.TransformException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ShortcutsController extends AbstractController {
	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private ShortcutsRepository shortcutsRepo;

	@Autowired 
	private LoopService loopService;

	@RequestMapping(value="/shortcuts/{userId}", method=RequestMethod.GET)
	public @ResponseBody List<Shortcut> getShortcuts(@PathVariable("userId") String userId, Principal principal) {
		if(log.isDebugEnabled()) {
			log.debug("getShortcuts(userId= " + userId + ")");
		}
		List<Shortcut> shortcuts = new ArrayList<Shortcut>();
		for(Shortcut s : shortcutsRepo.getShortcutsForUser(userId)) {
			Loop loop = loopService.getLoop(s.getLoopId(), s.getUserId());
			Objects.requireNonNull(loop, String.format("loop " + s.getLoopId() + " is null"));
			loop = loop.xml().removeAllTags().loopWithUpdatedContent();
			loop = convertLoopToHtml(loop);
			s = s.copyWithNewLoop(loop);
			shortcuts.add(s);
		}
		return shortcuts;
	}

	public Loop convertLoopToHtml(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("convertLoopToHtml(loop=" + loop + ")");
		}	
		try {
			loop = loop.copyWithNewContent(Transform.getInstance().transform("loop-shortcut-xml-to-html", loop.getContent()));

		} catch (TransformException e) {
			throw new RuntimeException("Error converting loop to HTML", e);
		}
		return loop;
	}
}
