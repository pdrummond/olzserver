package iode.olzserver.data;

import iode.olzserver.domain.Shortcut;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcShortcutsRepository extends AbstractJdbcRepository implements ShortcutsRepository {
	private final Logger log = Logger.getLogger(getClass());
	
	private static final String SHORTCUTS_SELECT_SQL = "SELECT id, loopId, userId, createdAt, createdBy FROM shortcut ";

	@Override
	public List<Shortcut> getShortcutsForUser(String userId) {
		if(log.isDebugEnabled()) {
			log.debug("getShortcutsForUser(" + userId + ")");
		}

		return jdbc.query(
				SHORTCUTS_SELECT_SQL + "WHERE userId = ?", 
				new String[]{ userId }, new DefaultShortcutRowMapper());
	}
	
	class DefaultShortcutRowMapper implements RowMapper<Shortcut> {
		public Shortcut mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Shortcut(
					rs.getString("id"), 
					rs.getString("loopId"), 
					rs.getString("userId"), 
					toDate(rs.getTimestamp("createdAt")),
					rs.getString("createdBy"));
		}
	}
}
