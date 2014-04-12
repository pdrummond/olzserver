package iode.olz.server.service;

public class LoopNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public LoopNotFoundException(String message) {
		super(message);
	}
}
