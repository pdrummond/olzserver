package iode.olzserver.data;

import iode.olzserver.domain.Loop;
import iode.olzserver.domain.Ref;

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