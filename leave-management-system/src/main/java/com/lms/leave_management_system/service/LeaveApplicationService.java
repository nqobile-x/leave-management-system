package com.lms.leave_management_system.service;

import com.lms.leave_management_system.model.Employee;
import com.lms.leave_management_system.model.LeaveApplication;
import com.lms.leave_management_system.model.LeaveApplication.Status;
import com.lms.leave_management_system.repository.EmployeeRepository;
import com.lms.leave_management_system.repository.LeaveApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveApplicationService {

    @Autowired
    private LeaveApplicationRepository leaveRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    // Submit a new leave application
    public String submitLeave(Long employeeId, String leaveType,
                              LocalDate startDate, LocalDate endDate,
                              String reason) {

        // Validate end date is not before start date
        if (endDate.isBefore(startDate)) {
            return "Error: End date cannot be before start date.";
        }

        // Check employee exists
        Optional<Employee> employee = employeeRepo.findById(employeeId);
        if (employee.isEmpty()) {
            return "Error: Employee not found.";
        }

        // Check for overlapping applications
        List<LeaveApplication> existing =
                leaveRepo.findByEmployeeIdAndStatus(employeeId, Status.PENDING);
        for (LeaveApplication app : existing) {
            if (!startDate.isAfter(app.getEndDate()) &&
                    !endDate.isBefore(app.getStartDate())) {
                return "Error: You already have a pending " +
                        "application for overlapping dates.";
            }
        }

        // Create and save application
        LeaveApplication application = new LeaveApplication();
        application.setEmployee(employee.get());
        application.setLeaveType(leaveType);
        application.setStartDate(startDate);
        application.setEndDate(endDate);
        application.setReason(reason);
        application.setStatus(Status.PENDING);
        application.setAppliedDate(LocalDate.now());

        leaveRepo.save(application);
        return "Success: Leave application submitted. " +
                "Status is PENDING manager approval.";
    }

    // Get all applications for an employee
    public List<LeaveApplication> getEmployeeApplications(Long employeeId) {
        return leaveRepo.findByEmployeeId(employeeId);
    }

    // Get all pending applications (for manager)
    public List<LeaveApplication> getPendingApplications() {
        return leaveRepo.findByStatus(Status.PENDING);
    }

    // Get all applications
    public List<LeaveApplication> getAllApplications() {
        return leaveRepo.findAll();
    }

    // Cancel a pending application
    public String cancelApplication(Long applicationId) {
        Optional<LeaveApplication> app = leaveRepo.findById(applicationId);
        if (app.isEmpty()) {
            return "Error: Application not found.";
        }
        if (app.get().getStatus() != Status.PENDING) {
            return "Error: Only pending applications can be cancelled.";
        }
        app.get().setStatus(Status.CANCELLED);
        leaveRepo.save(app.get());
        return "Success: Application cancelled.";
    }
}