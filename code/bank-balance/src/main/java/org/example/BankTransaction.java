package org.example;

import java.time.LocalDateTime;

public class BankTransaction {
    private final String name;
    private final int amount;
    private final LocalDateTime time;

    public BankTransaction(String name, int amount, LocalDateTime time) {
        this.name = name;
        this.amount = amount;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
