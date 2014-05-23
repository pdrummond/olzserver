package iode.olzserver.service;

import java.util.List;

import iode.olzserver.domain.Loop;
import iode.olzserver.domain.LoopList;

public interface LoopService {
	Loop getLoop(String loopHandle);	
	List<Loop> findLoopsByQuery(String query, String userId);
	List<Loop> getAllLoops(String userId);
	Loop createLoop(Loop loop);
	Loop createLoop(Loop loop, String parentUid);
	Loop updateLoop(Loop loop);
	void updateFilterText(String loopHandle, String filterText);
	void updateShowInnerLoops(String loopHandle, Boolean showInnerLoops);
	LoopList createList(LoopList list);
}
