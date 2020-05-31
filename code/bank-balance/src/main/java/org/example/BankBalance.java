package org.example;

import java.time.LocalDateTime;

public class BankBalance {
    private String name;
    private Integer balance;
    private LocalDateTime time;

    public BankBalance() {
        this.name = null;
        this.balance = null;
        this.time = null;
    }

    public BankBalance(String name, Integer balance, LocalDateTime time) {
        this.name = name;
        this.balance = balance;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
