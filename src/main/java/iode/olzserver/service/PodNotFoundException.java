package iode.olzserver.service;

public class PodNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public PodNotFoundException(String message) {
		super(message);
	}
}
