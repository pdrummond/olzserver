package iode.olzserver.domain;

import iode.olzserver.service.LoopStatus;
import iode.olzserver.transform.HtmlifyTags;
import iode.olzserver.xml.utils.XmlLoop;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class Loop {
	public static final String TAG_REGEX = "(#[^@/.!][\\w-]*)|(@[^#/.!][\\w-]*)|(@![^#/.][\\w-]*)";

	public static final String OWNERTAG = "ownertag";
	public static final String USERTAG = "usertag";
	public static final String HASHTAG = "hashtag";
	public static final String TAG = "tag";

	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private Long podId;
	private String content;
	private LoopStatus status;
	private User owner;
	private String createdBy;
	private Date createdAt;
	private String updatedBy;
	private Date updatedAt;
	private List<String> tags;
	private List<LoopList> lists;

	@JsonIgnore
	private boolean incomingProcessingDone = false;

	private XmlLoop xmlLoop;

	@JsonCreator
	public Loop(
			@JsonProperty("id") String id, 
			@JsonProperty("podId") Long podId, 
			@JsonProperty("content") String content,
			@JsonProperty("status") LoopStatus status, 
			@JsonProperty("owner") User owner,
			@JsonProperty("createdAt") Date createdAt, 
			@JsonProperty("createdBy") String createdBy,
			@JsonProperty("updatedAt") Date updatedAt, 
			@JsonProperty("updatedBy") String updatedBy) {

		this(id, podId, content, status, owner, createdAt, createdBy, updatedAt, updatedBy,  Collections.<String>emptyList(), Collections.<LoopList>emptyList());
	}

	public Loop(String id, Long podId, String content, LoopStatus status, User owner, Date createdAt, String createdBy, Date updatedAt, String updatedBy, List<String> tags, List<LoopList> lists) {
		this.id = id;
		this.podId = podId;
		this.content = content;
		this.status = status;
		this.owner = owner;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.updatedAt = updatedAt;
		this.updatedBy = updatedBy;
		this.tags = tags;
		this.lists = lists;
	}

	public Loop(String content) {
		this(UUID.randomUUID().toString(), 1L, content, LoopStatus.NONE, null, null, null, null, null, Collections.<String>emptyList(), Collections.<LoopList>emptyList());
	}

	public Loop(String id, String content) {
		this(id, null, content, LoopStatus.NONE, null, new Date(), null, new Date(), null);
	}

	public Loop(String id, Long podId, String content) {
		this(id, podId, content, LoopStatus.NONE, null, new Date(), null, new Date(), null);
	}

	//Db constructor 
	public Loop(String id, Long podId, String content, Date createdAt, String createdBy, Date updatedAt, String updatedBy) {
		this(id, podId, content, LoopStatus.NONE, null, createdAt, createdBy, updatedAt, updatedBy, Collections.<String>emptyList(), Collections.<LoopList>emptyList());
	}

	public Loop copyWithNewId(String id) {
		return new Loop(id, this.podId, this.content, this.status, this.owner, this.createdAt, this.createdBy, this.updatedAt, this.updatedBy, this.tags, this.lists);
	}

	public Loop copyWithNewPodId(Long podId) {
		return new Loop(this.id, podId, this.content, this.status, this.owner, this.createdAt, this.createdBy,  this.updatedAt, this.updatedBy, this.tags, this.lists);
	}

	public Loop copyWithNewLists(List<LoopList> lists) {
		return new Loop(this.id, this.podId, this.content, this.status, this.owner, this.createdAt, this.createdBy,  this.updatedAt, this.updatedBy, this.tags, lists);
	}

	public Loop copyWithNewTags(List<String> tags) {
		return new Loop(this.id, this.podId, this.content, this.status, this.owner, this.createdAt, this.createdBy,  this.updatedAt, this.updatedBy, tags, lists);
	}

	public Loop copyWithNewContent(String content) {
		return new Loop(this.id, this.podId, content, this.status, this.owner, this.createdAt, this.createdBy,  this.updatedAt, this.updatedBy, this.tags, this.lists);
	}

	public Loop copyWithNewStatus(LoopStatus status) {
		return new Loop(this.id, this.podId, content, status, this.owner, this.createdAt, this.createdBy, this.updatedAt, this.updatedBy, this.tags, this.lists);
	}

	public Loop copyWithNewOwner(User owner) {
		return new Loop(this.id, this.podId, this.content, status, owner, this.createdAt, this.createdBy, this.updatedAt, this.updatedBy, this.tags, this.lists);
	}

	public Loop copyWithNewCreatedBy(String createdBy) {
		return new Loop(this.id, this.podId, this.content, status, this.owner, this.createdAt, createdBy, this.updatedAt, createdBy/*updatedBy*/, this.tags, this.lists);
	}

	public Loop copyWithNewUpdatedBy(String updatedBy) {
		return new Loop(this.id, this.podId, this.content, status, this.owner, this.createdAt, this.createdBy, this.updatedAt, updatedBy, this.tags, this.lists);
	}

	public String getId() {
		return id;
	}

	public Long getPodId() {
		return podId;
	}

	public String getContent() {
		return content;
	}

	public LoopStatus getStatus() {
		return status;
	}

	public User getOwner() {
		return owner;
	}

	public List<LoopList> getLists() {
		return lists;
	}
	
	public List<String> getTags() {
		return tags;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	@Override
	public String toString() {
		return String.format("Loop(id=%s, content=%s)",  getId(), StringUtils.abbreviate(getContent(), 40)); 
	}

	public XmlLoop xml() {
		if(xmlLoop == null) {
			xmlLoop = new XmlLoop(this);
		}
		return xmlLoop;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} 

		boolean equal = false;
		if (obj instanceof Loop) {
			Loop other = (Loop) obj; 

			equal = Objects.equal(id, other.id);
		} 

		return equal;
	}

	public static Loop createWithContent(String header, String body, String footer) {
		return new Loop(new HtmlifyTags(String.format("<div data-type='loop'><div class='header'>%s</div><div class='body'>%s</div><div class='footer'>%s</div></div>", 
				(header==null?"":header), 
				(body==null?"":body), 
				(footer==null?"":footer))).execute());
	}
	
	public boolean isIncomingProcessingDone() {
		return incomingProcessingDone;
	}

	public Loop incomingProcessingDone() {
		this.incomingProcessingDone  = true;
		return this;
	}

	public Loop withTagAddedToFooter(String tag, String tagType) {
		return xml().addTagToFooter(tag, Loop.HASHTAG).loopWithUpdatedContent();
	}
}
