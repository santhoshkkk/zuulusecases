package in.sant.gateway.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="userProvider", url = "http://localhost:8000")
public interface UserProvider {
    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    User getUser(@PathVariable(value = "name")String name);
}
