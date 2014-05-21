package iode.olzserver.data;

import iode.olzserver.domain.LoopList;

import java.util.List;

public interface ListRepository {

	public LoopList getList(String id);
	public List<LoopList> getListsForLoop(String loopId);
	public LoopList createList(LoopList list);
	public void deleteList(Long listId);
	
}