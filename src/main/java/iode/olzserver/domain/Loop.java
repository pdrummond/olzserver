package iode.olzserver.domain;

import iode.olzserver.service.Transform;
import iode.olzserver.xml.utils.XmlLoop;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.dsig.TransformException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Loop {
	private final Logger log = Logger.getLogger(getClass());

	private String id;
	private Long sliceId;
	private String content;
	private String createdBy;	
	private Date createdAt;
	private List<Loop> loops;

	@JsonCreator
	public Loop(@JsonProperty("id") String id, @JsonProperty("sliceId") Long sliceId, @JsonProperty("content") String content, @JsonProperty("createdAt") Date createdAt, @JsonProperty("createdBy") String createdBy) {
		this(id, sliceId, content, createdAt, createdBy, Collections.<Loop>emptyList());
	}
	
	public Loop(String id) {
		this(id, null, "", null, null, Collections.<Loop>emptyList());
	}

	public Loop(String id, Long sliceId, String content, Date createdAt, String createdBy, List<Loop> loops) {
		this.id = id;
		this.sliceId = sliceId;
		this.content = content;
		this.createdAt = createdAt;
		this.loops = loops;
	}

	public Loop(String id, String content) {
		this(id, null, content, new Date(), null);
	}

	public Loop copyWithNewId(String id) {
		return new Loop(id, this.sliceId, this.content, this.createdAt, this.createdBy, this.loops);
	}

	public Loop copyWithNewSliceId(Long sliceId) {
		return new Loop(this.id, sliceId, this.content, this.createdAt, this.createdBy, this.loops);
	}

	public Loop copyWithNewInnerLoops(List<Loop> loops) {
		return new Loop(this.id, this.sliceId, this.content, this.createdAt, this.createdBy, loops);
	}
	
	public Loop copyWithNewContent(String content) {
		return new Loop(this.id, this.sliceId, content, this.createdAt, this.createdBy, loops);
	}
	
	public String getId() {
		return id;
	}

	public Long getSliceId() {
		return sliceId;
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
		return String.format("Loop(id=%s, content=%s)",  getId(), StringUtils.abbreviate(getContent(), 40)); 
	}

	public XmlLoop xml() {
		return new XmlLoop(this);
	}
	
	public Loop convertLoopToHtml() {
		Loop loop = null;
		if(log.isDebugEnabled()) {
			log.debug("convertLoopToHtml(" + this + ")");
		}	
		try {
			loop = copyWithNewContent(Transform.getInstance().transform("loop-xml-to-html", getContent()));
			if(log.isDebugEnabled()) {
				log.debug("loop=" + loop);
			}	
		} catch (TransformException e) {
			throw new RuntimeException("Error converting loop to HTML", e);
		}
		return loop;
	}

	public Loop convertLoopToXml() {
		Loop loop = null;
		if(log.isDebugEnabled()) {
			log.debug("convertLoopToXml(" + this + ")");
		}
		if(log.isDebugEnabled()) {
			log.debug("HTML content: " + getContent());
		}
		try {
			loop = copyWithNewContent(Transform.getInstance().transform("loop-html-to-xml", getContent()));
			if(log.isDebugEnabled()) {
				log.debug("XML content: " + loop.getContent());
			}
		} catch (TransformException e) {
			throw new RuntimeException("Error converting loop to XML", e);
		}
		return loop;
	}
	
	/*public List<String> extractSidTags() {
		//Three patterns, one for each tag type: hashtag, then usertag, then slashtag
		//For each pattern: first the tag identifier (#), then omit other tag identifiers ([^@/]) then a word including '-').  
		Pattern p = Pattern.compile("(#[^@/~][\\w-]*)|(~[^#/@][\\w-]*)|(/[^#@~][\\w-]*)");
		Matcher m = p.matcher(getSid());
		List<String> tags = new ArrayList<String>();
		while(m.find()) {
			String tag = m.group();
			if(tag.startsWith("/")) {
				tag = tag.replace("/", "#");
			}
			if(tag.startsWith("~")) {
				tag = tag.replace("~", "@");
			}
			tags.add(tag);
		}
		return ImmutableSet.copyOf(tags).asList(); //ensure no duplicates
	}

	public String extractSidOwner() {
		return extractSidOwner(getSid());
	}
	
	public static String extractSidOwner(String sid) {
		Pattern p = Pattern.compile("(@[^#/~][\\w-]*)");
		Matcher m = p.matcher(sid);
		String owner = null;
		while(m.find()) {
			owner = m.group();
		}
		return owner;
	}

	public static boolean isSidOwner(String sid, String owner) {
		String o = extractSidOwner(sid);
		if(o != null) {
			return o.equals(owner);
		} else {
			return false;
		}
	}*/	
}
