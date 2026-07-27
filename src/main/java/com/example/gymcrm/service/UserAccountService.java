package com.example.gymcrm.service;

public interface UserAccountService {
    boolean authenticate(String username, String password);
    void changePassword(String username, String oldPassword, String newPassword);
    void setActive(String username, String password, boolean active);
}
