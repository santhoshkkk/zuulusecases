package in.sant.user.web;

import in.sant.user.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public User getByName(@PathVariable(value = "name") String name){
        return new User(name, (name.hashCode()%100)+1);
    }
}
