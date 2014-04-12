package iode.olz.server.data;

import iode.olz.server.domain.Loop;
import iode.olz.server.service.LoopNotFoundException;

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

	public Loop getLoop(String sid) {
		log.debug("getLoop(sid=" + sid + ")");

		List<Loop> loops = jdbc.query(
				"SELECT uid, sid, content ::text, created_at, created_by FROM loops WHERE sid = ?",
				new Object[]{sid},
				new DefaultLoopRowMapper());
		if(loops.size() == 1) {
			return loops.get(0);
		} else {
			throw new LoopNotFoundException("No loop found with SID=" + sid);
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
				 + "FROM loops " 
				 + "ORDER BY updated_at DESC",
				new RowMapper<Loop>() {
					public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {						
						String sid = rs.getString("sid");
						String[] tags = rs.getString("tags").replace("{", "").replace("}", "").split(",");
						
						if(Arrays.asList(tags).containsAll(parentTags)) {
							if(owner != null) {
								if(Loop.isSidOwner(sid, owner)) {
									return getLoop(sid);
								}
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
						PreparedStatement ps = connection.prepareStatement("INSERT INTO loops(sid, content) values(?, XML(?))", new String[] {"uid"});
						ps.setString(1, loop.getSid());
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
		this.jdbc.update("UPDATE loops SET content = XML(?), updated_at = now() WHERE sid = ?", loop.getContent(), loop.getSid());
		return loop;
	};
	
	public class DefaultLoopRowMapper implements RowMapper<Loop> {
		public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Loop(
					rs.getString("uid"),
					rs.getString("sid"), 
					rs.getString("content"), 
					toDate(rs.getTimestamp("created_at")),
					rs.getString("created_by"));		
		}
	}
}
