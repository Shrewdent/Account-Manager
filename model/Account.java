package model;

import java.io.Serializable;

public class Account implements Serializable {
    private String accountName;
    private String password;
    private String platform;

    public Account(String accountName, String password, String platform) {
        this.accountName = accountName;
        this.password = password;
        this.platform = platform;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}