package iode.olzserver.domain;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class LoopTest {
	
	@Test
	public void testLoopId() {
		Loop loop = new Loop("#boom-1@pd/pd2.pd3*not-a-tag");
		
		List<String> result = loop.findTitleTagsWithoutSymbols();
		
		assertEquals("There should be 4 tags", 4, result.size());
		assertEquals("boom-1", result.get(0));
		assertEquals("pd", result.get(1));
		assertEquals("pd2", result.get(2));
		assertEquals("pd3", result.get(3));
	}

	@Test
	public void testLoopId2() {
		Loop loop = new Loop("#boom @pd");
		
		List<String> result = loop.findTitleTagsWithoutSymbols();
		
		assertEquals("Loop id should be boom", "boom", result.get(0));
		assertEquals("Pod name should be pd", "pd", result.get(1));
	}
}
