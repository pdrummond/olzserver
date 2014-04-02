package iode.olz.server.service;

import java.util.List;

import iode.olz.server.domain.Loop;

public interface LoopService {
	
	Loop createLoop(Loop loop);
	Loop getLoop(String loopId);
	Loop updateLoop(Loop loop);
	List<Loop> getLoops();
}
