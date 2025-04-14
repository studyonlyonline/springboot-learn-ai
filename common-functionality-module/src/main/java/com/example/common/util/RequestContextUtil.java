package com.example.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Utility class for accessing request context information from anywhere in the application.
 */
@Component
public class RequestContextUtil {

    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    private static final String REQUEST_TIMESTAMP_ATTRIBUTE = "requestTimestamp";
    private static final String USER_AGENT_ATTRIBUTE = "userAgent";
    private static final String REQUEST_PATH_ATTRIBUTE = "requestPath";

    /**
     * Get the current request if available.
     *
     * @return Optional containing the current request or empty if not available
     */
    public Optional<HttpServletRequest> getCurrentRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * Get the request ID for the current request.
     *
     * @return the request ID or null if not available
     */
    public String getRequestId() {
        return getCurrentRequest()
                .map(request -> (String) request.getAttribute(REQUEST_ID_ATTRIBUTE))
                .orElse(null);
    }

    /**
     * Get the timestamp for the current request.
     *
     * @return the request timestamp or null if not available
     */
    public Object getRequestTimestamp() {
        return getCurrentRequest()
                .map(request -> request.getAttribute(REQUEST_TIMESTAMP_ATTRIBUTE))
                .orElse(null);
    }

    /**
     * Get the user agent for the current request.
     *
     * @return the user agent or null if not available
     */
    public String getUserAgent() {
        return getCurrentRequest()
                .map(request -> (String) request.getAttribute(USER_AGENT_ATTRIBUTE))
                .orElse(null);
    }

    /**
     * Get the request path for the current request.
     *
     * @return the request path or null if not available
     */
    public String getRequestPath() {
        return getCurrentRequest()
                .map(request -> (String) request.getAttribute(REQUEST_PATH_ATTRIBUTE))
                .orElse(null);
    }
}
