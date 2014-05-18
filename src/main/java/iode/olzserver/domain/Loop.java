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
import com.google.common.collect.ImmutableSet;

public class Loop {
	public static final String TAG_REGEX = "(#[^@/.][\\w-]*)|(@[^#/.][\\w-]*)|(/[^#@/.][\\w-]*)|(\\.[^#/@][\\w-]*)";

	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private Long podId;
	private Boolean showInnerLoops;
	private String content;
	private LoopStatus status;
	private String filterText;
	private String createdBy;	
	private Date createdAt;
	private List<Loop> loops;

	@JsonCreator
	public Loop(
			@JsonProperty("id") String id, 
			@JsonProperty("podId") Long podId, 
			@JsonProperty("content") String content, 
			@JsonProperty("status") LoopStatus status, 
			@JsonProperty("filterText") String filterText,
			@JsonProperty("showInnerLoops") Boolean showInnerLoops, 
			@JsonProperty("createdAt") Date createdAt, 
			@JsonProperty("createdBy") String createdBy) {
		this(id, podId, content, status, filterText, showInnerLoops, createdAt, createdBy, Collections.<Loop>emptyList());
	}

	public Loop(String id, Long podId, String content, LoopStatus status, String filterText, Boolean showInnerLoops, Date createdAt, String createdBy, List<Loop> loops) {
		this.id = id;
		this.podId = podId;
		this.content = content;
		this.status = status;
		this.filterText = filterText;
		this.showInnerLoops = showInnerLoops;
		this.createdAt = createdAt;
		this.loops = loops;
	}

	public Loop(String id) {
		this(id, null, "", LoopStatus.NONE, null, Boolean.FALSE, null, null, Collections.<Loop>emptyList());
	}

	public Loop(String id, String content) {
		this(id, null, content, LoopStatus.NONE, null, Boolean.FALSE, new Date(), null);
	}

	public Loop(String id, Long podId, String content) {
		this(id, podId, content, LoopStatus.NONE, null, Boolean.FALSE, new Date(), null);
	}

	public Loop(String id, Long podId, String content, String filterText, Boolean showInnerLoops, Date createdAt, String createdBy) {
		this(id, podId, content, LoopStatus.NONE, filterText, showInnerLoops, createdAt, createdBy);
	}

	public Loop copyWithNewId(String id) {
		return new Loop(id, this.podId, this.content, this.status, this.filterText, this.showInnerLoops, this.createdAt, this.createdBy, this.loops);
	}

	public Loop copyWithNewPodId(Long podId) {
		return new Loop(this.id, podId, this.content, this.status, this.filterText, this.showInnerLoops, this.createdAt, this.createdBy, this.loops);
	}

	public Loop copyWithNewInnerLoops(List<Loop> loops) {
		return new Loop(this.id, this.podId, this.content, this.status, this.filterText, this.showInnerLoops, this.createdAt, this.createdBy, loops);
	}

	public Loop copyWithNewContent(String content) {
		return new Loop(this.id, this.podId, content, this.status, this.filterText, this.showInnerLoops, this.createdAt, this.createdBy, this.loops);
	}

	public Loop copyWithNewStatus(LoopStatus status) {
		return new Loop(this.id, this.podId, content, status, this.filterText, this.showInnerLoops, this.createdAt, this.createdBy, this.loops);
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

	public List<Loop> getLoops() {
		return loops;
	}

	public Date getCreatedAt() {
		return createdAt;
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

	public List<String> findBodyTags() {
		return findTags(getContent(), TAG_REGEX, true);
	}

	public List<String> findBodyTagsWithoutSymbols() {
		return findTags(getContent(), TAG_REGEX, false);
	}

	public List<String> findTitleTagsWithoutSymbols() {
		return findTags(getId(), TAG_REGEX, false);
	}

	public List<String> findLoopRefs() {
		return findTags(getContent(), "(@[^#/][\\w-]*)", true);
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
}
