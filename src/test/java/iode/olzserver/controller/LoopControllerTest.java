package iode.olzserver.controller;

import static org.junit.Assert.assertEquals;
import iode.olzserver.domain.Loop;

import org.junit.Test;

public class LoopControllerTest {
	
	@Test
	public void testFromHtmlToXml() {
		System.out.println("\n\n*** testFromHtmlToXml()\n\n");
		Loop loop = new Loop("<div data-type='loop'><div data-type='loop-header'>Header <p>para</p></div><div data-type='loop-body'>Body #boom</div><div data-type='loop-footer'>Footer</div></div>");
		
		loop = new LoopController().convertLoopToXml(loop);
		
		System.out.println(loop.xml());

		assertEquals("There should be 1 loop tag", 1, loop.xml().evaluateAndGetCount("//loop"));
		assertEquals("There should be 1 loop-header tag", 1, loop.xml().evaluateAndGetCount("//loop-header"));
		assertEquals("There should be 1 loop-body tag", 1, loop.xml().evaluateAndGetCount("//loop-body"));
		assertEquals("There should be 1 loop-footer tag", 1, loop.xml().evaluateAndGetCount("//loop-footer"));
	}
	
	@Test
	public void testFromXmlToHtml() {
		System.out.println("\n\n*** testFromXmlToHtml()\n\n");
		Loop loop = new Loop("<loop><loop-header>Header</loop-header><loop-body>Body #boom</loop-body><loop-footer>Footer</loop-footer></loop>");
		
		loop = new LoopController().convertLoopToHtml(loop);
		
		System.out.println(loop.xml());

		assertEquals("There should be 1 loop tag", 1, loop.xml().evaluateAndGetCount("//div[@data-type='loop']"));
		assertEquals("There should be 1 loop-header tag", 1, loop.xml().evaluateAndGetCount("//div[@data-type='loop-header']"));
		assertEquals("There should be 1 loop-body tag", 1, loop.xml().evaluateAndGetCount("//div[@data-type='loop-body']"));
		assertEquals("There should be 1 loop-footer tag", 1, loop.xml().evaluateAndGetCount("//div[@data-type='loop-footer']"));
	}


}
