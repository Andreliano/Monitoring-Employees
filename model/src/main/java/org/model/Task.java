package org.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task extends Entity<Long>{

    private String emailEmployee;

    private String description;

    private LocalDateTime momentOfSending;

    private TaskStatus status;

    private boolean blocked;

    public Task(String emailEmployee, String description, LocalDateTime momentOfSending, TaskStatus status, boolean blocked) {
        this.emailEmployee = emailEmployee;
        this.description = description;
        this.momentOfSending = momentOfSending;
        this.status = status;
        this.blocked = blocked;
    }

    public String getEmailEmployee() {
        return emailEmployee;
    }

    public void setEmailEmployee(String emailEmployee) {
        this.emailEmployee = emailEmployee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getMomentOfSending() {
        return momentOfSending;
    }

    public void setMomentOfSending(LocalDateTime momentOfSending) {
        this.momentOfSending = momentOfSending;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(emailEmployee, task.emailEmployee) && Objects.equals(description, task.description) && Objects.equals(momentOfSending, task.momentOfSending) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailEmployee, description, momentOfSending, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "emailEmployee='" + emailEmployee + '\'' +
                ", description='" + description + '\'' +
                ", momentOfSending=" + momentOfSending +
                ", status=" + status +
                '}';
    }
}
