package iode.olzserver.data;

import iode.olzserver.domain.Loop;

import java.util.List;

public interface LoopRepository {

	public Loop getLoop(String sid);
	public Loop createLoop(final Loop loop);
	public List<Loop> getInnerLoops(List<String> tags, String owner);
	public Loop updateLoop(Loop loop);
	public void resetDb();
	public List<Loop> findLoopsContainingTags(String[] loopTags);	
}