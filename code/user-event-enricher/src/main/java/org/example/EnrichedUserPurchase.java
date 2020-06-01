package org.example;

import java.util.Objects;

public class EnrichedUserPurchase {
    private UserPurchase purchase;
    private UserProfile profile;

    public EnrichedUserPurchase() {
    }

    public EnrichedUserPurchase(UserPurchase purchase, UserProfile profile) {
        this.purchase = purchase;
        this.profile = profile;
    }

    public UserPurchase getPurchase() {
        return purchase;
    }

    public void setPurchase(UserPurchase purchase) {
        this.purchase = purchase;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "EnrichedUserPurchase{" +
                "purchase=" + purchase +
                ", profile=" + profile +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrichedUserPurchase that = (EnrichedUserPurchase) o;
        return Objects.equals(purchase, that.purchase) &&
                Objects.equals(profile, that.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchase, profile);
    }
}
