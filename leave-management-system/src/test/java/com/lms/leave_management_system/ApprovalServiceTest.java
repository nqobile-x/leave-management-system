package com.lms.leave_management_system;

import com.lms.leave_management_system.model.Employee;
import com.lms.leave_management_system.model.LeaveApplication;
import com.lms.leave_management_system.repository.LeaveApplicationRepository;
import com.lms.leave_management_system.service.ApprovalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApprovalServiceTest {

    @Mock
    private LeaveApplicationRepository leaveRepo;

    @InjectMocks
    private ApprovalService approvalService;

    private LeaveApplication testApp;

    @BeforeEach
    void setUp() {
        Employee emp = new Employee(
                "Nqobile Sibiya", "nqobile@lms.com",
                Employee.Role.EMPLOYEE, "IT"
        );
        testApp = new LeaveApplication();
        testApp.setEmployee(emp);
        testApp.setStatus(LeaveApplication.Status.PENDING);
    }

    // Test 1: Successful approval
    @Test
    void testApproveLeave_Success() {
        when(leaveRepo.findById(1L))
                .thenReturn(Optional.of(testApp));

        String result = approvalService.approveLeave(
                1L, "Approved. Enjoy your leave.");

        assertTrue(result.contains("APPROVED"));
        assertEquals(LeaveApplication.Status.APPROVED,
                testApp.getStatus());
        verify(leaveRepo, times(1)).save(testApp);
    }

    // Test 2: Application not found
    @Test
    void testApproveLeave_NotFound() {
        when(leaveRepo.findById(99L))
                .thenReturn(Optional.empty());

        String result = approvalService.approveLeave(99L, "Approved");

        assertTrue(result.contains("Error"));
        assertTrue(result.contains("not found"));
    }

    // Test 3: Already approved
    @Test
    void testApproveLeave_AlreadyApproved() {
        testApp.setStatus(LeaveApplication.Status.APPROVED);
        when(leaveRepo.findById(1L))
                .thenReturn(Optional.of(testApp));

        String result = approvalService.approveLeave(1L, "Approved");

        assertTrue(result.contains("Error"));
        assertTrue(result.contains("already been"));
    }

    // Test 4: Reject without comment
    @Test
    void testRejectLeave_NoComment() {
        String result = approvalService.rejectLeave(1L, "");

        assertTrue(result.contains("Error"));
        assertTrue(result.contains("reason is required"));
        verify(leaveRepo, never()).save(any());
    }

    // Test 5: Successful rejection
    @Test
    void testRejectLeave_Success() {
        when(leaveRepo.findById(1L))
                .thenReturn(Optional.of(testApp));

        String result = approvalService.rejectLeave(
                1L, "Insufficient leave balance.");

        assertTrue(result.contains("REJECTED"));
        assertEquals(LeaveApplication.Status.REJECTED,
                testApp.getStatus());
    }
}