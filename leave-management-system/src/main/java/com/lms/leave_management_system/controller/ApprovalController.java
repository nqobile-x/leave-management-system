package com.lms.leave_management_system.controller;

import com.lms.leave_management_system.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    // Approve a leave application
    @PutMapping("/approve/{applicationId}")
    public ResponseEntity<String> approve(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String comment) {

        String result = approvalService.approveLeave(
                applicationId, comment);

        if (result.startsWith("Error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // Reject a leave application
    @PutMapping("/reject/{applicationId}")
    public ResponseEntity<String> reject(
            @PathVariable Long applicationId,
            @RequestParam String comment) {

        String result = approvalService.rejectLeave(
                applicationId, comment);

        if (result.startsWith("Error")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}