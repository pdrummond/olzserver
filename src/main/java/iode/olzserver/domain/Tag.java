package iode.olzserver.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tag {
	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private TagType type;
	private Date createdAt;
	private String createdBy;

	@JsonCreator
	public Tag(@JsonProperty("id") String id, @JsonProperty("type") TagType type, @JsonProperty("created_at") Date createdAt, @JsonProperty("created_by") String createdBy) {
		this.id = id;
		this.type = type;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
	}

	public String getId() {
		return id;
	}
	
	public TagType getType() {
		return type;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public Tag copyWithNewId(String id) {
		return new Tag(id, this.type, this.createdAt, this.createdBy);
	}
}
