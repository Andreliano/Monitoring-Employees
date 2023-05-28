package org.persistence.database;

import org.model.Boss;
import org.model.Task;
import org.model.TaskStatus;
import org.persistence.repository.TaskRepository;
import org.persistence.utils.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDataBaseRepository implements TaskRepository {

    private final JdbcUtils jdbcUtils = new JdbcUtils();

    @Override
    public Task save(Task task) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tasks(emailEmployee, description, momentOfSending, status) VALUES(?, ?, ?, ?)")) {
            preparedStatement.setString(1, task.getEmailEmployee());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(task.getMomentOfSending()));
            preparedStatement.setString(4, task.getStatus().toString());
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                return task;
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Task delete(Long idTask) {
        return null;
    }

    @Override
    public Task update(Task task) {
        return null;
    }

    @Override
    public Task findOne(Long idTask) {
        return null;
    }

    @Override
    public Iterable<Task> findAll() {
        Connection connection = jdbcUtils.getConnection();
        List<Task> tasks = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM tasks")){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    Task task = getTaskFromDataBase(resultSet);
                    tasks.add(task);
                }
            }
        }catch (SQLException ex){
            System.err.println("Error DB " + ex);
        }
        return tasks;
    }

    private Task getTaskFromDataBase(ResultSet resultSet) throws SQLException {
        long idTask = resultSet.getLong(1);
        String emailEmployee = resultSet.getString(2);
        String description = resultSet.getString(3);
        LocalDateTime momentOfSending = resultSet.getTimestamp(4).toLocalDateTime();
        TaskStatus status = TaskStatus.valueOf(resultSet.getString(5));
        boolean blocked = resultSet.getBoolean(6);
        Task task = new Task(emailEmployee, description, momentOfSending, status, blocked);
        task.setId(idTask);
        return task;
    }

    @Override
    public Iterable<Task> findAllTasksOfEmployee(String employeeEmail) {
        Connection connection = jdbcUtils.getConnection();
        List<Task> tasks = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM tasks WHERE emailEmployee = ?")){
            preparedStatement.setString(1, employeeEmail);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    Task task = getTaskFromDataBase(resultSet);
                    tasks.add(task);
                }
            }
        }catch (SQLException ex){
            System.err.println("Error DB " + ex);
        }
        return tasks;
    }

    @Override
    public List<Long> updateTasksToCompleted(List<Long> ids) {
        Connection connection = jdbcUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE tasks SET status = 'COMPLETED', blocked = true WHERE idTask = ANY(?::bigint[])")){
            preparedStatement.setArray(1,  connection.createArrayOf("bigint", ids.toArray()));
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                return ids;
            }
        }catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return null;
    }
}
