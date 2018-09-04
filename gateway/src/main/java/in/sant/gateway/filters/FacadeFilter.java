package in.sant.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import in.sant.gateway.model.UserAccount;
import in.sant.gateway.remote.Account;
import in.sant.gateway.remote.AccountProvider;
import in.sant.gateway.remote.User;
import in.sant.gateway.remote.UserProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * A facade API which combines multiple API responses
 */
@Slf4j
public class FacadeFilter extends ZuulFilter {

    private UserProvider userProvider;

    private AccountProvider accountProvider;

    private ObjectMapper objectMapper;

    public FacadeFilter(UserProvider userProvider, AccountProvider accountProvider) {
        objectMapper = new ObjectMapper();
        this.userProvider = userProvider;
        this.accountProvider = accountProvider;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 6;
    }

    @Override
    public boolean shouldFilter() {
        return isUserAccountRequest();
    }

    @Override
    public Object run() throws ZuulException {
        String userName = userName();
        User user = userProvider.getUser(userName);
        Account account = accountProvider.getByName(userName);

        try {
            responseWriter().write(toJsonByteArray(buildUserAccount(user, account)));
        } catch (IOException e) {
            log.warn("Error writing response", e);
        }
        RequestContext.getCurrentContext().setRouteHost(null);
        return null;
    }

    private char[] toJsonByteArray(UserAccount userAccount) throws JsonProcessingException {
        return objectMapper.writeValueAsString(userAccount).toCharArray();
    }

    private PrintWriter responseWriter() throws IOException {
        return RequestContext.getCurrentContext().getResponse().getWriter();
    }

    private UserAccount buildUserAccount(User user, Account account) {
        UserAccount userAccount = new UserAccount();
        userAccount.setName(user.getName());
        userAccount.setAge(user.getAge());
        userAccount.setAccountNumber(account.getNumber());
        userAccount.setAccountType(account.getType());
        return userAccount;
    }

    public boolean isUserAccountRequest() {
        return requestUrl().indexOf("/useraccount/") > -1;
    }

    private StringBuffer requestUrl() {
        return RequestContext.getCurrentContext().getRequest().getRequestURL();
    }

    public String userName() {
        StringBuffer requestURL = requestUrl();
        return requestURL.substring(requestURL.lastIndexOf("useraccount/") + 11);
    }
}
