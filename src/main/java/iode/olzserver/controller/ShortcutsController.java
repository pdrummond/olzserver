package iode.olzserver.controller;

import iode.olzserver.data.ShortcutsRepository;
import iode.olzserver.domain.Shortcut;

import java.security.Principal;
import java.util.List;

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

	@RequestMapping(value="/shortcuts/{userId}", method=RequestMethod.GET)
	public @ResponseBody List<Shortcut> getShortcuts(@PathVariable("userId") String userId, Principal principal) {
		if(log.isDebugEnabled()) {
			log.debug("getShortcuts(userId= " + userId + ")");
		}
		return shortcutsRepo.getShortcutsForUser(userId);
	}
}
