package iode.olzserver.domain;

import iode.olzserver.service.Transform;
import iode.olzserver.xml.utils.XmlLoop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.crypto.dsig.TransformException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

public class Loop {
	private final Logger log = Logger.getLogger(getClass());

	private String uid;
	private String sid;
	private String content;
	private String createdBy;	
	private Date createdAt;
	private List<Loop> loops;

	@JsonCreator
	public Loop(@JsonProperty("uid") String uid, @JsonProperty("sid") String sid, @JsonProperty("content") String content, @JsonProperty("created_at") Date createdAt, @JsonProperty("created_by") String createdBy) {
		this(uid, sid, content, createdAt, createdBy, Collections.<Loop>emptyList());
	}
	
	public Loop(String sid) {
		this(null, sid, "", null, null, Collections.<Loop>emptyList());
	}


	public Loop(String uid, String sid, String content, Date createdAt, String createdBy, List<Loop> loops) {
		this.uid = uid;
		this.sid = sid;
		this.content = content;
		this.createdAt = createdAt;
		this.loops = loops;
	}

	public Loop(String sid, String content) {
		this(UUID.randomUUID().toString(), sid, content, new Date(), null);
	}


	public Loop copyWithNewUid(String uid) {
		return new Loop(uid, this.sid, this.content, this.createdAt, this.createdBy, this.loops);
	}

	public Loop copyWithNewSid(String sid) {
		return new Loop(this.uid, sid, this.content, this.createdAt, this.createdBy, this.loops);
	}

	public Loop copyWithNewInnerLoops(List<Loop> loops) {
		return new Loop(this.uid, this.sid, this.content, this.createdAt, this.createdBy, loops);
	}
	
	public Loop copyWithNewContent(String content) {
		return new Loop(this.uid, this.sid, content, this.createdAt, this.createdBy, loops);
	}
	
	public String getUid() {
		return uid;
	}

	public String getSid() {
		return sid;
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
		return String.format("Loop(sid=%s, content=%s)",  getSid(), StringUtils.abbreviate(getContent(), 40)); 
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
	
	public List<String> extractSidTags() {
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
	}	
}
