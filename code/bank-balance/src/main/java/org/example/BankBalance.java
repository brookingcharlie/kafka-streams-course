package org.example;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "BankBalance{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankBalance that = (BankBalance) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(balance, that.balance) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, balance, time);
    }
}
