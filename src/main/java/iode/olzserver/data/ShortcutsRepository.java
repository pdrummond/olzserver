package iode.olzserver.data;

import iode.olzserver.domain.Shortcut;

import java.util.List;

public interface ShortcutsRepository {

	public List<Shortcut> getShortcutsForUser(String userId);
	
}