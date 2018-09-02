package in.sant.gateway.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class UrlRewriteFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        return isUserAccountRequest();
    }

    @Override
    public Object run() throws ZuulException {
        StringBuffer requestURL = getRequestURL();
        String username = extractName(requestURL);
        boolean filterSavings = isFilterSavings(requestURL);
        String newUrl = buildNewUrl(requestURL, username);
        RequestContext currentContext = RequestContext.getCurrentContext();
        Map<String, List<String>> requestQueryParams = currentContext.getRequestQueryParams();
        requestQueryParams.remove("show-only-savings");
        requestQueryParams.put("filter-savings", Arrays.asList(filterSavings ? "true" : "false"));
        try {
            currentContext.setRouteHost(new URL(newUrl));
        } catch (MalformedURLException e) {
            log.warn("malformed url", e);
        }
        return null;
    }

    private String buildNewUrl(StringBuffer requestURL, String username) {
        //YOu may use eureka for getting the service url. hardcoded for demonstration
        String newUrl = "http://localhost:8100/user/" + username;
        log.info("new url : {}", newUrl);
        return newUrl;
    }

    private String extractBaseUrl(StringBuffer requestURL) {
        return requestURL.substring(0, requestURL.indexOf("/account"));
    }

    private boolean isFilterSavings(StringBuffer requestURL) {
        String showOnlySavings = RequestContext.getCurrentContext().getRequest().getParameter("show-only-savings");
        return "true".equals(showOnlySavings);
    }

    private String extractName(StringBuffer requestURL) {
        int start = requestURL.lastIndexOf("user/") + 5;
        return requestURL.substring(start);
    }

    public boolean isUserAccountRequest() {
        StringBuffer requestURL = getRequestURL();
        return requestURL.indexOf("/account/user/") >= 0;
    }

    private StringBuffer getRequestURL() {
        StringBuffer requestURL = RequestContext.getCurrentContext().getRequest().getRequestURL();
        log.info("request url : {}", requestURL);
        return requestURL;
    }
}
