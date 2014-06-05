package iode.olzserver.service;

import iode.olzserver.domain.Loop;
import iode.olzserver.domain.LoopList;

import java.util.List;

public interface LoopService {
	Loop getLoop(String loopId, String userId, String pods);	
	List<Loop> findLoopsByQuery(String query, String pods, Long since, Boolean detailed, String parentLoopId, String userId);
	List<Loop> getAllLoops(String userId, String pods, Long since, Boolean detailed);
	Loop updateLoop(Loop loop, String userId);
	Loop createLoop(Loop loop, String userId);
	void updateFilterText(String loopHandle, String filterText);
	void updateShowInnerLoops(String loopHandle, Boolean showInnerLoops);
	LoopList createList(LoopList list);
	void deleteAllListsForLoop(String loopId);
}
