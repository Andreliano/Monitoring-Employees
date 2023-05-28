package org.services;

import org.model.Boss;
import org.model.Employee;
import org.model.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface IServices {
    long loginEmployee(Employee employee, IObserver client) throws Exception;

    void logoutEmployee(Employee employee, IObserver client) throws Exception;

    void loginBoss(Boss boss, IObserver client) throws Exception;

    void logoutBoss(Boss boss, IObserver client) throws Exception;

    void addTaskInList(Task task) throws Exception;

    List<Task> findAllTasksOfEmployee(String emailEmployee) throws Exception;

    void updateTasksStatusToCompleted(List<Long> ids) throws Exception;

    void updateNumberOfCompletedTasks(long numberOfTasks, String email) throws Exception;
    long updateEmployeeStatusToPresentAndArrivalTime(long idEmployee, LocalDateTime arrivalTime) throws Exception;

    long updateEmployeeStatusToAbsent(long idEmployee) throws Exception;

    List<Employee> findAllEmployeesWithPresentStatus() throws Exception;

    void addEmployeeInTable() throws Exception;

    void updateNumberOfCompletedTasksFromTable() throws Exception;

}
