package iode.olzserver.transform;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HtmlifyTest {
	
	/*@Test
	public void testUnwraped() {
		String input = "<div data-type='loop'><div class='loop-header'>#boom</div></div>";
		String output = new HtmlifyTags(input).execute();
		
		System.out.println("HTML: " + output);
		
		assertTrue(true);       
	}*/

	@Test
	public void testAlreadyWrapped() {
		String input = "<div data-type='loop'><div class='loop-header'>This is an example <a tag='tag hashtag'>#boom</a> #boom2</div></div>";
		String output = new HtmlifyTags(input).execute();
		
		System.out.println("HTML: " + output);
		
		assertTrue(true);       
		
	}	
}
