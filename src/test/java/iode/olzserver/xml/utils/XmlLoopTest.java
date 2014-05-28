package iode.olzserver.xml.utils;

import static org.junit.Assert.assertEquals;
import iode.olzserver.domain.Loop;
import iode.olzserver.service.UpdateTags;
import iode.olzserver.xml.utils.XmlLoop;

import java.util.List;

import org.junit.Test;

public class XmlLoopTest {
	
	@Test
	public void testGetLoopRefs() {
		Loop loop = new Loop("test", "<loop><body><hashtag>loop1</hashtag> and <hashtag>loop2</hashtag>.  This is a loop-ref: <loop-ref>@pd</loop-ref></body></loop>");
		loop = new UpdateTags().execute(loop);		
		XmlLoop xmlLoop = new XmlLoop(loop);
		List<String> usertags = xmlLoop.getLoopRefs();
		
		assertEquals("There should be 1 usertag", 1, usertags.size());
	}

	@Test
	public void testFindAllTags() {
		Loop loop = new Loop("test", "<div class='loop-header'><a class='tag hashtag'>#hashtag1</a> and <a class='tag ownertag'>@!pd</a>.</div><div class='loop-footer'></div>");
		XmlLoop xmlLoop = new XmlLoop(loop);
		List<String> tags = xmlLoop.findAllTags();
		assertEquals("There should be 2 tags", 2, tags.size());
		assertEquals("Tag 1 should be #hashtag1", "#hashtag1", tags.get(0));
		assertEquals("Tag 2 should be @!pd", "@!pd", tags.get(1));
	}
	
	@Test
	public void testFindAllTags_() {
		Loop loop = new Loop("test", "<div class='loop-header'><a class='tag hashtag'>#hashtag1</a> and <a class='tag ownertag'>@!pd</a>.</div><div class='loop-footer'></div>");
		XmlLoop xmlLoop = new XmlLoop(loop);
		List<String> tags = xmlLoop.findAllTags_();
		assertEquals("There should be 2 tags", 2, tags.size());
		assertEquals("Tag 1 should be hashtag1", "hashtag1", tags.get(0));
		assertEquals("Tag 2 should be pd", "pd", tags.get(1));
	}
	
	@Test
	public void testFindUserTags() {
		Loop loop = new Loop("test", "<div class='loop-header'><a class='tag hashtag'>#hashtag1</a> and <a class='tag usertag'>@pd</a>.</div><div class='loop-footer'></div>");
		XmlLoop xmlLoop = new XmlLoop(loop);
		List<String> tags = xmlLoop.findUserTags();
		assertEquals("There should be 1 tag", 1, tags.size());
		assertEquals("Tag 1 should be @pd", "@pd", tags.get(0));
	}
	
	@Test
	public void testFindUserTags_() {
		Loop loop = new Loop("test", "<div class='loop-header'><a class='tag hashtag'>#hashtag1</a> and <a class='tag usertag'>@pd</a>.</div><div class='loop-footer'></div>");
		XmlLoop xmlLoop = new XmlLoop(loop);
		List<String> tags = xmlLoop.findUserTags_();
		assertEquals("There should be 1 tag", 1, tags.size());
		assertEquals("Tag 1 should be pd", "pd", tags.get(0));
	}
	
	/*@Test
	public void testFindOwnerTag() {
		Loop loop = new Loop("test", "<div class='loop-header'><a class='tag hashtag'>#hashtag1</a> and <a class='tag ownertag'>@!pd</a>.</div><div class='loop-footer'></div>");
		XmlLoop xmlLoop = new XmlLoop(loop);
		String tag = xmlLoop.findOwnerTag();
		assertEquals("Tag should be @!pd", "@!pd", tag);
	}
	
	@Test
	public void testFindOwnerTag_() {
		Loop loop = new Loop("test", "<div class='loop-header'><a class='tag hashtag'>#hashtag1</a> and <a class='tag ownertag'>@!pd</a>.</div><div class='loop-footer'></div>");
		XmlLoop xmlLoop = new XmlLoop(loop);
		String tag = xmlLoop.findOwnerTag_();
		assertEquals("Tag should be pd", "pd", tag);
	}*/
	
	@Test
	public void testAddTag() {
		Loop loop = new Loop("test", "<loop><body><p>Para 1</p><p>Para 2</p></body></loop>");
		XmlLoop xmlLoop = new XmlLoop(loop);
		//xmlLoop.addTag("@pd");
		System.out.println(xmlLoop);
		assertEquals("There should be 2 children of loop body", 2, xmlLoop.childCount("/loop/body"));
		assertEquals("There should be one @pd loop-ref in last paragraph", 1, xmlLoop.evaluateAndGetCount("/loop/body/p[2]/loop-ref"));
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
