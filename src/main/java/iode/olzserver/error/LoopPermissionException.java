package iode.olzserver.error;

public class LoopPermissionException extends RuntimeException {
	
	private ErrorDetails error;

	public LoopPermissionException(String message) {
		super(message);
		error = new ErrorDetails(message);
	}
	
	public ErrorDetails getErrorDetails() {
		return error;
	}
}
