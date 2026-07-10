package com.laundry.laundryforu.repository;

import com.laundry.laundryforu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Tambahkan baris ini agar metode findByEmail dikenali:
    User findByEmail(String email);
}