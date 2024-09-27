package org.byesildal.inghubsbe.auth.service;

import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.auth.model.UserPrincipal;
import org.byesildal.inghubsbe.data.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final org.byesildal.inghubsbe.data.service.UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.getByEmail(username);
        if (user.isPresent())
            return new UserPrincipal(user.get());

        throw new UsernameNotFoundException("User not found");
    }
}
