package org.byesildal.inghubsbe.data.service;

import lombok.RequiredArgsConstructor;
import org.byesildal.inghubsbe.data.entity.User;
import org.byesildal.inghubsbe.data.enums.UserRole;
import org.byesildal.inghubsbe.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public boolean isCustomerExists(String customerEmail) {
        return userRepository.findByEmail(customerEmail).isPresent();
    }

    @Override
    public UUID createCustomer(String customerName, String customerEmail, String customerPassword) {
        var user = new User();
        user.setEmail(customerEmail);
        user.setName(customerName);
        user.setPassword(customerPassword);
        user.setRole(UserRole.CUSTOMER);
        return userRepository.save(user).getId();
    }

    @Override
    public UUID createAdmin(String adminName, String adminEmail, String adminPassword) {
        var user = new User();
        user.setEmail(adminEmail);
        user.setName(adminName);
        user.setPassword(adminPassword);
        user.setRole(UserRole.ADMIN);
        return userRepository.save(user).getId();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getByUuid(UUID uuid) {
        return userRepository.findById(uuid);
    }

    @Override
    public List<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).toList();
    }

}
