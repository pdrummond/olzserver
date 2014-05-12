package iode.olzserver.service;

import iode.olzserver.domain.Loop;

public interface LoopService {
	
	Loop getLoop(String loopHandle);	
	Loop createLoop(Loop loop);
	Loop createLoop(Loop loop, String parentUid);
	Loop updateLoop(Loop loop);
	void updateFilterText(String loopHandle, String filterText);
	void updateShowInnerLoops(String loopHandle, Boolean showInnerLoops);
}
