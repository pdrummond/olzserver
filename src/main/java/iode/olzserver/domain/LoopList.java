package iode.olzserver.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoopList {
	//private final Logger log = Logger.getLogger(getClass());

	private String id;
	private String loopId;
	private String name;
	private String query;
	private String comparator;
	private String sortOrder;
	private Date createdAt;
	private String createdBy;
	private List<Loop> loops;

	@JsonCreator
	public LoopList(
			@JsonProperty("id") String id, 
			@JsonProperty("loopId") String loopId, 
			@JsonProperty("name") String name, 
			@JsonProperty("query") String query,
			@JsonProperty("comparator") String comparator,
			@JsonProperty("sortOrder") String sortOrder,
			@JsonProperty("createdAt") Date createdAt, 
			@JsonProperty("createdBy") String createdBy,
			@JsonProperty("loops") List<Loop> loops) {
		this.id = id;
		this.loopId = loopId;
		this.name = name;
		this.query = query;
		this.comparator = comparator;
		this.sortOrder = sortOrder;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.loops = loops;
		if(this.loops == null) {
			this.loops = new ArrayList<Loop>();
		}
	}

	public LoopList(String id, String loopId, String name, String query, String comparator, String sortOrder, Date createdAt, String createdBy) {
		this(id, loopId, name, query, comparator, sortOrder, createdAt, createdBy, new ArrayList<Loop>());
	}

	public LoopList(String id, String loopId, String name, String query, Date createdAt, String createdBy) {
		this(id, loopId, name, query, "updatedAt", "ascending", createdAt, createdBy, new ArrayList<Loop>());
	}

	public String getId() {
		return id;
	}
	
	public String getLoopId() {
		return loopId;
	}
	
	public String getName() {
		return name;
	}

	public String getQuery() {
		return query;
	}

	public String getComparator() {
		return comparator;	
	}
	
	public String getSortOrder() {
		return sortOrder;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}
	
	public List<Loop> getLoops() {
		return loops;
	}
	

	public LoopList copyWithNewId(String id) {
		return new LoopList(id, this.loopId, this.name, this.query, this.comparator, this.sortOrder, this.createdAt, this.createdBy, this.loops);
	}

	public LoopList copyWithNewLoops(List<Loop> loops) {
		return new LoopList(this.id, this.loopId, this.name, this.query, this.comparator, this.sortOrder, this.createdAt, this.createdBy, loops);
	}
	
	@Override
	public String toString() {
		return "LoopList(name=" + Objects.toString(name) + ", query=" + Objects.toString(query) + ", numLoops=" + loops.size() + ")";
	}
}
