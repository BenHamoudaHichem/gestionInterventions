package com.app.gestionInterventions.security.services;

import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.repositories.work.user.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepositoryImpl userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + identifier));

        return UserDetailsImpl.build(user);
    }
}
