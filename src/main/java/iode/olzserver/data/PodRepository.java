package iode.olzserver.data;

import iode.olzserver.domain.Pod;

public interface PodRepository {
	public Pod getPod(Long id);
	public Pod getPodByName(String name);
	public Pod createPod(String podName);
	public Pod updatePod(Pod pod);
	public Long getAndUpdatePodNextNumber(Long podId);
}
