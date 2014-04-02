package iode.olz.server.data;

import iode.olz.server.domain.Loop;
import iode.olz.server.domain.Ref;

import java.util.List;

public interface RefRepository {

	public Ref getRef(String id);
	public Ref getRef(String loopId, String tagId);
	public List<Ref> getRefs();
	public Ref createRef(Ref ref);
	public Ref createRef(Loop loop, String tagId);
	public boolean refExists(String id);
	public List<Ref> getRefsForLoop(String loopId);
	public void deleteRef(int id);
	
}