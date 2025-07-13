package com.api.authapi.application.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserHelperService userHelperService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userHelperService.getUserByEmail(username);
    }
}
