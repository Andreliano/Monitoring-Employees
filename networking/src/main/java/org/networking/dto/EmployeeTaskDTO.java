package org.networking.dto;

import java.io.Serializable;

public class EmployeeTaskDTO implements Serializable {

    private String email;

    private long numberOfTasks;

    public EmployeeTaskDTO(String email, long numberOfTasks) {
        this.email = email;
        this.numberOfTasks = numberOfTasks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getNumberOfTasks() {
        return numberOfTasks;
    }

    public void setNumberOfTasks(long numberOfTasks) {
        this.numberOfTasks = numberOfTasks;
    }
}
