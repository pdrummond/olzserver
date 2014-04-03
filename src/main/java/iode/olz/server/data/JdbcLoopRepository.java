package iode.olz.server.data;

import iode.olz.server.domain.Loop;

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
				"select loop_id, loop_content ::text, loop_created_at, loop_created_by from loops where loop_id = ?",
				new Object[]{id},
				new DefaultLoopRowMapper());
		if(loops.size() == 1) {
			return loops.get(0);
		} else {
			return null;
		}
	}

	//SELECT loop_id, loop_content ::text from loops where (xpath('//tag[@type="usertag"]/text()', loop_content))  

	//Get loops where xml contains usertag = pd or usertag = PO.

	@Override
	public List<Loop> getInnerLoops(final String parentLid, final List<String> parentUsertags) {
		if(log.isDebugEnabled()) {
			log.debug("getInnerLoops(parentLid=" + parentLid + ", parentUsertags=" + parentUsertags + ")");
		}
		List<Loop> loops = jdbc.query(
				"SELECT "
				 +  "loop_id, "
				 +  "(xpath('//tag[@type=\"usertag\"]/text()', loop_content))::text as usertags, "
				 +  "(xpath('//tag[@type=\"hashtag\"]/text()', loop_content))::text as hashtags "
				 + "FROM loops",
				new RowMapper<Loop>() {
					public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {						
						String lid = rs.getString("loop_id");
						String[] usertags = rs.getString("usertags").replace("{", "").replace("}", "").split(",");
						String[] hashtags = rs.getString("hashtags").replace("{", "").replace("}", "").split(",");
						
						if(Arrays.asList(hashtags).contains(parentLid)) {
							if(parentUsertags.containsAll(Arrays.asList(usertags))) {
								return getLoop(lid);
							}
						} 
						return null;
					}
				 });		
		return Lists.newArrayList(Iterables.filter(loops, Predicates.notNull()));
		
	}


	public boolean loopExists(String lid) {
		return this.jdbc.queryForObject("select count(*) from loops where loop_id = ?", new Object[]{lid}, Integer.class) > 0;
	}

	public List<Loop> getLoops() {
		if(log.isDebugEnabled()) {
			log.debug("getLoops()");
		}
		return jdbc.query("select loop_id, loop_content ::text, loop_created_at, loop_created_by from loops", new DefaultLoopRowMapper());
	}

	public Loop createLoop(final Loop loop) {
		if(log.isDebugEnabled()) {
			log.debug("createLoop(loop=" + loop + ")");
		}
		jdbc.update(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement("INSERT INTO loops(loop_id, loop_content) values(?, XML(?))", new String[] {"loop_id"});
						ps.setString(1, loop.getId());
						ps.setString(2, loop.getContent());
						return ps;
					}
				});		

		return loop;
	}

	public class DefaultLoopRowMapper implements RowMapper<Loop> {
		public Loop mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Loop(
					rs.getString("loop_id"), 
					rs.getString("loop_content"), 
					toDate(rs.getTimestamp("loop_created_at")),
					rs.getString("loop_created_by"));		
		}
	}

	@Override
	public Loop changeLoopId(Loop loop, String newLoopId) {
		this.jdbc.update("UPDATE loops set loop_id = ? WHERE loop_id = ?", newLoopId, loop.getId());
		return loop.copyWithNewId(newLoopId);
	};

	@Override
	public Loop changeLoop(Loop loop, String newLoopId, String content) {
		this.jdbc.update("UPDATE loops SET loop_id = ?, content = ? WHERE loop_id = ?", newLoopId, content, loop.getId());
		return loop.copyWithNewId(newLoopId);
	};

}
