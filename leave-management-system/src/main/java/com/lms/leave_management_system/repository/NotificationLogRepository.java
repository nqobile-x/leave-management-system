package com.lms.leave_management_system.repository;

import com.lms.leave_management_system.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationLogRepository
        extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByRecipientEmail(String email);
    List<NotificationLog> findByType(NotificationLog.NotificationType type);
}