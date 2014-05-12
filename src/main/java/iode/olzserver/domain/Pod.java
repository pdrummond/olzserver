package iode.olzserver.domain;

import java.util.Date;

public class Pod {

	private Long id;
	private String name;
	private String createdBy;	
	private Date createdAt;

	public Pod(Long id, String name) {
		this(id, name, new Date(), null);
	}

	public Pod(Long id, String name, Date createdAt, String createdBy) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Pod copyWithNewName(String name) {
		return new Pod(this.id, name);
	}

	@Override
	public String toString() {
		return "Pod(name=" + name + ",id=" + id + ")"; 
	}
}
