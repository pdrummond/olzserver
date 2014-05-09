package iode.olzserver.data;

import iode.olzserver.domain.Loop;

import java.util.List;

public interface LoopRepository {

	public Loop getLoop(String id);
	public Loop createLoop(final Loop loop);
	public Loop updateLoop(Loop loop);
	public List<Loop> findInnerLoops(String loopId);
	public void updateShowInnerLoops(String loopId, Boolean showInnerLoops);
	public void updateFilterText(String loopId, String filterText);
}