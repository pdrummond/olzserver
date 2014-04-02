package iode.olzserver.xml.utils;

import static org.junit.Assert.assertEquals;
import iode.olz.server.domain.Loop;
import iode.olz.server.service.UpdateTags;
import iode.olz.server.xml.utils.XmlLoop;

import java.util.List;

import org.junit.Test;

public class XmlEntryTest {
	
	@Test
	public void testGetTags() {
		Loop loop = new Loop("test", "<loop><tag type = 'hashtag'>loop1</tag> and <tag type='hashtag'>loop2</tag>. This <tag type='usertag'>pd</tag> is a usertag.</loop>");
		loop = new UpdateTags().execute(loop);		
		XmlLoop xmlLoop = new XmlLoop(loop);
		List<String> usertags = xmlLoop.getUsertags();
		List<String> hashtags = xmlLoop.getHashtags();
		
		assertEquals("There should be 1 usertag", 1, usertags.size());
		assertEquals("There should be 2 hashtags", 2, hashtags.size());
	}
	
}
