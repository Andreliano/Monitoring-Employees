package org.persistence.repository;

import org.model.Employee;
import org.model.Task;

import java.util.List;

public interface TaskRepository extends GenericRepository<Long, Task>{
    Iterable<Task> findAllTasksOfEmployee(String employeeEmail);

    List<Long> updateTasksToCompleted(List<Long> ids);

}
