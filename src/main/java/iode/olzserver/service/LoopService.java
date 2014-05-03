package iode.olzserver.service;

import iode.olzserver.domain.Loop;

public interface LoopService {
	
	Loop getLoop(String uid);	
	Loop createLoop(Loop loop);
	Loop createLoop(Loop loop, String parentUid);
	Loop updateLoop(Loop loop);
	void updateFilterText(String loopId, String filterText);
	void updateShowInnerLoops(String loopId, Boolean showInnerLoops);
}
