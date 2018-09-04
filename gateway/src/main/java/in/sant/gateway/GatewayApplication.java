package in.sant.gateway;

import in.sant.gateway.filters.FacadeFilter;
import in.sant.gateway.filters.UrlRewriteFilter;
import in.sant.gateway.remote.AccountProvider;
import in.sant.gateway.remote.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableZuulProxy
@EnableFeignClients
public class GatewayApplication {

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private AccountProvider accountProvider;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }


    @Bean
    public UrlRewriteFilter urlRewriteFilter(){
        return new UrlRewriteFilter();
    }

    @Bean
    public FacadeFilter facadeFilter(){
        return new FacadeFilter(userProvider, accountProvider);
    }
}
