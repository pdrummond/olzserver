package iode.olzserver.transform;

import iode.olzserver.domain.User;
import iode.olzserver.service.UserService;
import iode.olzserver.utils.MD5Util;

public final class TransformUtils {
	
	public static final String removeOwnerPartFromTag(String tag) {
		if(tag.contains("#")) {
			return tag.split("@")[0];	
		} else {
			return tag;
		}		
	}

	public static final String removeLoopPartFromTag(String tag) {
		if(tag.contains("#")) {
			return "@" + tag.split("@")[1];
		} else {
			return "";
		}		
	}
	
	public final String getOwnerImageUrl(String tag, UserService userService) {
		String ownerName = tag.split("@")[1];
		User owner = null;

		if(ownerName != null) {
			owner = userService.getUser(ownerName);
			if(owner != null) {
				String hash = MD5Util.md5Hex(owner.getEmail().toLowerCase());
				owner = owner.copyWithNewImageUrl(String.format("http://www.gravatar.com/avatar/%s?s=15", hash));				
			}
		} 
		return owner==null?"":owner.getImageUrl();
	}
}
