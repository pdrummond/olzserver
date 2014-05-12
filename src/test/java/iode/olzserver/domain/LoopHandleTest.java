package iode.olzserver.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LoopHandleTest {
	
	@Test
	public void testPodHandle() {
		LoopHandle handle = new LoopHandle("@pd");
		assertEquals("Loop id should be @pd", "@pd", handle.getLoopId());
		assertEquals("Pod name should be @pd", "@pd", handle.getPodName());
	}
	
	@Test
	public void testPodAndLoopHandle() {
		LoopHandle handle = new LoopHandle("#10@pd");
		assertEquals("Loop id should be #10", "#10", handle.getLoopId());
		assertEquals("Pod name should be @pd", "@pd", handle.getPodName());
	}
	
	@Test
	public void testLoopOnlyHandle() {
		LoopHandle handle = new LoopHandle("#10");
		assertEquals("Loop id should be #10", "#10", handle.getLoopId());
		assertEquals("Pod name should be @iode", "@iode", handle.getPodName());
	}
}
