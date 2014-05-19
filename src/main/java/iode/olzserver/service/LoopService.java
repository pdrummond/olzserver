package iode.olzserver.service;

import java.util.List;

import iode.olzserver.domain.Loop;

public interface LoopService {
	Loop getLoop(String loopHandle);	
	List<Loop> findLoopsByQuery(String query);
	List<Loop> getAllLoops();
	Loop createLoop(Loop loop);
	Loop createLoop(Loop loop, String parentUid);
	Loop updateLoop(Loop loop);
	void updateFilterText(String loopHandle, String filterText);
	void updateShowInnerLoops(String loopHandle, Boolean showInnerLoops);
}
