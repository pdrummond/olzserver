package iode.olzserver.service;

import iode.olzserver.data.UserRepository;
import iode.olzserver.domain.User;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	private final Logger log = Logger.getLogger(getClass());
	
	@Autowired
	UserRepository userRepo;

	@Override
	public User getUser(String userId) {
		if(log.isDebugEnabled()) {
			log.debug("getUser(userId=" + userId + ")");
		}
		return userRepo.getUser(userId);
	}
	
}
