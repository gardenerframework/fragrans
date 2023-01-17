package com.jdcloud.gardener.fragrans.api.test.cases.servlet;

import com.jdcloud.gardener.fragrans.api.standard.error.exception.server.InternalServerErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class ExceptionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String exception = httpServletRequest.getParameter("exception");
        String status = httpServletRequest.getParameter("status");
        if (StringUtils.hasText(exception) && httpServletRequest.getRequestURI().equals("/filter/exception")) {
            try {
                if (status == null)
                    throw (RuntimeException) Class.forName(exception).newInstance();
                else {
                    throw new ResponseStatusException(HttpStatus.valueOf(Integer.parseInt(status)), HttpStatus.valueOf(Integer.parseInt(status)).getReasonPhrase(), new RuntimeException());
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new InternalServerErrorException();
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
