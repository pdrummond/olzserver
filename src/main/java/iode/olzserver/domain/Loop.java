package iode.olzserver.domain;

import iode.olzserver.service.LoopStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

public class Loop {
	public static final String TAG_REGEX = "(#[^@/.!][\\w-]*)|(@[^#/.!][\\w-]*)|(/[^#@/.!][\\w-]*)|(\\.[^#/@][\\w-!]*)";

	private static final String OWNER_REGEX_WITH_TAG_SYMBOL = "(@![^#/.][\\w-]*)";

	private static final String OWNER_REGEX_WITHOUT_TAG_SYMBOL = "@!([^#/.][\\w-]*)";

	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private Long podId;
	private Boolean showInnerLoops;
	private String content;
	private LoopStatus status;
	private String filterText;
	private String ownerImageUrl;
	private String createdBy;
	private Date createdAt;
	private String updatedBy;
	private Date updatedAt;
	private List<LoopList> lists;

	@JsonCreator
	public Loop(
			@JsonProperty("id") String id, 
			@JsonProperty("podId") Long podId, 
			@JsonProperty("content") String content,
			@JsonProperty("status") LoopStatus status, 
			@JsonProperty("filterText") String filterText,
			@JsonProperty("showInnerLoops") Boolean showInnerLoops, 
			@JsonProperty("ownerImageUrl") String ownerImageUrl,
			@JsonProperty("createdAt") Date createdAt, 
			@JsonProperty("createdBy") String createdBy,
			@JsonProperty("updatedAt") Date updatedAt, 
			@JsonProperty("updatedBy") String updatedBy) {

		this(id, podId, content, status, filterText, showInnerLoops, ownerImageUrl, createdAt, createdBy, updatedAt, updatedBy, Collections.<LoopList>emptyList());
	}

	public Loop(String id, Long podId, String content, LoopStatus status, String filterText, Boolean showInnerLoops, String ownerImageUrl, Date createdAt, String createdBy, Date updatedAt, String updatedBy, List<LoopList> lists) {
		this.id = id;
		this.podId = podId;
		this.content = content;
		this.status = status;
		this.filterText = filterText;
		this.showInnerLoops = showInnerLoops;
		this.ownerImageUrl = ownerImageUrl;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.updatedAt = updatedAt;
		this.updatedBy = updatedBy;

		this.lists = lists;
	}

	public Loop(String id) {
		this(id, null, "", LoopStatus.NONE, null, Boolean.FALSE, null, null, null, null, null, Collections.<LoopList>emptyList());
	}

	public Loop(String id, String content) {
		this(id, null, content, LoopStatus.NONE, null, Boolean.FALSE, null, new Date(), null, new Date(), null);
	}

	public Loop(String id, Long podId, String content) {
		this(id, podId, content, LoopStatus.NONE, null, Boolean.FALSE, null, new Date(), null, new Date(), null);
	}

	public Loop(String id, Long podId, String content, String filterText, Boolean showInnerLoops, Date createdAt, String createdBy, Date updatedAt, String updatedBy) {
		this(id, podId, content, LoopStatus.NONE, filterText, showInnerLoops, null, createdAt, createdBy, updatedAt, updatedBy);
	}

	public Loop copyWithNewId(String id) {
		return new Loop(id, this.podId, this.content, this.status, this.filterText, this.showInnerLoops, this.ownerImageUrl, this.createdAt, this.createdBy, this.updatedAt, this.updatedBy, this.lists);
	}

	public Loop copyWithNewPodId(Long podId) {
		return new Loop(this.id, podId, this.content, this.status, this.filterText, this.showInnerLoops, this.ownerImageUrl, this.createdAt, this.createdBy,  this.updatedAt, this.updatedBy, this.lists);
	}

	public Loop copyWithNewLists(List<LoopList> lists) {
		return new Loop(this.id, this.podId, this.content, this.status, this.filterText, this.showInnerLoops, this.ownerImageUrl, this.createdAt, this.createdBy,  this.updatedAt, this.updatedBy, lists);
	}

	public Loop copyWithNewContent(String content) {
		return new Loop(this.id, this.podId, content, this.status, this.filterText, this.showInnerLoops, this.ownerImageUrl, this.createdAt, this.createdBy,  this.updatedAt, this.updatedBy, this.lists);
	}

	public Loop copyWithNewStatus(LoopStatus status) {
		return new Loop(this.id, this.podId, content, status, this.filterText, this.showInnerLoops, this.ownerImageUrl, this.createdAt, this.createdBy, this.updatedAt, this.updatedBy, this.lists);
	}

	public Loop copyWithNewOwnerImageUrl(String ownerImageUrl) {
		return new Loop(this.id, this.podId, this.content, status, this.filterText, this.showInnerLoops, ownerImageUrl, this.createdAt, createdBy, this.updatedAt, createdBy/*updatedBy*/, this.lists);
	}

	public Loop copyWithNewCreatedBy(String createdBy) {
		return new Loop(this.id, this.podId, this.content, status, this.filterText, this.showInnerLoops, this.ownerImageUrl, this.createdAt, createdBy, this.updatedAt, createdBy/*updatedBy*/, this.lists);
	}

	public Loop copyWithNewUpdatedBy(String updatedBy) {
		return new Loop(this.id, this.podId, this.content, status, this.filterText, this.showInnerLoops, this.ownerImageUrl, this.createdAt, this.createdBy, this.updatedAt, this.updatedBy, this.lists);
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

	public String getFilterText() {
		return filterText;
	}

	public Boolean isShowInnerLoops() {
		return showInnerLoops;
	}

	public String getOwnerImageUrl() {
		return ownerImageUrl;
	}

	public List<LoopList> getLists() {
		return lists;
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

	public Loop convertLoopToHtml() {
		/*Loop loop = null;
		if(log.isDebugEnabled()) {
			log.debug("convertLoopToHtml(" + this + ")");
		}	
		loop = copyWithNewContent(new LoopDown(getContent()).toHtml());
		if(log.isDebugEnabled()) {
			log.debug("loop HTML=" + loop);
		}	

		List<Loop> innerLoops = new ArrayList<Loop>();
		for(Loop innerLoop : loop.getLoops()) {
			innerLoops.add(innerLoop.convertLoopToHtml());
		}
		return loop.copyWithNewInnerLoops(innerLoops);*/
		return this;
	}

	public Loop convertLoopToMd() {
		/*Loop loop = null;
		if(log.isDebugEnabled()) {
			log.debug("convertLoopToMd(" + this + ")");
		}
		if(log.isDebugEnabled()) {
			log.debug("HTML content: " + getContent());
		}
		try {
			loop = copyWithNewContent(Transform.getInstance().transform("loop-from-html", "<root>" + getContent() + "</root>") );
			if(log.isDebugEnabled()) {
				log.debug("MD content: " + loop.getContent());
			}
		} catch (TransformException e) {
			throw new RuntimeException("Error converting loop to MD", e);
		}

		List<Loop> innerLoops = new ArrayList<Loop>();
		for(Loop innerLoop : loop.getLoops()) {
			innerLoops.add(innerLoop.convertLoopToMd());
		}
		return loop.copyWithNewInnerLoops(innerLoops);*/
		return this;
	}

	public List<String> findTags() {
		return findTags(getContent(), TAG_REGEX, true);
	}

	public List<String> findTagsWithoutSymbols() {
		return findTags(getContent(), TAG_REGEX, false);
	}

	public List<String> findTitleTagsWithoutSymbols() {
		return findTags(getId(), TAG_REGEX, false);
	}

	public List<String> findUserTags() {
		return findTags(getContent(), "(@[^#/][\\w-]*)", true);
	}

	public List<String> findUserTags_() {
		List<String> tags = new ArrayList<String>();
		for(String tag : findUserTags()) {
			tags.add(tag.replaceAll("@!", ""));
			tags.add(tag.replaceAll("@", ""));
		}
		return tags;
	}

	public static List<String> findTags(String input, String regex, boolean includeSymbols) {
		//Three patterns, one for each tag type: hashtag, then usertag, then slashtag
		//For each pattern: first the tag identifier (#), then omit other tag identifiers ([^@/]) then a word including '-').  
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		List<String> tags = new ArrayList<String>();
		while(m.find()) {
			String tag = m.group();

			if(includeSymbols) {
				tag = tag.trim();
			} else {
				tag = tag.trim().replaceAll("[#@/.]", "");
			}
			tags.add(tag);
		}
		return ImmutableSet.copyOf(tags).asList(); //ensure no duplicates
	}

	public boolean hasOwner() {
		Pattern p = Pattern.compile(OWNER_REGEX_WITH_TAG_SYMBOL); 
		Matcher m = p.matcher(getContent());
		return m.find();
	}

	public String findOwner() {
		String owner = null;
		Pattern p = Pattern.compile(OWNER_REGEX_WITHOUT_TAG_SYMBOL);
		Matcher m = p.matcher(getContent());
		if(m.find()) {
			owner = m.group(1);
		}
		return owner;
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

			equal = 
			Objects.equal(id, other.id);
		} 

		return equal;
	}



}
