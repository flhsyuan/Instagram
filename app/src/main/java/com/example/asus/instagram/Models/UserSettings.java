package com.example.asus.instagram.Models;

/**
 * Yuan: store 2 objects: user & userAccountSettings
 */
public class UserSettings {

    private User user;
    private UserAccountsettings userAccountsettings;

    public UserSettings(User user, UserAccountsettings userAccountsettings) {
        this.user = user;
        this.userAccountsettings = userAccountsettings;
    }

    public UserSettings() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountsettings getUserAccountsettings() {
        return userAccountsettings;
    }

    public void setUserAccountsettings(UserAccountsettings userAccountsettings) {
        this.userAccountsettings = userAccountsettings;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "user=" + user +
                ", userAccountsettings=" + userAccountsettings +
                '}';
    }
}
