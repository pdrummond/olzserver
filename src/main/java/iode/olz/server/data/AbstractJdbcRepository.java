package iode.olz.server.data;

import java.sql.Timestamp;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class AbstractJdbcRepository {

	protected JdbcTemplate jdbc;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }
    
    protected JdbcTemplate getJdbcTemplate() {
    	return jdbc;
    }
    
	protected final Date toDate(Timestamp timestamp) {
	    return timestamp != null ? new Date(timestamp.getTime()) : null;
	}

	protected final Timestamp toTimestamp(Date date) {
	    return date != null ? new Timestamp(date.getTime()) : null;
	}


}
