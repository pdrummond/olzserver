package iode.olzserver.controller;

import iode.olzserver.error.ErrorDetails;
import iode.olzserver.error.LoopPermissionException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AbstractController {
	
	@ExceptionHandler(LoopPermissionException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorDetails handleIOException(LoopPermissionException ex) {
		return ex.getErrorDetails();
	  }

}
