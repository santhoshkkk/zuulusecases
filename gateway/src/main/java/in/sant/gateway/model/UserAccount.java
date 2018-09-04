package in.sant.gateway.model;

import lombok.Data;

@Data
public class UserAccount {
    private String name;
    private int age;
    private long accountNumber;
    private String accountType;
}
