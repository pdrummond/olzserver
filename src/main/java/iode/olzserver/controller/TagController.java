package iode.olzserver.controller;

import iode.olzserver.data.TagRepository;
import iode.olzserver.domain.Tag;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@Controller
public class TagController {
	private final Logger log = Logger.getLogger(getClass());

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	private TagRepository tagRepo;

	@RequestMapping("/tags")
	public @ResponseBody List<Tag> getTags() {
		return tagRepo.getTags();
	}

	@RequestMapping(value="/tags/{id}", method=RequestMethod.GET)
	public @ResponseBody Tag getTag(@PathVariable String id) {
		if(log.isDebugEnabled()) {
			log.debug("getTag(" + id + ")");
		}		
		Tag tag = tagRepo.getTag(id);
		/*if(tag.containsHashtag("#!list")) {
			List<Tag> innerTags = tagRepo.findTagsContaining("#" + lid);
			for(Tag l : innerTags) {
				l.add(linkTo(methodOn(TagController.class).getTag(l.getLid())).withSelfRel());
			}
			tag = tag.copyWithNewInnerTags(innerTags);
		}*/
		return tag;
	}

	@RequestMapping(value="/tags", method=RequestMethod.POST) 
	public @ResponseBody Tag createTag(@RequestBody Tag tag) {		
		if(log.isDebugEnabled()) {
			log.debug("createTag(" + tag + ")");
		}
		if(tag.getId() == null) {			
			tag = tag.copyWithNewId(UUID.randomUUID().toString());
		}

		tag = tagRepo.createTag(tag);
		checkForHashtags(tag);
		return tag;
	}

	private void checkForHashtags(Tag tag) {
//		for(String hashtag : tag.findHashtags()) {
//			log.debug("Tag" + tag.getLid() + " contains " + hashtag);
//			this.template.convertAndSend("/topic/hashtag/" + hashtag, tag);	
//		}
	}
	


}
