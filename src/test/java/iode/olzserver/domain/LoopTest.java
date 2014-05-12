package iode.olzserver.domain;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class LoopTest {
	
	@Test
	public void testLoopId() {
		Loop loop = new Loop("#boom@pd");
		
		List<String> result = loop.findTitleTagsWithoutSymbols();
		
		assertEquals("Loop id should be boom", "boom", result.get(0));
		assertEquals("Pod name should be pd", "pd", result.get(1));
	}

	@Test
	public void testLoopId2() {
		Loop loop = new Loop("#boom @pd");
		
		List<String> result = loop.findTitleTagsWithoutSymbols();
		
		assertEquals("Loop id should be boom", "boom", result.get(0));
		assertEquals("Pod name should be pd", "pd", result.get(1));
	}
}
