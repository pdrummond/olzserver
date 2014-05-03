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

	public Loop getLoop(String loopId, Long sliceId) {
		log.debug("getLoop(loopId=" + loopId + ", sliceId=" + sliceId + ")");

		List<Loop> loops = jdbc.query(
				"SELECT id, sliceId, content ::text, filterText, showInnerLoops, createdAt, createdBy FROM loop WHERE id = ? AND sliceId = ?",
				new Object[]{loopId, sliceId},
				new DefaultLoopRowMapper());
		if(loops.size() == 1) {
			return loops.get(0);
		} else {
			throw new LoopNotFoundException("No loop found with id " + loopId + " and slice id " + sliceId);
		}
	}

	/*@Override
	public List<Loop> getInnerLoops(final List<String> parentTags, final String owner) {
		if(log.isDebugEnabled()) {
			log.debug("getInnerLoops(parentTags=" + parentTags + ")");
		}
		List<Loop> loops = jdbc.query(
				"SELECT "
						+  "id,"
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
	}*/

	public Loop createLoop(final Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("createLoop(loop=" + loop + ")");
		}
		jdbc.update(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement("INSERT INTO loop (id, sliceId, content) values(?, ?, XML(?))");
						ps.setString(1, loop.getId());
						ps.setLong(2, loop.getSliceId());
						ps.setString(3, loop.getContent());
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
		this.jdbc.update("UPDATE loop SET content = XML(?), filterText = ?, showInnerLoops = ?, updatedAt = now() WHERE id = ?", 
				loop.getContent(),
				loop.getFilterText(),
				loop.isShowInnerLoops(),
				loop.getId());
		return loop;
	};

	public class DefaultLoopRowMapper implements RowMapper<Loop> {
		public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Loop(
					rs.getString("id"),
					rs.getLong("sliceId"),
					rs.getString("content"),
					rs.getString("filterText"),
					rs.getBoolean("showInnerLoops"),
					toDate(rs.getTimestamp("createdAt")),
					rs.getString("createdBy"));		
		}
	}

	@Override
	public List<Loop> findInnerLoops(final String loopId, final Long sliceId) {
		if(log.isDebugEnabled()) {
			log.debug("findInnerLoops(loopId=" + loopId + ", sliceId=" + sliceId + ")");
		}
		List<Loop> loops = jdbc.query(
				"SELECT "
						+  "id,"
						+  "(xpath('//loop-ref/text()', content))::text as loop_refs "
						+ "FROM loop " 
						+ "WHERE sliceId = ? "
						+ "ORDER BY updatedAt DESC",
						new Object[] {sliceId},
						new RowMapper<Loop>() {
							public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {						
								String id = rs.getString("id");
								String[] loopRefs = rs.getString("loop_refs").replace("{", "").replace("}", "").split(",");
								
								if(id.equals(loopId)) { //Don't include the parent loop.
									return null;
								} else if(Arrays.asList(loopRefs).contains(loopId)) {
									return getLoop(id, sliceId);
								} else {
									return null;
								}
							}
						});		
		return Lists.newArrayList(Iterables.filter(loops, Predicates.notNull()));
	}

	@Override
	public Long getAndUpdateSliceNextNumber(int sliceId) {
		Long nextNumber = this.jdbc.queryForObject("SELECT nextNumber from slice where id = ?", new Object[]{sliceId}, Long.class);
		this.jdbc.update("UPDATE slice SET nextNumber = ?, updatedAt = now() WHERE id = ?", nextNumber+1, sliceId);
		return nextNumber;
	}

	@Override
	public void updateShowInnerLoops(String loopId, Boolean showInnerLoops) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(loopId=" + loopId + "showInnerLoops=" + showInnerLoops + ")");
		}		
		this.jdbc.update("UPDATE loop SET showInnerLoops = ? WHERE id = ?", showInnerLoops, loopId);
	}

	@Override
	public void updateFilterText(String loopId, String filterText) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(loopId=" + loopId + "filterText=" + filterText + ")");
		}		
		this.jdbc.update("UPDATE loop SET filterText = ? WHERE id = ?", filterText, loopId);		
	}
}
