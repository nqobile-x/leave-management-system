package com.lms.leave_management_system;

import com.lms.leave_management_system.model.Employee;
import com.lms.leave_management_system.model.LeaveApplication;
import com.lms.leave_management_system.repository.EmployeeRepository;
import com.lms.leave_management_system.repository.LeaveApplicationRepository;
import com.lms.leave_management_system.service.LeaveApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveApplicationServiceTest {

    @Mock
    private LeaveApplicationRepository leaveRepo;

    @Mock
    private EmployeeRepository employeeRepo;

    @InjectMocks
    private LeaveApplicationService leaveService;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee(
                "Nqobile Sibiya",
                "nqobile@lms.com",
                Employee.Role.EMPLOYEE,
                "Software Development"
        );
        testEmployee.setId(1L);
    }

    // Test 1: Successful leave submission
    @Test
    void testSubmitLeave_Success() {
        when(employeeRepo.findById(1L))
                .thenReturn(Optional.of(testEmployee));
        when(leaveRepo.findByEmployeeIdAndStatus(
                1L, LeaveApplication.Status.PENDING))
                .thenReturn(new ArrayList<>());

        String result = leaveService.submitLeave(
                1L, "Annual",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                "Family vacation"
        );

        assertTrue(result.contains("Success"));
        verify(leaveRepo, times(1)).save(any());
    }

    // Test 2: End date before start date
    @Test
    void testSubmitLeave_EndDateBeforeStartDate() {
        String result = leaveService.submitLeave(
                1L, "Annual",
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(1),
                "Family vacation"
        );

        assertTrue(result.contains("Error"));
        assertTrue(result.contains("End date cannot be before start date"));
        verify(leaveRepo, never()).save(any());
    }

    // Test 3: Employee not found
    @Test
    void testSubmitLeave_EmployeeNotFound() {
        when(employeeRepo.findById(99L))
                .thenReturn(Optional.empty());

        String result = leaveService.submitLeave(
                99L, "Annual",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                "Family vacation"
        );

        assertTrue(result.contains("Error"));
        assertTrue(result.contains("Employee not found"));
    }

    // Test 4: Cancel pending application
    @Test
    void testCancelApplication_Success() {
        LeaveApplication app = new LeaveApplication();
        app.setStatus(LeaveApplication.Status.PENDING);
        when(leaveRepo.findById(1L))
                .thenReturn(Optional.of(app));

        String result = leaveService.cancelApplication(1L);

        assertTrue(result.contains("Success"));
        assertEquals(LeaveApplication.Status.CANCELLED, app.getStatus());
    }

    // Test 5: Cancel already approved application
    @Test
    void testCancelApplication_AlreadyApproved() {
        LeaveApplication app = new LeaveApplication();
        app.setStatus(LeaveApplication.Status.APPROVED);
        when(leaveRepo.findById(1L))
                .thenReturn(Optional.of(app));

        String result = leaveService.cancelApplication(1L);

        assertTrue(result.contains("Error"));
        assertTrue(result.contains("Only pending applications can be cancelled"));
    }
}