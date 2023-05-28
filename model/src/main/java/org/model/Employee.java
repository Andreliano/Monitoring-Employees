package org.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Employee extends CompanyMember {
    private LocalDateTime arrivalTime;

    private int numberOfTasksCompleted;

    private EmployeeStatus status;


    public Employee() {

    }

    public Employee(LocalDateTime arrivalTime, int numberOfTasksCompleted, EmployeeStatus status) {
        this.arrivalTime = arrivalTime;
        this.numberOfTasksCompleted = numberOfTasksCompleted;
        this.status = status;
    }


    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getNumberOfTasksCompleted() {
        return numberOfTasksCompleted;
    }

    public void setNumberOfTasksCompleted(int numberOfTasksCompleted) {
        this.numberOfTasksCompleted = numberOfTasksCompleted;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return numberOfTasksCompleted == employee.numberOfTasksCompleted && Objects.equals(arrivalTime, employee.arrivalTime) && status == employee.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), arrivalTime, numberOfTasksCompleted, status);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "firstName=" + super.getFirstName() +
                ", lastName=" + super.getLastName() +
                ", email=" + super.getEmail() +
                ", password=" + super.getPassword() +
                ", arrivalTime=" + arrivalTime +
                ", numberOfTasksCompleted=" + numberOfTasksCompleted +
                ", status=" + status +
                '}';
    }
}
