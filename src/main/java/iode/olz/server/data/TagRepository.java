package iode.olz.server.data;

import iode.olz.server.domain.Tag;

import java.util.List;

public interface TagRepository {

	public Tag getTag(String id);
	public List<Tag> getTags();
	public Tag createTag(Tag loop);
	public boolean tagExists(String id);
}