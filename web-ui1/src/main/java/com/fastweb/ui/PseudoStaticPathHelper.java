package com.fastweb.ui;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;


public class PseudoStaticPathHelper extends UrlPathHelper {

    private static final String SUFFIX = ".html";

    @Override
    public String getRequestUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        System.err.print(uri);
        if (!uri.endsWith(SUFFIX)) {
            return super.getRequestUri(request);
        }
        ErrorPage e404 = new ErrorPage(HttpStatus.NOT_FOUND, "/static/error/index.jsp");
        String path = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (null == path) {
            int idx = uri.indexOf(SUFFIX);
            uri = uri.substring(0, idx);
            request.setAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE, uri);
            request.setAttribute(WebUtils.INCLUDE_SERVLET_PATH_ATTRIBUTE, uri);
        }

        return super.getRequestUri(request);
    }

}
