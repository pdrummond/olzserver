package iode.olzserver.data;

import iode.olzserver.domain.LoopList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcListRepository extends AbstractJdbcRepository implements ListRepository {
	private final Logger log = Logger.getLogger(getClass());
	
	private static final String LIST_SELECT_SQL = "SELECT id, loopId, name, query, comparator, sortOrder, createdAt, createdBy FROM list ";

	@Override
	public LoopList getList(String listId) {
		if(log.isDebugEnabled()) {
			log.debug("getList(listId=" + listId + ")");
		}

		List<LoopList> lists = jdbc.query(
				LIST_SELECT_SQL + "WHERE id = ?",
				new Object[]{listId}, 
				new DefaultListRowMapper());
		
		if(lists.size() == 1) {
			return lists.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<LoopList> getListsForLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getListsForLoop(" + loopId + ")");
		}

		return jdbc.query(
				LIST_SELECT_SQL + "WHERE loopId = ?", 
				new String[]{ loopId }, new DefaultListRowMapper());
	}
	
	public LoopList createList(final LoopList list) {
		if(log.isDebugEnabled()) {
			log.debug("createList(list=" + list + ")");
		}
		
		jdbc.update(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement("INSERT INTO list(id, loopId, name, query, comparator, sortOrder) values(?, ?, ?, ?, ?, ?)");
						ps.setString(1, list.getId());
						ps.setString(2, list.getLoopId());
						ps.setString(3, list.getName());
						ps.setString(4, list.getQuery());
						ps.setString(5, list.getComparator());
						ps.setString(6, list.getSortOrder());
						return ps;
					}
				});		

		return list;
	}

	@Override
	public void deleteList(Long listId) {
		this.jdbc.update("DELETE FROM list WHERE id = ?", listId);
	}
	
	class DefaultListRowMapper implements RowMapper<LoopList> {
		public LoopList mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new LoopList(
					rs.getString("id"), 
					rs.getString("loopId"), 
					rs.getString("name"), 
					rs.getString("query"),					
					rs.getString("comparator"),					
					rs.getString("sortOrder"),					
					toDate(rs.getTimestamp("createdAt")),
					rs.getString("createdBy"));
		}
	}

	@Override
	public void deleteListsForLoop(String loopId) {
		this.jdbc.update("DELETE FROM list WHERE loopId = ?", loopId);
	}
}
