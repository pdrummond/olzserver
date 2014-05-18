package iode.olzserver.service;

import iode.olzserver.domain.Loop;

public interface LoopService {
	Loop getOuterLoop();	
	Loop getLoop(String loopHandle);	
	Loop getLoopByQuery(String query);
	Loop createLoop(Loop loop);
	Loop createLoop(Loop loop, String parentUid);
	Loop updateLoop(Loop loop);
	void updateFilterText(String loopHandle, String filterText);
	void updateShowInnerLoops(String loopHandle, Boolean showInnerLoops);
}
