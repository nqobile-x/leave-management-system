package com.lms.leave_management_system.controller;

import com.lms.leave_management_system.model.LeaveApplication;
import com.lms.leave_management_system.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveApplicationService leaveService;

    // Submit a leave application
    @PostMapping("/submit")
    public ResponseEntity<String> submitLeave(
            @RequestParam Long employeeId,
            @RequestParam String leaveType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam String reason) {

        String result = leaveService.submitLeave(
                employeeId, leaveType, startDate, endDate, reason);

        if (result.startsWith("Error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // Get all applications for an employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveApplication>> getByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(
                leaveService.getEmployeeApplications(employeeId));
    }

    // Get all pending applications
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveApplication>> getPending() {
        return ResponseEntity.ok(
                leaveService.getPendingApplications());
    }

    // Get all applications
    @GetMapping("/all")
    public ResponseEntity<List<LeaveApplication>> getAll() {
        return ResponseEntity.ok(
                leaveService.getAllApplications());
    }

    // Cancel an application
    @PutMapping("/cancel/{applicationId}")
    public ResponseEntity<String> cancel(
            @PathVariable Long applicationId) {
        String result = leaveService.cancelApplication(applicationId);
        if (result.startsWith("Error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}