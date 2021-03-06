package org.example;

import java.time.LocalDateTime;

public class BankTransaction {
    private String name;
    private Integer amount;
    private LocalDateTime time;

    public BankTransaction() {
        this.name = null;
        this.amount = null;
        this.time = null;
    }

    public BankTransaction(String name, Integer amount, LocalDateTime time) {
        this.name = name;
        this.amount = amount;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
