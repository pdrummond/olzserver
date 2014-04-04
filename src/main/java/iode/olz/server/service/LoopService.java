package iode.olz.server.service;

import iode.olz.server.domain.Loop;

public interface LoopService {
	
	Loop getLoop(String uid);	
	Loop createLoop(Loop loop);
	Loop createLoop(Loop loop, String parentUid);
	Loop updateLoop(Loop loop);	
}
