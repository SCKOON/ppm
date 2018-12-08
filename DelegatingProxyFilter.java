package com.filter;

import org.springframework.session.SessionRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;


import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 重写拦截器的拦截逻辑
 */
public class DelegatingProxyFilter extends DelegatingFilterProxy {

    private Set<String> excludesPattern;

    private String contextPath;

//    private Filter filterWrapper;

    private SessionRepository jdbcSessionRepository = null;

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    protected Filter initDelegate(WebApplicationContext wac) throws ServletException {
//        this.sessionRepository = wac.getBean("sessionRepository", SessionRepository.class);
        this.jdbcSessionRepository = wac.getBean("sessionRepository", SessionRepository.class);
//        this.sessionRepositoryFilter = wac.getBean("springSessionRepositoryFilter", SessionRepositoryFilter.class);
        this.contextPath = getContextPath(getFilterConfig().getServletContext());
        String exclusions = getFilterConfig().getInitParameter("exclusions");
        if (exclusions != null && exclusions.trim().length() != 0) {
            excludesPattern = new HashSet<String>(Arrays.asList(exclusions.split("\\s*,\\s*")));
        }
        return super.initDelegate(wac);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //redis是否可达 redis服务是否崩溃
        String requestURI = getRequestURI(req);
        if (isExclusion(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        String sessionid = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("SESSION")) {
                    sessionid = cookie.getValue();
                    break;
                }
            }
            if (sessionid != null) {
                //如果redi服务可以用，则要判断是断了重连还是一直是可用
                if (jdbcSessionRepository.getSession(sessionid) != null) {
                    //接着走数据库
                    super.doFilter(request, response, filterChain);
                    return;
                } else {
                    //浏览器有session字段但是数据库没有session
                    if (req.getServletPath().equals("/loginController.do")) {
                        //如果走的是登录和校验
                        if (req.getQueryString() != null && (req.getQueryString().equals("login") || req.getQueryString().equals("checkuser")|| req.getQueryString().equals("logout"))) {
                            filterChain.doFilter(request, response);
                            return;
                        }
                    }
                    returnToLogin(req, res);
                    return;
                }
            } else {
                if (req.getServletPath().equals("/loginController.do")) {
                    //如果走的是登录和校验
                    if (req.getQueryString() != null && (req.getQueryString().equals("login") || req.getQueryString().equals("checkuser")|| req.getQueryString().equals("logout"))) {
                        filterChain.doFilter(request, response);
                        return;
                    } else {
                        returnToLogin(req, res);
                    }
                }
            }
        } else {
            if (req.getServletPath().equals("/loginController.do")) {
                //如果走的是登录和校验
                if (req.getQueryString() != null && (req.getQueryString().equals("login") || req.getQueryString().equals("checkuser") || req.getQueryString().equals("logout"))) {
                    filterChain.doFilter(request, response);
                    return;
                }
            } else {
                returnToLogin(req, res);
            }
            return;
        }
    }

    private void returnToLogin(HttpServletRequest req, HttpServletResponse res) {
        res.setContentType("text/html;charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        String projectName = req.getContextPath();
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(res.getOutputStream());
            String msg = "session has expiresd, please login in again!";
            msg = new String(msg.getBytes("UTF-8"));

            out.write("<meta http-equiv='Content-Type' content='text/html';charset='UTF-8'>");
            out.write("<script>");
            out.write("top.location.href = '" + projectName + "/loginController.do?login'; ");
            out.write("alert('" + msg + "');");
            out.write("</script>");
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getRequestURI(HttpServletRequest request) {
        return request.getServletPath();
    }

    public boolean isExclusion(String requestURI) {
        if (excludesPattern == null) {
            return false;
        }

        if (contextPath != null && requestURI.startsWith(contextPath)) {
            requestURI = requestURI.substring(contextPath.length());
            if (!requestURI.startsWith("/")) {
                requestURI = "/" + requestURI;
            }
        }

        for (String pattern : excludesPattern) {
            if (matches(pattern, requestURI)) {
                return true;
            }
        }

        return false;
    }

    public boolean matches(String pattern, String source) {
        if (pattern == null || source == null) {
            return false;
        }
        pattern = pattern.trim();
        source = source.trim();
        if (pattern.endsWith("*")) {
            // pattern: /druid* source:/druid/index.html
            int length = pattern.length() - 1;
            if (source.length() >= length) {
                if (pattern.substring(0, length).equals(source.substring(0, length))) {
                    return true;
                }
            }
        } else if (pattern.startsWith("*")) {
            // pattern: *.html source:/xx/xx.html
            int length = pattern.length() - 1;
            if (source.length() >= length && source.endsWith(pattern.substring(1))) {
                return true;
            }
        } else {
            // pattern: /druid/index.html source:/druid/index.html
            if (pattern.equals(source)) {
                return true;
            }
        }
        return false;
    }

    private static String getContextPath_2_5(ServletContext context) {
        String contextPath = context.getContextPath();

        if (contextPath == null || contextPath.length() == 0) {
            contextPath = "/";
        }

        return contextPath;
    }

    public static String getContextPath(ServletContext context) {
        if (context.getMajorVersion() == 2 && context.getMinorVersion() < 5) {
            return null;
        }

        try {
            return getContextPath_2_5(context);
        } catch (NoSuchMethodError error) {
            return null;
        }
    }
}
