package iode.olzserver.data;

import iode.olzserver.domain.Loop;

import java.util.List;

public interface LoopRepository {

	public Loop getLoop(String id, Long sliceId);
	public Loop createLoop(final Loop loop);
	//public List<Loop> getInnerLoops(List<String> tags, String owner);
	public Loop updateLoop(Loop loop);
	public List<Loop> findLoopsContainingTags(String[] loopTags, Long sliceId);
	public Long getAndUpdateSliceNextNumber(int sliceId);	
}