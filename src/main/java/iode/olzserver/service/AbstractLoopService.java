package iode.olzserver.service;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AbstractLoopService {
	private final Logger log = Logger.getLogger(getClass());
	
	@ExceptionHandler(LoopNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	public final String handleLoopNotFoundException(Exception ex) {
		if (log.isDebugEnabled()) {
			log.debug("handleLoopNotFoundException(message=" + ex.getMessage()+ ")");
		}
		return ex.getMessage();
	}
	
}
