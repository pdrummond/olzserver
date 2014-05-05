package iode.olzserver.data;

import iode.olzserver.domain.Slice;

public interface SliceRepository {
	public Slice getSlice(Long id);
	public Slice getSliceByName(String name);
	public Slice createSlice(String sliceName);
	public Slice updateSlice(Slice slice);
	public Long getAndUpdateSliceNextNumber(Long sliceId);
}