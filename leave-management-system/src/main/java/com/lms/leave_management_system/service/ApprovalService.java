package com.lms.leave_management_system.service;

import com.lms.leave_management_system.model.LeaveApplication;
import com.lms.leave_management_system.model.LeaveApplication.Status;
import com.lms.leave_management_system.repository.LeaveApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ApprovalService {

    @Autowired
    private LeaveApplicationRepository leaveRepo;

    public String approveLeave(Long applicationId, String comment) {
        Optional<LeaveApplication> app = leaveRepo.findById(applicationId);

        if (app.isEmpty()) {
            return "Error: Application not found for ID: " + applicationId;
        }

        if (app.get().getStatus() != Status.PENDING) {
            return "Error: Application has already been " +
                    app.get().getStatus().toString().toLowerCase() + ".";
        }

        app.get().setStatus(Status.APPROVED);
        app.get().setManagerComment(
                comment != null ? comment : "Approved.");
        leaveRepo.save(app.get());

        return "Success: Leave application " + applicationId +
                " has been APPROVED for " +
                app.get().getEmployee().getName() + ".";
    }

    public String rejectLeave(Long applicationId, String comment) {
        if (comment == null || comment.isBlank()) {
            return "Error: A reason is required when rejecting leave.";
        }

        Optional<LeaveApplication> app = leaveRepo.findById(applicationId);

        if (app.isEmpty()) {
            return "Error: Application not found for ID: " + applicationId;
        }

        if (app.get().getStatus() != Status.PENDING) {
            return "Error: Application has already been " +
                    app.get().getStatus().toString().toLowerCase() + ".";
        }

        app.get().setStatus(Status.REJECTED);
        app.get().setManagerComment(comment);
        leaveRepo.save(app.get());

        return "Success: Leave application " + applicationId +
                " has been REJECTED. Reason: " + comment;
    }
}