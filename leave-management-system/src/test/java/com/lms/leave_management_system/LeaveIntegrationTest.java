package com.lms.leave_management_system;

import com.lms.leave_management_system.model.Employee;
import com.lms.leave_management_system.model.LeaveApplication;
import com.lms.leave_management_system.repository.EmployeeRepository;
import com.lms.leave_management_system.repository.LeaveApplicationRepository;
import com.lms.leave_management_system.service.ApprovalService;
import com.lms.leave_management_system.service.LeaveApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LeaveIntegrationTest {

    @Autowired
    private LeaveApplicationService leaveService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private LeaveApplicationRepository leaveRepo;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        leaveRepo.deleteAll();
        employeeRepo.deleteAll();

        testEmployee = employeeRepo.save(new Employee(
                "Test Employee",
                "test@lms.com",
                Employee.Role.EMPLOYEE,
                "IT"
        ));
    }

    // Integration Test 1: Submit leave and verify in database
    @Test
    void testSubmitLeave_SavedToDatabase() {
        String result = leaveService.submitLeave(
                testEmployee.getId(),
                "Annual",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                "Integration test leave"
        );

        assertTrue(result.contains("Success"));

        List<LeaveApplication> saved =
                leaveRepo.findByEmployeeId(testEmployee.getId());
        assertEquals(1, saved.size());
        assertEquals("Annual", saved.get(0).getLeaveType());
        assertEquals(LeaveApplication.Status.PENDING,
                saved.get(0).getStatus());
    }

    // Integration Test 2: Full approval workflow
    @Test
    void testApprovalWorkflow_UpdatesDatabase() {
        leaveService.submitLeave(
                testEmployee.getId(), "Annual",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                "Workflow test"
        );

        Long appId = leaveRepo
                .findByEmployeeId(testEmployee.getId())
                .get(0).getId();

        String result = approvalService.approveLeave(
                appId, "Approved.");

        assertTrue(result.contains("APPROVED"));

        var updated = leaveRepo.findById(appId);
        assertTrue(updated.isPresent());
        assertEquals(LeaveApplication.Status.APPROVED,
                updated.get().getStatus());
        assertEquals("Approved.",
                updated.get().getManagerComment());
    }

    // Integration Test 3: Rejection workflow
    @Test
    void testRejectionWorkflow_UpdatesDatabase() {
        leaveService.submitLeave(
                testEmployee.getId(), "Sick",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                "Rejection test"
        );

        Long appId = leaveRepo
                .findByEmployeeId(testEmployee.getId())
                .get(0).getId();

        String result = approvalService.rejectLeave(
                appId, "Insufficient balance.");

        assertTrue(result.contains("REJECTED"));

        var updated = leaveRepo.findById(appId);
        assertTrue(updated.isPresent());
        assertEquals(LeaveApplication.Status.REJECTED,
                updated.get().getStatus());
    }

    // Integration Test 4: Cancel workflow
    @Test
    void testCancelWorkflow_UpdatesDatabase() {
        leaveService.submitLeave(
                testEmployee.getId(), "Annual",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                "Cancel test"
        );

        Long appId = leaveRepo
                .findByEmployeeId(testEmployee.getId())
                .get(0).getId();

        String result = leaveService.cancelApplication(appId);

        assertTrue(result.contains("Success"));

        var updated = leaveRepo.findById(appId);
        assertTrue(updated.isPresent());
        assertEquals(LeaveApplication.Status.CANCELLED,
                updated.get().getStatus());
    }
}