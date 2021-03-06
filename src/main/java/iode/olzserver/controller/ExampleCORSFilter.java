package iode.olzserver.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Component
public class ExampleCORSFilter implements Filter{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {       
        HttpServletRequest req = (HttpServletRequest)request;                                    
        HttpServletResponse res = (HttpServletResponse)response;   

        if(req.getHeader("Origin") != null){
            res.addHeader("Access-Control-Allow-Origin", "*");
        }

        if("OPTIONS".equals(req.getMethod())){
            res.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
            res.addHeader("Access-Control-Expose-Headers", "X-Cache-Date, X-Atmosphere-tracking-id");
            res.addHeader("Access-Control-Allow-Headers", 
                    "Origin, Content-Type, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-Tracking-id, X-Atmosphere-Transport");
            res.addHeader("Access-Control-Max-Age", "-1");
        }                                               
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() { }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }    
} 
