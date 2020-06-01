package org.example;

import java.util.Objects;

public class UserPurchase {
    private String user;
    private String product;

    public UserPurchase() {
    }

    public UserPurchase(String user, String product) {
        this.user = user;
        this.product = product;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "UserPurchase{" +
                "user='" + user + '\'' +
                ", product='" + product + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPurchase userPurchase = (UserPurchase) o;
        return Objects.equals(user, userPurchase.user) &&
                Objects.equals(product, userPurchase.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, product);
    }
}
