package iode.olz.server.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ref {
	//private final Logger log = Logger.getLogger(getClass());

	private int id;
	private String loopId;
	private String tagId;
	private Date createdAt;
	private String createdBy;

	@JsonCreator
	public Ref(@JsonProperty("loop_id") String loopId, @JsonProperty("tag_id") String tagId, @JsonProperty("created_at") Date createdAt, @JsonProperty("created_by") String createdBy) {
		this(-1, loopId, tagId, createdAt, createdBy);
	}
	
	public Ref(String loopId, String tagId) {
		this(-1, loopId, tagId, new Date(), null);
	}
	
	public Ref(int id, String loopId, String tagId, Date createdAt, String createdBy) {
		this.id = id;
		this.loopId = loopId;
		this.tagId = tagId;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
	}

	public int getId() {
		return id;
	}
	
	public String getLoopId() {
		return loopId;
	}
	
	public String getTagId() {
		return tagId;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Ref copyWithNewId(int id) {
		return new Ref(id, this.loopId, this.tagId, this.createdAt, this.createdBy);
	}
}
