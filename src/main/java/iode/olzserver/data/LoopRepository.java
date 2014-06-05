package iode.olzserver.data;

import iode.olzserver.domain.Loop;
import iode.olzserver.domain.Pod;

import java.util.List;

public interface LoopRepository {

	public Loop getLoop(String loopId, String pods, Long podId);
	public Loop createLoop(final Loop loop);
	public Loop updateLoop(Loop loop);
	public List<Loop> findLoopsByQuery(String query, String pods, Long podId, Long since);
	public void updateShowInnerLoops(String loopId, Long podId, Boolean showInnerLoops);
	public void updateFilterText(String loopId, Long podId, String filterText);
	public List<Loop> findAllLoopsInPod(Pod pod);
	public List<Loop> getAllLoops(String pods, Long since);
	public Loop findLoopByContents(String content);
}