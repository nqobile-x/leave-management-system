package com.lms.leave_management_system;

import com.lms.leave_management_system.model.Employee;
import com.lms.leave_management_system.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Override
    public void run(String... args) throws Exception {
        // Create test employees on startup
        Employee emp1 = new Employee(
                "Nqobile Sibiya",
                "nqobile@lms.com",
                Employee.Role.EMPLOYEE,
                "Software Development"
        );
        Employee emp2 = new Employee(
                "Thabo Dlamini",
                "thabo@lms.com",
                Employee.Role.MANAGER,
                "Software Development"
        );
        Employee emp3 = new Employee(
                "Lerato Mokoena",
                "lerato@lms.com",
                Employee.Role.EMPLOYEE,
                "HR"
        );

        employeeRepo.save(emp1);
        employeeRepo.save(emp2);
        employeeRepo.save(emp3);

        System.out.println("=== Test data loaded ===");
        System.out.println("Employee 1: Nqobile Sibiya (ID: 1)");
        System.out.println("Employee 2: Thabo Dlamini - Manager (ID: 2)");
        System.out.println("Employee 3: Lerato Mokoena (ID: 3)");
        System.out.println("========================");
    }
}