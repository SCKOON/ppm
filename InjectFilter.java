package com.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


public class InjectFilter implements Filter {


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        if ((request != null) && (request.getParameterMap() != null))
        {
            Enumeration it = request.getParameterNames();
            String paramString = null;
            while (it.hasMoreElements()) {
                String paramName = (String)it.nextElement();
                Object paramValue = httpRequest.getParameter(paramName);
                String paramStr = paramName + "=" + paramValue.toString();
                if (paramString != null) {
                    paramString = paramString + "&";
                    paramString = paramString + paramStr;
                } else if (paramString == null)
                {
                    paramString = paramStr;
                }
                if (!(Validator.isValidatorChar(paramValue.toString(), httpRequest)))
                {
                    httpRequest.getRequestDispatcher("/webpage/common/error.jsp").forward(httpRequest, httpResponse);
                }
            }
        }
        chain.doFilter(request, response);

    }



    public void init(FilterConfig fConfig) throws ServletException {
    }



    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
