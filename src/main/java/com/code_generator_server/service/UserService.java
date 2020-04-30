package com.code_generator_server.service;

import com.code_generator_server.entity.User;
import com.code_generator_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        var user = repository.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public boolean updateLayers(Principal principal, String layers) {
        String username = principal.getName();
        repository.updateLayers(username, layers);
        return true;
    }

    public boolean updateHyperParameters(Principal principal, String hyperParameters) {
        String username = principal.getName();
        repository.updateHyperParameters(username, hyperParameters);
        return true;
    }

    public boolean saveUser(User user) {
        User user1 = repository.findByUsername(user.getUsername());
        if (user1 != null)
            return false;
        user.setPassword(encoder.encode(user.getPassword()));
        repository.save(user);
        return true;
    }
}