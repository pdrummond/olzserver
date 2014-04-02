package iode.olz.server.data;

import iode.olz.server.domain.Tag;
import iode.olz.server.domain.TagType;

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
public class JdbcTagRepository extends AbstractJdbcRepository implements TagRepository {
	private final Logger log = Logger.getLogger(getClass());

	public Tag getTag(String id) {
		log.debug("getTag(id=" + id + ")");

		Tag tag = jdbc.queryForObject(
				"select tag_id, tag_type, tag_created_at, tag_created_by from tags where tag_id = ?",
				new Object[]{id},
				new RowMapper<Tag>() {
					public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
						Tag tag = new Tag(
								rs.getString("tag_id"), 
								TagType.fromValue(rs.getInt("tag_type")), 
								toDate(rs.getTimestamp("created_at")),
								rs.getString("tag_created_by"));
						return tag;
					}
				});
		return tag;
	}

	public boolean tagExists(String id) {
		return this.jdbc.queryForObject("select count(*) from tags where tag_id = ?", new Object[]{id}, Integer.class) > 0;
	}

	public List<Tag> getTags() {
		log.debug("getTags()");

		return jdbc.query("select tag_id, tag_type, tag_created_at, tag_created_by from tags", new RowMapper<Tag>() {
			@Override
			public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Tag(
						rs.getString("tag_id"), 
						TagType.fromValue(rs.getInt("tag_type")), 
						toDate(rs.getTimestamp("tag_created_at")),
						rs.getString("tag_created_by"));
			}
		});
	}

	public Tag createTag(final Tag tag) {
		if(log.isDebugEnabled()) {
			log.debug("createTag(tag=" + tag + ")");
		}
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement ps = connection.prepareStatement("INSERT INTO tags(tag_id, tag_type) values(?, ?)", new String[] {"tag_id"});
						ps.setString(1, tag.getId());
						ps.setInt(2, tag.getType().getId());
						return ps;
					}
				}, keyHolder);		

		return tag.copyWithNewId(keyHolder.getKey().toString());
	}
	}
