package iode.olzserver;

import static org.junit.Assert.assertEquals;
import iode.olz.server.Application;
import iode.olz.server.data.LoopRepository;
import iode.olz.server.domain.Loop;

import java.util.ArrayList;
import java.util.List;

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
	public void contextLoads() {
		List<String> usertags = new ArrayList<String>();
		usertags.add("po");
		List<Loop> loops = loopRepo.getInnerLoops("journal", usertags);
		
		assertEquals("There should be 1 loop", 1, loops.size());		
	}

}
