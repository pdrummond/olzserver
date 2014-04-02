package iode.olz.server.controller;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

//@Component
public class SimpleCORSFilter implements Filter {
	private final Logger log = Logger.getLogger(getClass());
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if(log.isDebugEnabled()) {
			log.debug("doFilter()");
		}
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
		response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		
		if(log.isDebugEnabled()) {
			log.debug("Response Access-Control-Allow-Origin:" + response.getHeader("Access-Control-Allow-Origin"));
		}
		
		chain.doFilter(req, res);
	}

	public void init(FilterConfig filterConfig) {
		
		
		
	}

	public void destroy() {
		
	}

}
