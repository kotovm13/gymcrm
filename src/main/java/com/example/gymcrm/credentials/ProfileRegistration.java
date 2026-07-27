package com.example.gymcrm.credentials;

import com.example.gymcrm.domain.User;

public record ProfileRegistration<T extends User>(T profile, String password) {
}
