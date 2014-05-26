package iode.olzserver.service;

import iode.olzserver.domain.Loop;
import iode.olzserver.domain.LoopList;

import java.util.List;

public interface LoopService {
	Loop getLoop(String loopHandle);	
	List<Loop> findLoopsByQuery(String query, Long since, String parentLoopId, String userId);
	List<Loop> getAllLoops(String userId, Long since);
	Loop updateLoop(Loop loop, String userId);
	Loop createLoop(Loop loop, String userId);
	void updateFilterText(String loopHandle, String filterText);
	void updateShowInnerLoops(String loopHandle, Boolean showInnerLoops);
	LoopList createList(LoopList list);
}
