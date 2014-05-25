package iode.olzserver.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	//private final Logger log = Logger.getLogger(getClass());

	private String userId;
	private String email; 	
	private String imageUrl; 	
	private Date createdAt;

	@JsonCreator
	public User(@JsonProperty("userId") String userId, @JsonProperty("email") String email, @JsonProperty("imageUrl") String imageUrl, @JsonProperty("createdAt") Date createdAt) {
		this.userId = userId;		
		this.email = email;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
	}
	
	public User(String userId, String email, Date createdAt) {
		this(userId, email, null, createdAt);
	}

	public String getUserId() {
		return userId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public User copyWithNewImageUrl(String imageUrl) {
		return new User(this.userId, this.email, imageUrl, this.createdAt);
	}
}
