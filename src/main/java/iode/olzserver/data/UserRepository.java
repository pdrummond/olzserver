package iode.olzserver.data;

import iode.olzserver.domain.User;

public interface UserRepository {

	public User getUser(String userId);
	Long getAndUpdateNextLoopId(String userId);
}