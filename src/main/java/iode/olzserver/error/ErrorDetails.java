package iode.olzserver.error;

public class ErrorDetails {
	
	private boolean error;
	
	private String message;

	public ErrorDetails(String message) {
		this.error = true;
		this.message = message;
	}
	
	public boolean isError() {
		return error;
	}
	
	public String getMessage() {
		return message;
	}

}
