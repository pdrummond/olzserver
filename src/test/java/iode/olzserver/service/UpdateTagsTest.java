package iode.olzserver.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import iode.olzserver.domain.Loop;
import iode.olzserver.service.UpdateTags;
import iode.olzserver.xml.utils.XmlLoop;

import org.junit.Test;

public class UpdateTagsTest {
	
	@Test
	public void testHashtagWrapping() {
		Loop loop = new Loop("test", "<div class='loop'>#loop1 and #loop2. Done.</div>");
		loop = new UpdateTags().execute(loop);		
		XmlLoop xmlLoop = new XmlLoop(loop);		
		assertEquals("There should be 2 hashtag elements", 2, xmlLoop.evaluateAndGetCount("//span[@class='hashtag']"));
		assertFalse("There should no '#' characters", loop.getContent().contains("#"));
	}
	
	@Test
	public void testHashtagWrapping2() {
		Loop loop = new Loop("test", "<div class='loop'>#loop1 and #loop2 and <span class='hashtag'>loop3</span>. Done.</div>");
		loop = new UpdateTags().execute(loop);		
		XmlLoop xmlLoop = new XmlLoop(loop);
		System.out.println(xmlLoop.toString());
		assertEquals("There should be 3 hashtag elements", 3, xmlLoop.evaluateAndGetCount("//span[@class='hashtag']"));
		assertFalse("There should no '#' characters", loop.getContent().contains("#"));
	}

}
