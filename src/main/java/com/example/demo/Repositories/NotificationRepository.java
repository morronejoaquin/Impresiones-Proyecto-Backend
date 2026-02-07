package com.example.demo.Repositories;

import com.example.demo.Model.Entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(String email);
}
