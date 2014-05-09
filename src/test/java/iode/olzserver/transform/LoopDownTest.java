package iode.olzserver.transform;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LoopDownTest {
	
	@Test
	public void testNoLoopRef() {
		String input = "This *is* some loopdown";
		String output = new LoopDown(input).toHtml();
		
		System.out.println("HTML: " + output);
		
		assertEquals("Loopdown should be identical", "This *is* some loopdown", output);       
		
	}

	@Test
	public void testSingleLoopRef() {
		String input = "This *is* some loopdown with a loopref: @c4d6bcca-605a-439c-ac7d-181a0ee3d308. That will be _all_.";
		String output = new LoopDown(input).toHtml();
		
		System.out.println("HTML: " + output);
		
		assertEquals("Loopdown should be converted to HTML", "This *is* some loopdown with a loopref: <a class='loopref' title='@c4d6bcca-605a-439c-ac7d-181a0ee3d308'>@c4d6</a>. That will be _all_.", output);       
		
	}
	
}
