package com.campuscare.campuscare.security;

import com.campuscare.campuscare.model.Role;
import com.campuscare.campuscare.model.User;
import com.campuscare.campuscare.repository.RoleRepository;
import com.campuscare.campuscare.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CustomUserDetailsService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Role role = roleRepository.findById(user.getRole().getRoleId())
                .orElseThrow(() -> new UsernameNotFoundException("Role not found for user: " + email));

        return new UserPrincipal(user, role);
    }
}