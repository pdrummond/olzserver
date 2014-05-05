package iode.olzserver.service;

public class SliceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public SliceNotFoundException(String message) {
		super(message);
	}
}
