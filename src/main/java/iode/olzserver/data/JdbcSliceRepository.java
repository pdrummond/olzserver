package iode.olzserver.data;

import iode.olzserver.domain.Slice;
import iode.olzserver.service.SliceNotFoundException;

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
public class JdbcSliceRepository extends AbstractJdbcRepository implements SliceRepository {
	private final Logger log = Logger.getLogger(getClass());

	@Override
	public Slice getSlice(Long id) {
		log.debug("getSlice(id=" + id + ")");

		List<Slice> slices = jdbc.query(
				"SELECT id, name, createdAt, createdBy FROM slice WHERE id = ?",
				new Object[]{id},
				new DefaultSliceRowMapper());
		if(slices.size() == 1) {
			return slices.get(0);
		} else {
			throw new IllegalArgumentException("No slice found with id: " + id);
		}
	}
	
	@Override
	public Slice getSliceByName(String name) {
		log.debug("getSliceByName(name=" + name + ")");

		List<Slice> slices = jdbc.query(
				"SELECT id, name, createdAt, createdBy FROM slice WHERE name = ?",
				new Object[]{name},
				new DefaultSliceRowMapper());
		if(slices.size() == 1) {
			return slices.get(0);
		} else {
			throw new SliceNotFoundException("No slice found with name: " + name);
		}
	}

	@Override
	public Slice createSlice(final String sliceName) {
		if(log.isDebugEnabled()) {
			log.debug("createSlice(sliceName=" + sliceName + ")");
		}
		
		final String[] primaryKey = new String[] {"id"};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		PreparedStatementCreator psCreator = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement("INSERT INTO slice (name) values (?)", primaryKey);
				
				ps.setString(1, sliceName);
				return ps;
			}
		};
		jdbc.update(psCreator, keyHolder);
				
		return new Slice(keyHolder.getKey().longValue(), sliceName);
	}

	@Override
	public Slice updateSlice(Slice slice) {
		if(log.isDebugEnabled()) {
			log.debug("updateSlice(slice=" + slice + ")");
		}		
		this.jdbc.update("UPDATE slice SET name = ?, updatedAt = now() WHERE id = ?", 
				slice.getName(),
				slice.getId());
		return slice;
	};
	
	@Override
	public Long getAndUpdateSliceNextNumber(Long sliceId) {
		Long nextNumber = this.jdbc.queryForObject("SELECT nextNumber from slice where id = ?", new Object[]{sliceId}, Long.class);
		this.jdbc.update("UPDATE slice SET nextNumber = ?, updatedAt = now() WHERE id = ?", nextNumber+1, sliceId);
		return nextNumber;
	}

	public class DefaultSliceRowMapper implements RowMapper<Slice> {
		public Slice mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Slice(
					rs.getLong("id"),
					rs.getString("name"),
					toDate(rs.getTimestamp("createdAt")),
					rs.getString("createdBy"));		
		}
	}	
}
