package com.lms.leave_management_system.repository;

import com.lms.leave_management_system.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveApplicationRepository
        extends JpaRepository<LeaveApplication, Long> {

    List<LeaveApplication> findByEmployeeId(Long employeeId);
    List<LeaveApplication> findByStatus(LeaveApplication.Status status);
    List<LeaveApplication> findByEmployeeIdAndStatus(
            Long employeeId, LeaveApplication.Status status);
}