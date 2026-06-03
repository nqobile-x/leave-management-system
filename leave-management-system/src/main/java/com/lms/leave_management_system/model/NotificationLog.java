package com.lms.leave_management_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientEmail;
    private String recipientName;
    private String subject;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private LocalDateTime sentAt = LocalDateTime.now();

    public enum NotificationType {
        LEAVE_APPROVED, LEAVE_REJECTED, LEAVE_SUBMITTED, LEAVE_CANCELLED
    }

    public NotificationLog() {}

    public NotificationLog(String recipientEmail, String recipientName,
                           String subject, String message,
                           NotificationType type) {
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.subject = subject;
        this.message = message;
        this.type = type;
        this.sentAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getRecipientEmail() { return recipientEmail; }
    public String getRecipientName() { return recipientName; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
    public LocalDateTime getSentAt() { return sentAt; }
}