package com.verytouch.vkit.samples.oauth2.server.oauth2;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserService implements UserDetailsService {

    private final List<User> users;

    public UserService() {
        // 123456
        String password = "$2a$10$piCRbkfQUTAHjsbgXcoFrOSkuHTb7K8lGrTclGZvgjr.LK1ZWmUJa";
        this.users = new ArrayList<>();
        users.add(User.build("admin", password, "18511112222", "ROLE_ADMIN"));
        users.add(User.build("guest", password, "18533334444", "ROLE_GUEST"));
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return users.stream().filter(u -> Objects.equals(s, u.getUsername())).findAny().orElse(null);
    }

    public UserDetails loadUserByPhone(String s) throws UsernameNotFoundException {
        return users.stream().filter(u -> Objects.equals(s, u.getPhone())).findAny().orElse(null);
    }
}
