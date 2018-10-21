# zuul use cases
Example project for demonstrating different zuul use cases 



##Simple redirect
Consider the user service running on port 8000 which fetches user by name.
`GET http://localhost:8000/santhosh`
Response:```{“name":"sant","age":28}```


Say Zuul gateway want to expose this service to external world as `users` service
`GET http://localhost:8080/users/santhosh`
having the same response

- Step 1: Use the following Zuul configuration to define the route

```
zuul:
  routes:
    users:
      url: http://localhost:8000
```

- Step 2: To enable Zuul proxy on your project, you need to use the following configuration configuration `@EnableZuulProxy`

Note that you don’t need to add a custom filter for this. The built in `Routing Filters` does the job for you.

##Redirect to a custom url
There are cases when the URL has to be rewritten. 
Consider the following example, 
Bank service running on port 8100 has the following Rest endpoint to fetch the user accounts details by username.

`GET http://localhost:8100/user/santhosh?filter-savings=true`
Reponse:```{“number":2007780400,"type":"SAVINGS"}```

Say the api-gateway is receiving the following request which has to be redirected to the above url

`GET http://localhost:8080/account/byname/santhosh?only-savings=true`

Here there are two changes
- The url has  changed from account/byname/<name> to user/<name>
- The request parameter has changed from only-savings to filter-savings

This can be achieved using a pre filter to rewrite the url and query params in the Zuul request context.

Here is the relevant code snippet

```RequestContext currentContext = RequestContext.getCurrentContext();
Map<String, List<String>> requestQueryParams = currentContext.getRequestQueryParams();
requestQueryParams.remove("show-only-savings");
requestQueryParams.put("filter-savings", Arrays.asList(filterSavings ? "true" : "false"));
try {
    currentContext.setRouteHost(new URL(newUrl));
} catch (MalformedURLException e) {
    log.warn("malformed url", e);
}
```

Have look at the `UrlRewriteFilter.java` 


##Implementing a facade API 
Implementing a facade by invoking multiple urls and customising the response

There are cases when a gateway may need to call multiple urls and then merge the response.

Consider the following example,

Bank service running on port 8100 has the following Rest endpoint to fetch the user accounts details by username.
`GET http://localhost:8100/user/santhosh?filter-savings=true`
Response:`{“number":2007780400,"type":"SAVINGS"}`

And
User service running on port 8000 which fetches user by name.
`GET http://localhost:8000/santhosh`
Response:`{"name":"santhosh","age":1}`


Consider the gateway want to support the following facade API to fetch the useraccount details
`GET http://localhost:8080/useraccount/santhosh`
Response:`{"name":"santhosh","age":1,"accountNumber":2007780400,"accountType":"CURRENT"}`

This can be achieved by invoking both the APIs from a pre-filter, then merging the response to the required format and finally stopping further redirection. Stopping further redirection can be done the following code
```RequestContext.getCurrentContext().setRouteHost(null);```


In the example code feign clients are used to fetch data from the APIs. Note that a route configuration is to be added for the facade url. Refer application.yml