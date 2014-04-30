package iode.olzserver.data;

import iode.olzserver.domain.Loop;
import iode.olzserver.domain.Ref;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcRefRepository extends AbstractJdbcRepository implements RefRepository {
	private final Logger log = Logger.getLogger(getClass());

	@Override
	public Ref getRef(String loopId, String tagId) {
		if(log.isDebugEnabled()) {
			log.debug("getRef(loopId=" + loopId + ", tagId=" + tagId + ")");
		}

		List<Ref> refs = jdbc.query(
				"select ref_id, ref_loop_id, ref_tag_id, ref_created_at, ref_created_by from refs where ref_loop_id = ? AND ref_tag_id = ?",
				new Object[]{loopId, tagId}, 
				new DefaultRefRowMapper());
		
		if(refs.size() == 1) {
			return refs.get(0);
		} else {
			return null;
		}
	}

	public Ref getRef(String id) {
		log.debug("getRef(id=" + id + ")");
		return jdbc.queryForObject(
				"select ref_id, ref_loop_id, ref_tag_id, ref_created_at, ref_created_by from refs where ref_id = ?",
				new Object[]{id},
				new DefaultRefRowMapper());		
	}

	public boolean refExists(String id) {
		return this.jdbc.queryForObject("select count(*) from refs where ref_id = ?", new Object[]{id}, Integer.class) > 0;
	}

	public List<Ref> getRefs() {
		if(log.isDebugEnabled()) {
			log.debug("getRefs()");
		}
		return jdbc.query("select ref_id, ref_loop_id, ref_tag_id, ref_created_at, ref_created_by from refs", new DefaultRefRowMapper());
	}
	
	@Override
	public List<Ref> getRefsForLoop(String loopId) {
		if(log.isDebugEnabled()) {
			log.debug("getRefsForLoop(" + loopId + ")");
		}

		return jdbc.query(
				"select ref_id, ref_loop_id, ref_tag_id, ref_created_at, ref_created_by from refs where ref_loop_id = ?", 
				new String[]{ loopId }, new DefaultRefRowMapper());
	}
	
	@Override
	public Ref createRef(Loop loop, String tagId) {
		return createRef(new Ref(loop.getId(), tagId));
	}

	public Ref createRef(final Ref ref) {
		if(log.isDebugEnabled()) {
			log.debug("createRef(ref=" + ref + ")");
		}
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement("INSERT INTO refs(ref_loop_id, ref_tag_id, ref_created_by) values(?, ?, ?)", new String[] {"ref_id"});
						ps.setString(1, ref.getLoopId());
						ps.setString(2, ref.getTagId());
						ps.setString(3, ref.getCreatedBy());
						return ps;
					}
				}, keyHolder);		

		return ref.copyWithNewId(keyHolder.getKey().intValue());
	}

	@Override
	public void deleteRef(int id) {
		this.jdbc.update("DELETE FROM ref WHERE ref_id = ?", id);
	}
	
	class DefaultRefRowMapper implements RowMapper<Ref> {
		public Ref mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Ref(
					rs.getInt("ref_id"), 
					rs.getString("ref_loop_id"), 
					rs.getString("ref_tag_id"), 
					toDate(rs.getTimestamp("created_at")),
					rs.getString("ref_created_by"));
		}
	}

}
