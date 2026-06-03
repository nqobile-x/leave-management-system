package com.lms.leave_management_system.service;

import com.lms.leave_management_system.model.NotificationLog;
import com.lms.leave_management_system.model.NotificationLog.NotificationType;
import com.lms.leave_management_system.repository.NotificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationLogRepository notifRepo;

    public void notifyApproval(String employeeName,
                               String employeeEmail,
                               String leaveType,
                               String comment) {
        String subject = "Leave Application Approved - " + leaveType;
        String message = "Dear " + employeeName + ", your " + leaveType +
                " leave application has been APPROVED. " +
                "Manager comment: " + comment;

        NotificationLog log = new NotificationLog(
                employeeEmail, employeeName, subject, message,
                NotificationType.LEAVE_APPROVED);
        notifRepo.save(log);

        System.out.println("=== NOTIFICATION SENT ===");
        System.out.println("To     : " + employeeEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("=========================");
    }

    public void notifyRejection(String employeeName,
                                String employeeEmail,
                                String leaveType,
                                String reason) {
        String subject = "Leave Application Rejected - " + leaveType;
        String message = "Dear " + employeeName + ", your " + leaveType +
                " leave application has been REJECTED. " +
                "Reason: " + reason;

        NotificationLog log = new NotificationLog(
                employeeEmail, employeeName, subject, message,
                NotificationType.LEAVE_REJECTED);
        notifRepo.save(log);

        System.out.println("=== NOTIFICATION SENT ===");
        System.out.println("To     : " + employeeEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("=========================");
    }

    public void notifySubmission(String managerEmail,
                                 String employeeName,
                                 String leaveType) {
        String subject = "New Leave Request - " + employeeName;
        String message = employeeName + " has submitted a " +
                leaveType + " leave request pending your approval.";

        NotificationLog log = new NotificationLog(
                managerEmail, "Manager", subject, message,
                NotificationType.LEAVE_SUBMITTED);
        notifRepo.save(log);

        System.out.println("=== NOTIFICATION SENT ===");
        System.out.println("To     : " + managerEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("=========================");
    }

    public List<NotificationLog> getAllNotifications() {
        return notifRepo.findAll();
    }
}