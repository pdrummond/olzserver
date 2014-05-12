package iode.olzserver.domain;

public class LoopHandle {
	private String loopId = null;
	private String podName = null;
	
	public LoopHandle(String handle) {
		if(handle.contains("#") && handle.contains("@")) {
			loopId = handle.split("@")[0];
			podName = "@" + handle.split("@")[1];
		} else if(handle.contains("#")) {
			loopId = handle;			
		} else if(handle.contains("@")) {
			podName = handle;
		}

		if(loopId == null) {
			loopId = podName;
		} else if(podName == null) {
			podName = "@iode"; //TEMP: 'CURRENT POD' HARDCODDED FOR NOW;
		}		
	}

	public String getLoopId() {
		return loopId;
	}

	public String getPodName() {
		return podName;
	}
	
	@Override 
	public String toString() {
		return String.format("%s%s", loopId, podName);
	}
	
}
