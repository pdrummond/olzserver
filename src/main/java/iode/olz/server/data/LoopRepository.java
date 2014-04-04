package iode.olz.server.data;

import iode.olz.server.domain.Loop;

import java.util.List;

public interface LoopRepository {

	public Loop getLoop(String id);
	public Loop createLoop(final Loop loop);
	public List<Loop> getInnerLoops(String lid, List<String> usertags);
	public Loop updateLoop(Loop loop);
	
}