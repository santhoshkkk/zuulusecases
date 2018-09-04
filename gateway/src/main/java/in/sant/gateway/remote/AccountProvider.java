package in.sant.gateway.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "accountProvider", url = "http://localhost:8100")
public interface AccountProvider {
    @RequestMapping(method = RequestMethod.GET, value = "/user/{name}")
    Account getByName(@PathVariable(value = "name") String name);
}
