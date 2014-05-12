package iode.olzserver.data;

import iode.olzserver.domain.Pod;
import iode.olzserver.service.PodNotFoundException;

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
public class JdbcPodRepository extends AbstractJdbcRepository implements PodRepository {
	private final Logger log = Logger.getLogger(getClass());

	@Override
	public Pod getPod(Long id) {
		log.debug("getPod(id=" + id + ")");

		List<Pod> pods = jdbc.query(
				"SELECT id, name, createdAt, createdBy FROM pod WHERE id = ?",
				new Object[]{id},
				new DefaultPodRowMapper());
		if(pods.size() == 1) {
			return pods.get(0);
		} else {
			throw new IllegalArgumentException("No pod found with id: " + id);
		}
	}

	@Override
	public Pod getPodByName(String name) {
		log.debug("getPodByName(name=" + name + ")");

		List<Pod> pods = jdbc.query(
				"SELECT id, name, createdAt, createdBy FROM pod WHERE name = ?",
				new Object[]{name},
				new DefaultPodRowMapper());
		if(pods.size() == 1) {
			return pods.get(0);
		} else {
			throw new PodNotFoundException("No pod found with name: " + name);
		}
	}

	@Override
	public Pod createPod(final String podName) {
		if(log.isDebugEnabled()) {
			log.debug("createPod(podName=" + podName + ")");
		}

		final String[] primaryKey = new String[] {"id"};
		KeyHolder keyHolder = new GeneratedKeyHolder();

		PreparedStatementCreator psCreator = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement("INSERT INTO pod (name) values (?)", primaryKey);

				ps.setString(1, podName);
				return ps;
			}
		};
		jdbc.update(psCreator, keyHolder);

		return new Pod(keyHolder.getKey().longValue(), podName);
	}

	@Override
	public Pod updatePod(Pod pod) {
		if(log.isDebugEnabled()) {
			log.debug("updatePod(pod=" + pod + ")");
		}		
		this.jdbc.update("UPDATE pod SET name = ?, updatedAt = now() WHERE id = ?", 
				pod.getName(),
				pod.getId());
		return pod;
	};

	@Override
	public Long getAndUpdatePodNextNumber(Long podId) {
		Long nextNumber = this.jdbc.queryForObject("SELECT nextNumber from pod where id = ?", new Object[]{podId}, Long.class);
		this.jdbc.update("UPDATE pod SET nextNumber = ?, updatedAt = now() WHERE id = ?", nextNumber+1, podId);
		return nextNumber;
	}

	public class DefaultPodRowMapper implements RowMapper<Pod> {
		public Pod mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Pod(
					rs.getLong("id"),
					rs.getString("name"),
					toDate(rs.getTimestamp("createdAt")),
					rs.getString("createdBy"));		
		}
	}	
}