package com.filter;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SqlInjectFilter implements Filter {

    private static List<String> sensWords = new ArrayList<String>();

    private static String reg = "(<script>(.*?)</script>)|eval\\((.*?)\\)"
            + "|(?:－)|(?:%)|(?:#)|(?://)|(?:')|(?:--)|(?:;)"

            + "|(?:~)|(?:`)|(?:!)|(?:@)|(?:#)|(?:\\$)|(?:%)|(?:\\^)|(?:&)|(?:\\*)|(?:\\()|(?:\\))|(?:_)|(?:\\+)|(?:=)"
            + "|(?:\\{)|(?:\\[)|(?:})|(?:])|(?:\\|)|(?:;)|(?:\\:)|(?:\\\")|(?:')|(?:<)|(?:>)|(?:\\?)|(?:/)|(?://)|(?:\\\\)"

            + "|(/\\*(?:.)*?\\*/)|(\\b(chr|mid|truncate|sitename|netuser|xp_cmdshell|cmd|shell|like'|table|from|grant|use|group|group_concat|concat"
            + "|column|column_name|information_schema_columns|information|schema|columns|table_schema|order|by"
            + "|select|update|union|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into"
            + "|drop|execute|in|where|like|join|create|modify|rename|alter|call|cas|script|eval|expression|javascript|vbscript|view-source"
            + "|oncontrolselect|oncopy|oncut|ondataavailable|ondatasetchanged|ondatasetcomplete|ondblclick|ondeactivate|ondrag|ondragend"
            + "|ondragenter|ondragleave|ondragover|ondragstart|ondrop|onerror=|onerroupdate|onfilterchange|onfinish|onfocus|onfocusin|onfocusout"
            + "|onhelp|onkeydown|onkeypress|onkeyup|onlayoutcomplete|onload|onlosecapture|onmousedown|onmouseenter|onmouseleave|onmousemove"
            + "|onmousout|onmouseover|onmouseup|onmousewheel|onmove|onmoveend|onmovestart|onabort|onactivate|onafterprint|onafterupdate|onbefore"
            + "|onbeforeactivate|onbeforecopy|onbeforecut|onbeforedeactivate|onbeforeeditocus|onbeforepaste|onbeforeprint|onbeforeunload|onbeforeupdate"
            + "|onblur|onbounce|oncellchange|onchange|onclick|oncontextmenu|onpaste|onpropertychange|onreadystatechange|onreset|onresize|onresizend"
            + "|onresizestart|onrowenter|onrowexit|onrowsdelete|onrowsinserted|onscroll|onselect|onselectionchange|onselectstart|onstart|onstop|onsubmit"
            + "|onunload|css|script)\\b)";


    private List<String> referers = new ArrayList<String>();

    private Set<String> ignoreFields = new LinkedHashSet(){
        {
            add("datagrid");
            add("field");
            add("searchColums");
            add("undefined");
            add("sqlbuilder");
            add("complexSqlbuilder");
            add("functionUrl");
        }
    };

    public void destroy() {

    }


    private boolean isRefererOk(String referer) {
        if(referers.size()>0) {
            for (String ref : referers) {
                if (referer.startsWith(ref)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String xrw = request.getHeader("X-Requested-With");
        //response.addHeader("Set-Cookie", "Path=/;HttpOnly");
        String referer = request.getHeader("Referer");
        if (referer != null && !isRefererOk(referer))/*!referer.startsWith(baseUrl)*/ {
            if (xrw != null && xrw.equals("XMLHttpRequest")) {
                // ajax
                response.getWriter().print("{errInject: true}");
            } else {
                request.getRequestDispatcher("/webpage/common/error.jsp").forward(request, response);
            }
            return;
        }

        int p = request.getRequestURI().lastIndexOf("/");
        String pa = request.getRequestURI().substring(p + 1);

        if (!esurl.contains(pa)) {
            Set<String> keys = request.getParameterMap().keySet();
            for (String key : keys) {
                if(ignoreFields.contains(key)){
                    continue;
                }
                String value = request.getParameter(key);
//                if ("areaName".equals(key)) {
//                    value = new String(value.getBytes("iso-8859-1"), "GBK");
//                }
                if (sqlPattern.matcher(value).find()) {
                    response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    if (xrw != null && xrw.equals("XMLHttpRequest")) {
                        // ajax
                        response.getWriter().print("{errInject: true }");
                    } else {
                        request.getRequestDispatcher("/webpage/common/error.jsp").forward(request, response);
                    }
                    return;
                }
            }
        }
        chain.doFilter(req, res);

    }


    public void init(FilterConfig filterConfig) throws ServletException {

        String rs = filterConfig.getInitParameter("referer");
        if (!StringUtils.isBlank(rs)) {
            referers = Arrays.asList(rs.split(","));
        }

    }

	
	/*public void init(FilterConfig conf) throws ServletException {
        reg = conf.getInitParameter("sensitive-words");
		System.err.println(reg);
	}*/


    //    private String lastUrl = "";
    private String LAST_ACCESS_TIME = "lastAccessTime";
    private String LAST_URL = "lastUrl";
    private String DELAY = "delay";
    private String SAME_URL_COUNT = "sameUrlCount";

//    float delay = 0.5f;
//    int sameUrlCount = 0;
    
 /*   private int isFrq(ServletRequest req, ServletResponse res)
    {
        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession();
        
        String nowUrl = request.getRequestURL().toString();
        
//        System.out.println(" nowUrl " + nowUrl);
        if(!StringUtil.isBlank(nowUrl))
        {
            if(nowUrl.trim().toLowerCase(Locale.CHINESE).contains("are-you-alive-ping-from-workshop"))
            {
                return 0;
            }
            if(nowUrl.trim().endsWith("mapTest.jsp")
                    ||nowUrl.trim().endsWith("index.jsp")
                    ||nowUrl.trim().endsWith("login.jsp")
                    ||nowUrl.trim().endsWith("sysmanage_AffichePromulgate_pafficheCount.action")
                    ||nowUrl.trim().endsWith("sysmanage_Top_clearSession.action")
            		)
            {
                return 0;
            }
            if(!(nowUrl.trim().toLowerCase(Locale.CHINESE).endsWith(".action") 
                    || nowUrl.trim().toLowerCase(Locale.CHINESE).endsWith(".jsp"))
                    )
            {
//                System.out.println(" nowUrl pass " + nowUrl);
                return 0;
            }
        }
            
       
        
//        System.out.println("nowUrl " + nowUrl);
        
//        if(StringUtil.isBlank(nowUrl))
//        {
//            return 0;
//        }
//        
        
        Object objlasturl = session.getAttribute(LAST_URL);
        if(objlasturl == null)
        {
//            lastUrl = nowUrl;
            session.setAttribute(LAST_URL, nowUrl);
            session.setAttribute(LAST_ACCESS_TIME, new Date());
            
//            System.out.println("lastUrl null 0 ");
            
            return 0;
        }
        String lastUrl = objlasturl.toString();
        
//        System.out.println("lastUrl " + lastUrl);
        
        Object objLAT = session.getAttribute(LAST_ACCESS_TIME);
        
        if(objLAT == null)
        {
//            lastUrl = nowUrl;
            session.setAttribute(LAST_URL, nowUrl);
            session.setAttribute(LAST_ACCESS_TIME, new Date());
            
//            System.out.println("objLAT null 0 ");
            
            return 0;
        }
        
        Date dateLAT = (Date)objLAT;
        
        float minDelay = 0.2f;
        float delay = minDelay;
        Object objdelay = session.getAttribute(DELAY);
        if(objdelay != null)
        {
            delay = Float.parseFloat(objdelay.toString());
        }
        
        Date beforeDate = new Date(new Date().getTime() - (int)(delay * 1000)); // ��ǰʱ��ǰn��
        
        
        if(!StringUtil.isBlank(lastUrl) 
                && lastUrl.equals(nowUrl) 
                && dateLAT.getTime() > beforeDate.getTime() )
        {
            //�ϴη��ʵ�url�ͱ��η��ʵ�url��ͬ,�ҷ���ʱ����1s֮��
            
//            if(delay < 60 *60)
//            {
//                if(delay < 1)
//                {
//                    delay = 1;
//                }
//                else
//                {
//                    delay *= 2;
//                }
//            }
            delay = 30f;
            session.setAttribute(LAST_URL, nowUrl);
            session.setAttribute(LAST_ACCESS_TIME, new Date());
            session.setAttribute(DELAY, delay);
            
            System.out.println("���ʴ˲�������Ƶ������������������������� " + delay + " ����ٽ��д˲���!" + request.getRequestURL());
            
            return (int)delay;
        }
        
        
        
        
        int sameUrlCount = 0;
        if(!StringUtil.isBlank(lastUrl) 
                && !lastUrl.equals(nowUrl))
        {
            sameUrlCount = 0;
            session.setAttribute(SAME_URL_COUNT, sameUrlCount);
        }

        Object objsameurlcount = session.getAttribute(SAME_URL_COUNT);
        if(objsameurlcount != null)
        {
            sameUrlCount = Integer.parseInt(objsameurlcount.toString());
        }
        
        Date tempBeforeDate = new Date(new Date().getTime() - 1 * 1000);
        if(!StringUtil.isBlank(lastUrl) 
                && lastUrl.equals(nowUrl)
                && dateLAT.getTime() > tempBeforeDate.getTime() )
        {
            sameUrlCount ++;
            session.setAttribute(SAME_URL_COUNT, sameUrlCount);
//            System.out.println("sameUrlCount ++ ");
            if(sameUrlCount >= 3)
            {
                // ����n�η��ʸò���,��ÿ�η��ʼ������m��
                delay = 30f;
                System.out.println("���ʴ˲�������Ƶ������������������������� " + delay + " ����ٽ��д˲���!"  );
                
//                System.out.println("sameUrlCount " + sameUrlCount);
                session.setAttribute(LAST_URL, nowUrl);
                session.setAttribute(LAST_ACCESS_TIME, new Date());
                session.setAttribute(DELAY, delay);
                
                
//                b2 = true;
                return (int)delay;
            }
        }
        else
        {
//            System.out.println("bbbbbb22222");
            sameUrlCount = 0;
            session.setAttribute(SAME_URL_COUNT, sameUrlCount);
//            b2 = false;
        }
//        if(!StringUtil.isBlank(lastUrl) 
//                && lastUrl.equals(nowUrl)
//                && dateLAT.getTime() <= beforeDate.getTime() 
//                && b2)
//        {
//            System.out.println("bbbbbb22222");
//            sameUrlCount = 0;
//            b2 = false;
//        }
        
        
        
//        System.out.println("lastUrl " + lastUrl + " nowUrl " + nowUrl );
        
        session.setAttribute(LAST_ACCESS_TIME, new Date());
        lastUrl = nowUrl;
        session.setAttribute(LAST_URL, lastUrl);
        delay = minDelay;
        session.setAttribute(DELAY, delay);
        return 0;
    }*/

    private static ArrayList<String> esurl = new ArrayList<String>() {
        {
            add("login.jsp");
            add("loginController.do");
        }
    };

}