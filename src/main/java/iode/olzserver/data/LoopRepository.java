package iode.olzserver.data;

import iode.olzserver.domain.Loop;

import java.util.List;

public interface LoopRepository {

	public Loop getLoop(String id, Long sliceId);
	public Loop createLoop(final Loop loop);
	public Loop updateLoop(Loop loop);
	public List<Loop> findInnerLoops(String loopId, Long sliceId);
	public Long getAndUpdateSliceNextNumber(int sliceId);
	public void updateShowInnerLoops(String loopId, Boolean showInnerLoops);
	public void updateFilterText(String loopId, String filterText);	
}