package iode.olz.server.data;

import iode.olz.server.domain.Loop;

import java.util.List;

public interface LoopRepository {

	public Loop getLoop(String id);
	public List<Loop> getLoops();
	public Loop createLoop(final Loop loop);
	public boolean loopExists(String lid);
	public List<Loop> getInnerLoops(String lid, List<String> usertags);
	public Loop changeLoopId(Loop loop, String newLoopId);
	public Loop changeLoop(Loop loop, String newLoopId, String content);
	
}