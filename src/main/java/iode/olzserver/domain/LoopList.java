package iode.olzserver.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoopList {
	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private String loopId;
	private String name;
	private Integer total;
	private String query;
	private Date createdAt;
	private String createdBy;

	@JsonCreator
	public LoopList(
			@JsonProperty("id") String id, 
			@JsonProperty("loopId") String loopId, 
			@JsonProperty("name") String name, 
			@JsonProperty("query") String query,
			@JsonProperty("total") Integer total, 
			@JsonProperty("created_at") Date createdAt, 
			@JsonProperty("created_by") String createdBy) {
		this.id = id;
		this.loopId = loopId;
		this.name = name;
		this.query = query;
		this.total = total;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
	}

	public String getId() {
		return id;
	}
	
	public String getLoopId() {
		return loopId;
	}
	
	public String getName() {
		return name;
	}

	public String getQuery() {
		return query;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public LoopList copyWithNewId(String id) {
		return new LoopList(id, this.loopId, this.name, this.query, this.total, this.createdAt, this.createdBy);
	}
}
