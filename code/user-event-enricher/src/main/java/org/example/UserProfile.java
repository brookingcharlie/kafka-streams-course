package org.example;

import java.util.Objects;

public class UserProfile {
    private String name;
    private String email;

    public UserProfile() {
    }

    public UserProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile userProfile = (UserProfile) o;
        return Objects.equals(name, userProfile.name) &&
                Objects.equals(email, userProfile.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
