package iode.olzserver.domain;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Shortcut {
	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private String title;
	private String loopId;
	private String userId;
	private Loop loop;
	private Date createdAt;
	private String createdBy;

	@JsonCreator
	public Shortcut(
			@JsonProperty("id") String id, 
			@JsonProperty("title") String title, 
			@JsonProperty("loopId") String loopId, 
			@JsonProperty("userId") String userId, 
			@JsonProperty("loop") Loop loop,			
			@JsonProperty("createdAt") Date createdAt, 
			@JsonProperty("createdBy") String createdBy) {
		this.id = id;
		this.title = title;
		this.loopId = loopId;
		this.userId = userId;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.loop = loop;
	}

	public Shortcut(String id, String title, String loopId, String userId, Date createdAt, String createdBy) {
		this(id, title, loopId, userId, null, createdAt, createdBy);
	}

	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
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
		return new Shortcut(this.id, this.title, this.loopId, this.userId, loop, this.createdAt, this.createdBy);
	}
	
	@Override
	public String toString() {
		return "Shortcut(title=" + Objects.toString(title) + ", loopId=" + Objects.toString(loopId) + ", userId=" + Objects.toString(userId) + ")";
	}
}
