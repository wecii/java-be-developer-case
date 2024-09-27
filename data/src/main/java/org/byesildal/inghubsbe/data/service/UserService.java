package org.byesildal.inghubsbe.data.service;

import org.byesildal.inghubsbe.data.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    boolean isCustomerExists(String customerEmail);
    UUID createCustomer(String customerName, String customerEmail, String customerPassword);
    UUID createAdmin(String adminName, String adminEmail, String adminPassword);
    Optional<User> getByEmail(String email);
    Optional<User> getByUuid(UUID uuid);
    List<User> getAllUsers();
}
