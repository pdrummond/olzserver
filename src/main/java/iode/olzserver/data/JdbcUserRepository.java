package iode.olzserver.data;

import iode.olzserver.domain.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserRepository extends AbstractJdbcRepository implements UserRepository {
	private final Logger log = Logger.getLogger(getClass());

	public User getUser(String userId) {
		log.debug("getUser(id=" + userId + ")");

		List<User> users = jdbc.query(
				"select userId, email, nextLoopId, createdAt from users where userId = ?",
				new Object[]{userId},
				new RowMapper<User>() {
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						User user = new User(
								rs.getString("userId"), 
								rs.getString("email"),
								rs.getLong("nextLoopId"),
								toDate(rs.getTimestamp("createdAt")));
						return user;
					}
				});
		if(users.size() == 1) {
			return users.get(0);
		} else {
			return null;
		}		
	}
	
	@Override
	public Long getAndUpdateNextLoopId(String userId) {
		log.debug("getUser(id=" + userId + ")");
		Long nextLoopId = jdbc.queryForObject("select nextLoopId from users where userId = ?", new Object[]{userId}, Long.class);
		jdbc.update("update users set nextLoopId = ? where userId = ?", new Object[]{nextLoopId+1, userId});
		return nextLoopId;
	}

}
