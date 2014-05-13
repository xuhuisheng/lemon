package com.mossle.ext.rest;

import java.io.IOException;
import java.io.InputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.ApplicationContextHelper;
import com.mossle.core.util.IoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;

public class RestFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(RestFilter.class);
    private JsonMapper jsonMapper = new JsonMapper();

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        ApplicationContext ctx = ApplicationContextHelper
                .getApplicationContext();
        Map<String, Object> map = ctx.getBeansWithAnnotation(Path.class);

        for (Object object : map.values()) {
            Class clz = object.getClass();

            for (Method method : clz.getDeclaredMethods()) {
                if (this.matches(request, method)) {
                    Map<String, String> parameters = parseBody(request
                            .getInputStream());
                    this.invokeMethod(request, res, object, method, parameters);

                    return;
                }
            }
        }

        filterChain.doFilter(req, res);
    }

    public Map<String, String> parseBody(InputStream inputStream)
            throws IOException {
        String body = IoUtils.readString(inputStream);
        body = URLDecoder.decode(body, "UTF-8");
        logger.debug("body : {}", body);

        Map<String, String> parameters = new HashMap<String, String>();

        for (String text : body.split("&")) {
            logger.debug("text : {}", text);

            if (text.indexOf("=") == -1) {
                continue;
            }

            int index = text.indexOf("=");
            String key = text.substring(0, index);
            String value = text.substring(index + 1);

            parameters.put(key, value);
        }

        return parameters;
    }

    public void invokeMethod(HttpServletRequest request, ServletResponse res,
            Object object, Method method, Map<String, String> parameters) {
        try {
            List arguments = new ArrayList();

            Annotation[][] annotationArray = method.getParameterAnnotations();
            Class[] parameterTypeArray = method.getParameterTypes();

            for (int i = 0; i < annotationArray.length; i++) {
                Annotation[] annotations = annotationArray[i];

                for (Annotation annotation : annotations) {
                    String name = null;
                    String type = null;
                    String value = null;

                    if (annotation instanceof PathParam) {
                        name = ((PathParam) annotation).value();
                        type = "path";
                        value = this.getPathParam(request, name, method);
                    } else if (annotation instanceof QueryParam) {
                        name = ((QueryParam) annotation).value();
                        type = "query";
                        value = request.getParameter(name);
                    } else if (annotation instanceof FormParam) {
                        name = ((FormParam) annotation).value();
                        type = "form";
                        value = parameters.get(name);
                    } else if (annotation instanceof HeaderParam) {
                        name = ((HeaderParam) annotation).value();
                        type = "header";
                        value = request.getHeader(name);
                    }

                    if (value != null) {
                        if (parameterTypeArray[i] == String.class) {
                            arguments.add(value);
                        } else if ((parameterTypeArray[i] == Long.class)
                                || (parameterTypeArray[i] == long.class)) {
                            arguments.add(Long.parseLong(value));
                        } else if ((parameterTypeArray[i] == Integer.class)
                                || (parameterTypeArray[i] == int.class)) {
                            arguments.add(Integer.parseInt(value));
                        }
                    } else {
                        if (parameterTypeArray[i] == String.class) {
                            arguments.add(null);
                        }
                    }
                }
            }

            logger.debug("{}, {}, {}", object, method, arguments);

            Object result = method.invoke(object, arguments.toArray());
            res.setContentType(MediaType.APPLICATION_JSON);
            res.getOutputStream().write(
                    jsonMapper.toJson(result).getBytes("UTF-8"));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public boolean matches(HttpServletRequest request, Method method) {
        if ("GET".equalsIgnoreCase(request.getMethod())
                && (method.getAnnotation(GET.class) == null)) {
            return false;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())
                && (method.getAnnotation(POST.class) == null)) {
            return false;
        }

        if ("PUT".equalsIgnoreCase(request.getMethod())
                && (method.getAnnotation(PUT.class) == null)) {
            return false;
        }

        if ("DELETE".equalsIgnoreCase(request.getMethod())
                && (method.getAnnotation(DELETE.class) == null)) {
            return false;
        }

        logger.debug("{}", request.getRequestURI());

        String prefix = request.getContextPath() + "/rs";
        String requestPath = request.getRequestURI().substring(prefix.length());
        logger.debug("{}", requestPath);

        String path = "/";

        if (method.getDeclaringClass().getAnnotation(Path.class) != null) {
            path += method.getDeclaringClass().getAnnotation(Path.class)
                    .value();
        }

        if (method.getAnnotation(Path.class) != null) {
            path += ("/" + method.getAnnotation(Path.class).value());
        }

        logger.debug("{}, {}", method, path);

        String[] src = requestPath.split("/");
        String[] dest = path.split("/");

        if (src.length != dest.length) {
            return false;
        }

        for (int i = 0; i < src.length; i++) {
            if (!this.matchPath(src[i], dest[i])) {
                return false;
            }
        }

        return true;
    }

    public boolean matchPath(String src, String dest) {
        if (src.equals(dest)) {
            return true;
        }

        if (dest.startsWith("{") && dest.endsWith("}")) {
            return true;
        }

        return false;
    }

    public String getPathParam(HttpServletRequest request, String name,
            Method method) {
        String prefix = request.getContextPath() + "/rs";
        String requestPath = request.getRequestURI().substring(prefix.length());

        String path = "/";

        if (method.getDeclaringClass().getAnnotation(Path.class) != null) {
            path += method.getDeclaringClass().getAnnotation(Path.class)
                    .value();
        }

        if (method.getAnnotation(Path.class) != null) {
            path += ("/" + method.getAnnotation(Path.class).value());
        }

        String[] src = requestPath.split("/");
        String[] dest = path.split("/");

        for (int i = 0; i < dest.length; i++) {
            if (dest[i].equals("{" + name + "}")) {
                return src[i];
            }
        }

        return null;
    }
}
