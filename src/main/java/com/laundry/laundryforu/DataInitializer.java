package com.laundry.laundryforu;

import com.laundry.laundryforu.model.User;
import com.laundry.laundryforu.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User customer = userRepository.findByEmail("budi@example.com");
        if (customer == null) {
            customer = new User();
            customer.setUsername("budi_santoso");
            customer.setEmail("budi@example.com");
            customer.setPassword("password123");
            customer.setRole("customer");
        }
        customer.setPhoto("dumb-profile.jpg");
        userRepository.save(customer);
    }
}
