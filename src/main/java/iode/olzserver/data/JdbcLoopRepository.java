package iode.olzserver.data;

import iode.olzserver.domain.Loop;
import iode.olzserver.service.LoopNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Repository
public class JdbcLoopRepository extends AbstractJdbcRepository implements LoopRepository {
	private final Logger log = Logger.getLogger(getClass());

	public Loop getLoop(String id) {
		log.debug("getLoop(id=" + id + ")");

		List<Loop> loops = jdbc.query(
				"SELECT id, content ::text, createdAt, createdBy FROM loop WHERE id = ?",
				new Object[]{id},
				new DefaultLoopRowMapper());
		if(loops.size() == 1) {
			return loops.get(0);
		} else {
			throw new LoopNotFoundException("No loop found with id " + id);
		}
	}

	@Override
	public List<Loop> getInnerLoops(final List<String> parentTags, final String owner) {
		if(log.isDebugEnabled()) {
			log.debug("getInnerLoops(parentTags=" + parentTags + ")");
		}
		List<Loop> loops = jdbc.query(
				"SELECT "
						+  "sid,"
						+  "(xpath('//tag/text()', content))::text as tags "
						+ "FROM loop " 
						+ "ORDER BY updated_at DESC",
						new RowMapper<Loop>() {
							public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {						
								String sid = rs.getString("sid");
								String[] tags = rs.getString("tags").replace("{", "").replace("}", "").split(",");

								if(Arrays.asList(tags).containsAll(parentTags)) {
									if(owner != null) {
										//if(Loop.isSidOwner(sid, owner)) {
											return getLoop(sid);
										//}
									} else {
										return getLoop(sid);
									}
								}
								return null;
							}
						});		
		return Lists.newArrayList(Iterables.filter(loops, Predicates.notNull()));
	}

	public Loop createLoop(final Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("createLoop(loop=" + loop + ")");
		}
		jdbc.update(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement("INSERT INTO loop (id, content) values(?, XML(?))");
						ps.setString(1, loop.getId());
						ps.setString(2, loop.getContent());
						return ps;
					}
				});
		return loop;
	}

	@Override
	public Loop updateLoop(Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(loop=" + loop + ")");
		}		
		this.jdbc.update("UPDATE loop SET content = XML(?), updatedAt = now() WHERE id = ?", loop.getContent(), loop.getId());
		return loop;
	};

	public class DefaultLoopRowMapper implements RowMapper<Loop> {
		public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Loop(
					rs.getString("id"),
					rs.getString("content"), 
					toDate(rs.getTimestamp("createdAt")),
					rs.getString("createdBy"));		
		}
	}

	@Override
	public List<Loop> findLoopsContainingTags(final String[] loopTags) {
		if(log.isDebugEnabled()) {
			log.debug("findLoopsContainingTags(loopTags=" + loopTags + ")");
		}
		List<Loop> loops = jdbc.query(
				"SELECT "
						+  "id,"
						+  "(xpath('//tag/text()', content))::text as tags "
						+ "FROM loop " 
						+ "ORDER BY updatedAt DESC",
						new RowMapper<Loop>() {
							public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {						
								String loopId = rs.getString("id");
								String[] tags = rs.getString("tags").replace("{", "").replace("}", "").split(",");

								if(Arrays.asList(tags).containsAll(Arrays.asList(loopTags))) {
									return getLoop(loopId);
								} else {
									return null;
								}
							}
						});		
		return Lists.newArrayList(Iterables.filter(loops, Predicates.notNull()));
	}
}
