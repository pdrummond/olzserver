package iode.olzserver;

import static org.junit.Assert.assertEquals;
import iode.olzserver.Application;
import iode.olzserver.data.LoopRepository;
import iode.olzserver.domain.Loop;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class, loader=SpringApplicationContextLoader.class)
public class ApplicationTests {
	
	@Autowired 
	LoopRepository loopRepo;

	@Test
	@Ignore
	public void contextLoads() {
		List<String> tags = new ArrayList<String>();
		tags.add("journal");
		tags.add("po");
		List<Loop> loops = loopRepo.getInnerLoops(tags, null);
		
		assertEquals("There should be 1 loop", 1, loops.size());		
	}

}
