package iode.olz.server.domain;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Loop {
	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private String content;
	private String createdBy;	
	private Date createdAt;
	private List<Loop> loops;

	@JsonCreator
	public Loop(@JsonProperty("id") String id, @JsonProperty("content") String content, @JsonProperty("created_at") Date createdAt, @JsonProperty("created_by") String createdBy) {
		this(id, content, createdAt, createdBy, Collections.<Loop>emptyList());
	}

	public Loop(String id, String content, Date createdAt, String createdBy, List<Loop> loops) {
		this.id = id;
		this.content = content;
		this.createdAt = createdAt;
		this.loops = loops;
	}

	public Loop(String id, String content) {
		this(id, content, new Date(), null);
	}

	public Loop(String loopId) {
		this(loopId, "<div class='loop'><body></body></div>", new Date(), null);
	}

	public Loop copyWithNewId(String id) {
		return new Loop(id, this.content, this.createdAt, this.createdBy, this.loops);
	}

	public Loop copyWithNewInnerLoops(List<Loop> loops) {
		return new Loop(this.id, this.content, this.createdAt, this.createdBy, loops);
	}
	
	public Loop copyWithNewContent(String content) {
		return new Loop(this.id, content, this.createdAt, this.createdBy, loops);
	}
	
	public String getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public List<Loop> getLoops() {
		return loops;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
	
	@Override
	public String toString() {
		return String.format("Loop(id=%s, content=%s)",  getId(), StringUtils.abbreviate(getContent(), 20)); 
	}

}
