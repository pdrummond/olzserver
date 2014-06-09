package iode.olzserver.domain;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Shortcut {
	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private String loopId;
	private String userId;
	private Loop loop;
	private Date createdAt;
	private String createdBy;

	@JsonCreator
	public Shortcut(
			@JsonProperty("id") String id, 
			@JsonProperty("loopId") String loopId, 
			@JsonProperty("userId") String userId, 
			@JsonProperty("loop") Loop loop,			
			@JsonProperty("createdAt") Date createdAt, 
			@JsonProperty("createdBy") String createdBy) {
		this.id = id;
		this.loopId = loopId;
		this.userId = userId;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.loop = loop;
	}

	public Shortcut(String id, String loopId, String userId, Date createdAt, String createdBy) {
		this(id, loopId, userId, null, createdAt, createdBy);
	}

	public String getId() {
		return id;
	}
	
	public String getLoopId() {
		return loopId;
	}
	
	public String getUserId() {
		return userId;
	}

	public Loop getLoop() {
		return loop;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}
	
	public Shortcut copyWithNewLoop(Loop loop) {
		return new Shortcut(this.id, this.loopId, this.userId, this.loop, this.createdAt, this.createdBy);
	}
	
	@Override
	public String toString() {
		return "Shortcut(loopId=" + Objects.toString(loopId) + ", userId=" + Objects.toString(userId) + ")";
	}
}
