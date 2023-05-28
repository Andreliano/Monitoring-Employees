package org.persistence.repository;

import org.model.Employee;

import java.time.LocalDateTime;

public interface EmployeeRepository extends GenericRepository<Long, Employee> {
    long checkEmployeeAccountExistence(String email, String password);

    long updateEmployeeStatusToPresentAndArrivalTime(long idEmployee, LocalDateTime arrivalTime);

    long updateEmployeeStatusToAbsent(long idEmployee);

    long updateNumberOfTasksCompleted(long numberOfTasks, String email);

    Iterable<Employee> findAllEmployeesWithPresentStatus();

}
