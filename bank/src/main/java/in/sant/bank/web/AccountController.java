package in.sant.bank.web;

import in.sant.bank.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static in.sant.bank.model.AccountType.CURRENT;
import static in.sant.bank.model.AccountType.SAVINGS;

@RestController
@Slf4j
public class AccountController {

    @RequestMapping(method = RequestMethod.GET, value = "/user/{name}")
    public Account getByName(@PathVariable(value = "name") String name, @RequestParam(value = "filter-savings", required = false, defaultValue = "false") boolean filterSavings) {
        log.info("filter-savings: {}", filterSavings);
        return new Account(name.hashCode(), filterSavings?SAVINGS:CURRENT);
    }
}
