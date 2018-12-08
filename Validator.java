package com.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Validator
{
    private static Log logger = LogFactory.getLog(Validator.class);

    private static boolean isUrl(String value)
    {
        if ((value == null) || (value.trim().length() == 0))
            return false;

        Pattern p =
                Pattern.compile("^(http[s]?|ftp|file):\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$");
        Matcher m = p.matcher(value);
        return m.matches();
    }

    private static String getServerName(String url) {
        if ((url == null) || (url.trim().length() == 0))
            return null;

        url = url.trim();
        String serverName = null;
        int spaceIdx = url.indexOf(" ");
        if (spaceIdx >= 0)
            url = url.substring(0, spaceIdx);

        if (url.startsWith("http://"))
            serverName = url.substring("http://".length());
        else if (url.startsWith("https://"))
            serverName = url.substring("https://".length());
        else if (url.startsWith("ftp:"))
            serverName = url.substring("ftp://".length());
        else if (url.startsWith("file:"))
            serverName = url.substring("file://".length());

        logger.debug("url=" + url);
        return serverName;
    }

    private static boolean isLinkInjection(String value, HttpServletRequest request)
    {
        if (!(isUrl(value)))
            return false;
        String serverName = getServerName(value);
        if ((serverName == null) || (serverName.trim().length() == 0))
        {
            return false;
        }

        return ((!(serverName.equals(request.getServerName()))) &&
                (!(serverName.equals(request.getLocalAddr()))) &&
                (!(serverName.equals(request.getLocalName()))));
    }

    private static boolean isSqlInjection(String value)
    {
        if ((value == null) || (value.trim().length() == 0))
        {
            return false;
        }
        Pattern p = Pattern.compile("^(.*)( select )(.*)( from .*)");
        Matcher m = p.matcher(value.toLowerCase());
        if (m.matches())
        {
            return true;
        }

        p = Pattern.compile("^(.*)( (and|or) )(.*)(=)(.*)");
        m = p.matcher(value.toLowerCase());
        return m.matches();
    }

    public static boolean isValidatorChar(String value, HttpServletRequest request)
    {
        if ((value == null) || (value.trim().length() == 0))
            return true;

/*        if ((value.toLowerCase().indexOf("<dataset>") >= 0) ||
                (value.toLowerCase().indexOf("<uwws:processcontent") >= 0) ||
                (value.toLowerCase().indexOf("dataStores") >= 0) ||
                (value.toLowerCase().indexOf("rowset") >= 0) ||
                (value.toLowerCase().indexOf("(self-tuning)") >= 0))
            return true;*/

        if (value.toLowerCase().indexOf("_alert") >= 0)
        {
            return true;
        }
        if (isLinkInjection(value, request))
            return false;

        if (isSqlInjection(value))
        {
            return false;
        }
        if (value.indexOf("<") >= 0)
            return false;
        if (value.indexOf(">") >= 0)
            return false;
        if (value.indexOf("(") >= 0)
            return false;
        if (value.indexOf(")") >= 0)
            return false;
//        if (value.indexOf("+") >= 0)
//            return false;
        if (value.indexOf("]") >= 0)
            return false;
        if (value.indexOf("[") >= 0)
            return false;
        if (value.indexOf("alert") >= 0)
            return false;
        if (value.indexOf("document.") >= 0)
            return false;
        if (value.indexOf("window.") >= 0)
            return false;
        if (value.indexOf(".location") >= 0)
            return false;
        if (value.indexOf("location.") >= 0)
            return false;
        if (value.indexOf(".reload") >= 0)
            return false;
        if (value.indexOf(".replace") >= 0)
            return false;
        if (value.indexOf(".href") >= 0)
            return false;
        if (value.indexOf(".history") >= 0)
            return false;
        if (value.indexOf(".host") >= 0)
            return false;
        if (value.indexOf(".open") >= 0)
            return false;
        if (value.indexOf(".url") >= 0)
            return false;
        if (value.indexOf(".search") >= 0)
            return false;
        if (value.indexOf("javascript") >= 0)
            return false;
        if (value.indexOf("<script") >= 0)
            return false;
        if (value.indexOf("VBScript") >= 0)
            return false;
        if (value.indexOf("javascript") >= 0)
            return false;
        if (value.indexOf("Javascript") >= 0)
            return false;
        if (value.indexOf("vbscript") >= 0)
            return false;
        if (value.indexOf("VBScript") >= 0)
            return false;
        if ((value.indexOf("=") >= 0) && (value.indexOf("?") >= 0) && (!(value.contains("filter"))))
            return false;
        if (value.indexOf("alert") >= 0)
            return false;
        if (value.indexOf("&lt;") >= 0)
            return false;
        if (value.indexOf("&gt;") >= 0)
            return false;
        if (value.indexOf("(") >= 0)
            return false;
        if (value.indexOf(")") >= 0)
            return false;

        return (value.split("\\\\x").length <= 3);
    }

    public static String filter(String value)
    {
        if (value == null)
            return null;

        StringBuffer result = new StringBuffer(value.length());
        for (int i = 0; i < value.length(); ++i)
            switch (value.charAt(i))
            {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                case '\'':
                    result.append("&#39;");
                    break;
                case '%':
                    result.append("&#37;");
                    break;
                case ';':
                    result.append("&#59;");
                    break;
                case '(':
                    result.append("&#40;");
                    break;
                case ')':
                    result.append("&#41;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '+':
                    result.append("&#43;");
                    break;
                default:
                    result.append(value.charAt(i));
            }


        return result.toString();
    }
}