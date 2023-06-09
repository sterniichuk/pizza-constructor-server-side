package com.pizzacalculator.pizzacalculatorserverside.security.service;

import com.pizzacalculator.pizzacalculatorserverside.security.exception.NotFoundException;
import com.pizzacalculator.pizzacalculatorserverside.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByPhone(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));
    }
}
