package iode.olzserver.data;

import iode.olzserver.domain.Loop;
import iode.olzserver.domain.Pod;
import iode.olzserver.service.LoopNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	//private static final String LOOP_SELECT_SQL = "SELECT id, podId, content, filterText, showInnerLoops, createdAt, createdBy FROM loop ";
	private static final String LOOP_SELECT_SQL = "SELECT id, content, filterText, showInnerLoops, createdAt, createdBy FROM loop ";

	public Loop getLoop(String loopId, Long podId) {
		log.debug("getLoop(loopId=" + loopId + ", podId=" + podId + ")");
		List<Loop> loops = jdbc.query(
				LOOP_SELECT_SQL + " WHERE id = ?",// AND podId = ?",
				new Object[]{loopId},//, podId},
				new DefaultLoopRowMapper());
		if(loops.size() == 1) {
			return loops.get(0);
		} else {
			throw new LoopNotFoundException("No loop found with id " + loopId);
		}
	}

	public Loop createLoop(final Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("createLoop(loop=" + loop + ")");
		}
		jdbc.update(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement("INSERT INTO loop (id, content) values(?, ?)");
						ps.setString(1, loop.getId());
						//ps.setLong(2, loop.getPodId());
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
		this.jdbc.update("UPDATE loop SET content = ?, filterText = ?, showInnerLoops = ?, updatedAt = now() WHERE id = ?", 
				loop.getContent(),
				loop.getFilterText(),
				loop.isShowInnerLoops(),
				loop.getId());
		return loop;
	};

	public class DefaultLoopRowMapper implements RowMapper<Loop> {
		public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rsToLoop(rs);		
		}
	}

	public Loop rsToLoop(ResultSet rs) throws SQLException {
		return new Loop(
				rs.getString("id"),
				1L,//rs.getLong("podId"),
				rs.getString("content"),
				rs.getString("filterText"),
				rs.getBoolean("showInnerLoops"),
				toDate(rs.getTimestamp("createdAt")),
				rs.getString("createdBy"));
	}

	@Override
	public List<Loop> findInnerLoops(final String loopId, final Long podId) {
		if(log.isDebugEnabled()) {
			log.debug("findInnerLoops(loopId=" + loopId + ")");
		}
		List<Loop> loops = jdbc.query(
				LOOP_SELECT_SQL
				+ "WHERE content ~ '" + Loop.TAG_REGEX + "' " //OR id ~ '" + Loop.TAG_REGEX + "') " 
				+ "ORDER BY updatedAt DESC",
				new RowMapper<Loop>() {
					public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {
						Loop loop = rsToLoop(rs);
						
						if(loop.getId().equals(loopId)) { //Don't include the parent loop.
							return null;
						} else {
							Set<String> loopRefs = new HashSet<String>(loop.findBodyTagsWithoutSymbols());
							loopRefs.addAll(loop.findTitleTagsWithoutSymbols());
							
							Set<String> loopIds = new HashSet<String>(Loop.findTags(loopId, Loop.TAG_REGEX, false));
							if(loopRefs.containsAll(loopIds)) {
								return loop;
							} else {
								return null;
							}
						}
					}
				});		
		return Lists.newArrayList(Iterables.filter(loops, Predicates.notNull()));
	}

	@Override
	public void updateShowInnerLoops(String loopId, Long podId, Boolean showInnerLoops) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(loopId=" + loopId + ", podId=" + podId + ", showInnerLoops=" + showInnerLoops + ")");
		}		
		//this.jdbc.update("UPDATE loop SET showInnerLoops = ? WHERE id = ? AND podId = ?", showInnerLoops, loopId, podId);
		this.jdbc.update("UPDATE loop SET showInnerLoops = ? WHERE id = ?", showInnerLoops, loopId);
	}

	@Override
	public void updateFilterText(String loopId, Long podId, String filterText) {
		if(log.isDebugEnabled()) {
			log.debug("updateLoop(loopId=" + loopId + ", podId=" + podId + ", filterText=" + filterText + ")");
		}		
		//this.jdbc.update("UPDATE loop SET filterText = ? WHERE id = ? AND podId = ?", filterText, loopId, podId);
		this.jdbc.update("UPDATE loop SET filterText = ? WHERE id = ?", filterText, loopId);
	}

	@Override
	public List<Loop> findAllLoopsInPod(Pod pod) {
		if(log.isDebugEnabled()) {
			log.debug("findAllLoopsInPod(pod=" + pod + ")");
		}
		return jdbc.query(LOOP_SELECT_SQL + "WHERE podId = ? AND id <> ?" + "ORDER BY updatedAt DESC",
				new Object[] {pod.getId(), pod.getName()},
				new DefaultLoopRowMapper());
	}

	@Override
	public List<Loop> getAllLoops() {
		if(log.isDebugEnabled()) {
			log.debug("getAllLoops()");
		}
		return jdbc.query(LOOP_SELECT_SQL + "ORDER BY updatedAt DESC",new DefaultLoopRowMapper());
	}
}
