package com.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XSRFFilter extends HttpServlet implements Filter {

    @Override
    public void doFilter(ServletRequest servletrequest, ServletResponse servletresponse,FilterChain chain)throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletrequest;
        HttpServletResponse response = (HttpServletResponse) servletresponse;

        String referer = request.getHeader("referer");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(request.getScheme()).append("://").append(request.getServerName());
        if(referer != null && !referer.equals("") ){
            if(referer.lastIndexOf(String.valueOf(stringBuffer)) != 0){
            request.getRequestDispatcher("/webpage/common/error.jsp").forward(request, response);
            }
        }
        chain.doFilter(servletrequest, servletresponse);
    }



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
    }


    public void destroy()
    {
    }

}
