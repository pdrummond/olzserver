package iode.olzserver.data;

import iode.olzserver.domain.Loop;
import iode.olzserver.domain.Slice;
import iode.olzserver.service.LoopNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	private static final String LOOP_SELECT_SQL = "SELECT id, uid, sliceId, content, filterText, showInnerLoops, createdAt, createdBy FROM loop ";


	public Loop getLoop(String loopId, Long sliceId) {
		log.debug("getLoop(loopId=" + loopId + ", sliceId=" + sliceId + ")");

		List<Loop> loops = jdbc.query(
				LOOP_SELECT_SQL + " WHERE id = ? AND sliceId = ?",
				new Object[]{loopId, sliceId},
				new DefaultLoopRowMapper());
		if(loops.size() == 1) {
			return loops.get(0);
		} else {
			throw new LoopNotFoundException("No loop found with id " + loopId + " and slice id " + sliceId);
		}
	}

	public Loop createLoop(final Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("createLoop(loop=" + loop + ")");
		}
		jdbc.update(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement("INSERT INTO loop (id, sliceId, content) values(?, ?, ?)");
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

	@Override
	public List<Loop> findAllLoopsForSlice(Slice slice) {
		if(log.isDebugEnabled()) {
			log.debug("findAllLoopsForSlice(slice=" + slice + ")");
		}
		return jdbc.query(LOOP_SELECT_SQL + "WHERE sliceId = ? AND id <> ?" + "ORDER BY updatedAt DESC",
				new Object[] {slice.getId(), slice.getName()},
				new DefaultLoopRowMapper());
	}


	public Loop rsToLoop(ResultSet rs) throws SQLException {
		return new Loop(
				rs.getString("id"),
				rs.getString("uid"),
				rs.getLong("sliceId"),
				rs.getString("content"),
				rs.getString("filterText"),
				rs.getBoolean("showInnerLoops"),
				toDate(rs.getTimestamp("createdAt")),
				rs.getString("createdBy"));
	}

	@Override
	public List<Loop> findInnerLoops(final String loopId, final Long sliceId) {
		if(log.isDebugEnabled()) {
			log.debug("findInnerLoops(loopId=" + loopId + ", sliceId=" + sliceId + ")");
		}
		List<Loop> loops = jdbc.query(
				LOOP_SELECT_SQL 
				+ "WHERE sliceId = ? "
				+ "AND content ~ '(#[^@/~][\\w-]*)|(~[^#/@][\\w-]*)|(/[^#@~][\\w-]*)'"
				+ "ORDER BY updatedAt DESC",
				new Object[] {sliceId},
				new RowMapper<Loop>() {
					public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {
						Loop loop = rsToLoop(rs);
						List<String> loopRefs = loop.findLoopRefs();
						if(loop.getId().equals(loopId)) { //Don't include the parent loop.
							return null;
						} else if(loopRefs.contains(loopId)) {
							return loop;
						} else {
							return null;
						}
					}
				});		
		return Lists.newArrayList(Iterables.filter(loops, Predicates.notNull()));
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
