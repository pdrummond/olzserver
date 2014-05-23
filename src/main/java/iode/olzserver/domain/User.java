package iode.olzserver.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	//private final Logger log = Logger.getLogger(getClass());

	private String userId;
	private String email; 	
	private Date createdAt;

	@JsonCreator
	public User(@JsonProperty("userId") String userId, @JsonProperty("email") String email, @JsonProperty("created_at") Date createdAt) {
		this.userId = userId;		
		this.email = email;
		this.createdAt = createdAt;
	}

	public String getUserId() {
		return userId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
}
