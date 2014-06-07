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
	//public static final String TAG_REGEX = "(#[^@/.!][\\w-]*)|(@[^#/.!][\\w-]*)|(@![^#/.][\\w-]*)";
	public static final String TAG_REGEX = "(#[^@/.!][\\w-]*)(@[^#/.!][\\w-]*)|(@[^#/.!][\\w-]*)";

	public static final String OWNERTAG = "ownertag";
	public static final String USERTAG = "usertag";
	public static final String HASHTAG = "hashtag";
	public static final String TAG = "tag";

	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private String handle;
	private String ownerTag;
	private Long podId;
	private String content;
	private LoopStatus status;
	private User owner;
	private String createdBy;
	private Date createdAt;
	private String updatedBy;
	private Date updatedAt;
	private String newId;
	private List<String> tags;
	private List<LoopList> lists;

	@JsonIgnore
	private boolean incomingProcessingDone = false;

	private XmlLoop xmlLoop;
	
	@JsonCreator
	public Loop(
			@JsonProperty("id") String id, 
			@JsonProperty("handle") String handle, 
			@JsonProperty("ownerTag") String ownerTag, 
			@JsonProperty("podId") Long podId, 
			@JsonProperty("content") String content,
			@JsonProperty("status") LoopStatus status, 
			@JsonProperty("owner") User owner,
			@JsonProperty("createdAt") Date createdAt, 
			@JsonProperty("createdBy") String createdBy,
			@JsonProperty("updatedAt") Date updatedAt, 
			@JsonProperty("updatedBy") String updatedBy,
			@JsonProperty("newId") String newId) {

		this(id, handle, ownerTag, podId, content, status, owner, createdAt, createdBy, updatedAt, updatedBy, Collections.<String>emptyList(), Collections.<LoopList>emptyList(), newId);
	}
	
	public Loop(Loop.Builder b) {
		this.id = b.id;
		this.handle = b.handle;
		this.ownerTag = b.ownerTag;
		this.podId = b.podId;
		this.content = b.content;
		this.status = b.status;
		this.owner = b.owner;
		this.createdAt = b.createdAt;
		this.createdBy = b.createdBy;
		this.updatedAt = b.updatedAt;
		this.updatedBy = b.updatedBy;
		this.tags = b.tags;
		this.lists = b.lists;
		this.newId = b.newId;
	}

	public Loop(String id, String handle, String ownerTag, Long podId, String content, LoopStatus status, User owner, Date createdAt, String createdBy, Date updatedAt, String updatedBy, List<String> tags, List<LoopList> lists, String newId) {
		this.id = id;
		this.handle = handle;
		this.ownerTag = ownerTag;
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
		this.newId = newId;
	}

	public Loop(String content) {
		this(UUID.randomUUID().toString(), null, null, 1L, content, LoopStatus.NONE, null, null, null, null, null, Collections.<String>emptyList(), Collections.<LoopList>emptyList(), null);
	}

	public Loop(String id, Long podId, String content) {
		this(id, null, null, podId, content, LoopStatus.NONE, null, new Date(), null, new Date(), null, null);
	}

	//Db constructor 
	public Loop(String id, String ownerTag, Long podId, String content, Date createdAt, String createdBy, Date updatedAt, String updatedBy) {
		this(id, null, ownerTag, podId, content, LoopStatus.NONE, null, createdAt, createdBy, updatedAt, updatedBy, Collections.<String>emptyList(), Collections.<LoopList>emptyList(), null);
	}

	public Loop(String id, String content) {
		this(id, null, null, 1L, content, LoopStatus.NONE, null, new Date(), null, new Date(), null, null);
	}

	public Loop copyWithNewId(String id) {
		return new Loop(Loop.Builder.fromLoop(this).id(id));
	}

	public Loop copyWithNewHandle(String handle) {
		return new Loop(Loop.Builder.fromLoop(this).handle(handle));
	}

	public Loop copyWithNewPodId(Long podId) {
		return new Loop(Loop.Builder.fromLoop(this).podId(podId));
	}

	public Loop copyWithNewLists(List<LoopList> lists) {
		return new Loop(Loop.Builder.fromLoop(this).lists(lists));
	}

	public Loop copyWithNewTags(List<String> tags) {
		return new Loop(Loop.Builder.fromLoop(this).tags(tags));
	}

	public Loop copyWithNewContent(String content) {
		return new Loop(Loop.Builder.fromLoop(this).content(content));
	}

	public Loop copyWithNewStatus(LoopStatus status) {
		return new Loop(Loop.Builder.fromLoop(this).status(status));
	}

	public Loop copyWithNewOwner(User owner) {
		return new Loop(Loop.Builder.fromLoop(this).owner(owner));
	}

	public Loop copyWithNewOwnerTag(String ownerTag) {
		return new Loop(Loop.Builder.fromLoop(this).ownerTag(ownerTag));
	}

	public Loop copyWithNewCreatedBy(String createdBy) {
		return new Loop(Loop.Builder.fromLoop(this).createdBy(createdBy));
	}

	public Loop copyWithNewUpdatedBy(String updatedBy) {
		return new Loop(Loop.Builder.fromLoop(this).updatedBy(updatedBy));
	}

	public String getId() {
		return id;
	}
	
	public String getHandle() {
		return handle;
	}

	public String getOwnerTag() {
		return ownerTag;
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
	
	public String getNewId() {
		return newId;
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

	public static class Builder {
		private String id;
		private String handle;
		private String ownerTag;
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
		private String newId;

		public Builder() {
		}

		public static Builder fromLoop(Loop loop) {
			Loop.Builder b = new Loop.Builder();
			b.id = loop.id;
			b.handle = loop.handle;
			b.ownerTag = loop.ownerTag;
			b.podId = loop.podId;
			b.content = loop.content;
			b.status = loop.status;
			b.owner = loop.owner;
			b.createdBy = loop.createdBy;
			b.createdAt = loop.createdAt;
			b.updatedBy = loop.updatedBy;
			b.updatedAt = loop.updatedAt;
			b.tags = loop.tags;
			b.lists = loop.lists;
			b.newId = loop.newId;
			return b;
		}

		public Builder id(String val) 			 { id = val; return this; 			}
		public Builder handle(String val) 		 { handle = val; return this; 		}
		public Builder ownerTag(String val) 	 { ownerTag = val; return this; 	}
		public Builder podId(Long val) 			 { podId = val; return this; 		}
		public Builder content(String val) 		 { content = val; return this; 		}
		public Builder status(LoopStatus val) 	 { status = val; return this; 		}
		public Builder owner(User val) 			 { owner = val; return this; 		}
		public Builder createdBy(String val) 	 { createdBy = val; return this; 	}
		public Builder createdAt(Date val) 		 { createdAt = val; return this; 	}
		public Builder updatedBy(String val) 	 { updatedBy = val; return this; 	}
		public Builder updatedAt(Date val) 		 { updatedAt = val; return this; 	}
		public Builder tags(List<String> val)	 { tags = val; return this; 		}
		public Builder lists(List<LoopList> val) { lists = val; return this; 		}
		public Builder newId(String val)	 	 { newId = val; return this; 		}

		public Loop build() {
			return new Loop(this);
		}

	}

	public String extractOwnerTagFromId() {
		return getId().split("@")[1];
	}

	public String extractOwnerTagFromId_() {
		return extractOwnerTagFromId().replace("@", "");
	}
}
