package com.vaadin.cdi.internal;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Created by kumm on 2016.11.07..
 */
@WebFilter(urlPatterns = "/*", asyncSupported = true)
public class CounterFilter implements javax.servlet.Filter {

    @Inject
    Counter counter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getParameter("resetCounts") != null) {
            counter.reset();
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
