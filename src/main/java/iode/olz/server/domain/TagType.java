package iode.olz.server.domain;

public enum TagType {
	INVALID(0),
	HASH_TAG(1),
	USER_TAG(2);

	private final int id;
	private TagType(int id) {
		this.id = id;
	}


	public static TagType fromValue(int value) {
		for(TagType e : values()){
			if(e.id == value) {
				return e;
			}
		}
		return TagType.INVALID;
	}

	public int getId() {
		return id;
	}
}
