package com.laundry.laundryforu.repository;

import com.laundry.laundryforu.model.Order; // WAJIB ADA
import com.laundry.laundryforu.model.User;  // WAJIB ADA
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByUserId(Long userId);
}