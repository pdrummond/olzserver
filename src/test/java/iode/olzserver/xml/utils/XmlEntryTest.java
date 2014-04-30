package iode.olzserver.xml.utils;

import static org.junit.Assert.assertEquals;
import iode.olzserver.domain.Loop;
import iode.olzserver.service.UpdateTags;
import iode.olzserver.xml.utils.XmlLoop;

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
	
	/*@Test
	public void testGetSidTags() {
		Loop loop = new Loop("#iode/in-progress/open#closed#in-progress@open_loopz~user9");
		List<String> tags = loop.extractSidTags();
		
		String owner = loop.extractSidOwner();
		
		assertEquals("Owner should be @open_loopz", "@open_loopz", owner);
		assertEquals("There should be 5 loops (last tag - #in-progress is a duplicate so it should be removed)", 5, tags.size());
		assertEquals("Tag 1 should be #iode", "#iode", tags.get(0));
		assertEquals("Tag 2 should be #in-progress", "#in-progress", tags.get(1));
		assertEquals("Tag 3 should be #open", "#open", tags.get(2));
		assertEquals("Tag 4 should be #closed", "#closed", tags.get(3));
		assertEquals("Tag 5 should be @user9", "@user9", tags.get(4));
	}*/
	
}
