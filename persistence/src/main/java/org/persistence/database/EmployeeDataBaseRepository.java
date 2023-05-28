package org.persistence.database;

import org.model.Employee;
import org.model.EmployeeStatus;
import org.persistence.repository.EmployeeRepository;
import org.persistence.utils.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDataBaseRepository implements EmployeeRepository {

    private final JdbcUtils jdbcUtils = new JdbcUtils();

    @Override
    public Employee save(Employee employee) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO employees(firstname, lastname, email, password, arrivalTime, numberOfTasksCompleted, status) VALUES(?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            preparedStatement.setString(3, employee.getEmail());
            preparedStatement.setString(4, employee.getPassword());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(employee.getArrivalTime()));
            preparedStatement.setInt(6, employee.getNumberOfTasksCompleted());
            preparedStatement.setString(7, employee.getStatus().toString());
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                return employee;
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Employee delete(Long idEmployee) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM employees WHERE idEmployee = ?")) {
            preparedStatement.setLong(1, idEmployee);
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                Employee employee = new Employee(LocalDateTime.now(), -1, EmployeeStatus.ABSENT);
                employee.setId(idEmployee);
                employee.setEmail("");
                employee.setPassword("");
                employee.setFirstName("");
                employee.setLastName("");
                return employee;
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Employee update(Employee employee) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE employees SET firstname = ?, lastname = ?, email = ?, password = ?, arrivalTime = ?, numberOfTasksCompleted = ?, status = ? WHERE idboss = ?")) {
            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            preparedStatement.setString(3, employee.getEmail());
            preparedStatement.setString(4, employee.getPassword());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(employee.getArrivalTime()));
            preparedStatement.setInt(6, employee.getNumberOfTasksCompleted());
            preparedStatement.setString(7, employee.getStatus().toString());
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                return employee;
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Employee findOne(Long idEmployee) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM employees WHERE idEmployee = ?")) {
            preparedStatement.setLong(1, idEmployee);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return getEmployeeFromDataBase(resultSet);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return null;
    }

    @Override
    public Iterable<Employee> findAll() {
        Connection connection = jdbcUtils.getConnection();
        List<Employee> employees = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM employees")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Employee employee = getEmployeeFromDataBase(resultSet);
                    employees.add(employee);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return employees;
    }

    @Override
    public Iterable<Employee> findAllEmployeesWithPresentStatus(){
        Connection connection = jdbcUtils.getConnection();
        List<Employee> employees = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM employees WHERE status = ?")) {
            preparedStatement.setString(1, "PRESENT");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Employee employee = getEmployeeFromDataBase(resultSet);
                    employees.add(employee);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return employees;
    }

    @Override
    public long checkEmployeeAccountExistence(String email, String password) {
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT idEmployee FROM employees WHERE email = ? AND password = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return 0;
    }

    @Override
    public long updateEmployeeStatusToPresentAndArrivalTime(long idEmployee, LocalDateTime arrivalTime) {
        Connection connection = jdbcUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE employees SET status = ?, arrivalTime = ? WHERE idEmployee = ? AND status = 'ABSENT'")){
            preparedStatement.setString(1, "PRESENT");
            preparedStatement.setTimestamp(2, Timestamp.valueOf(arrivalTime));
            preparedStatement.setLong(3, idEmployee);
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                return idEmployee;
            }
        }catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return 0;
    }

    @Override
    public long updateEmployeeStatusToAbsent(long idEmployee) {
        Connection connection = jdbcUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE employees SET status = ? WHERE idEmployee = ? AND status = 'PRESENT'")){
            preparedStatement.setString(1, "ABSENT");
            preparedStatement.setLong(2, idEmployee);
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                return idEmployee;
            }
        }catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return 0;
    }

    @Override
    public long updateNumberOfTasksCompleted(long numberOfTasks, String email) {
        Connection connection = jdbcUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE employees SET numberOfTasksCompleted = numberOfTasksCompleted + ? WHERE email = ?")){
            preparedStatement.setLong(1, numberOfTasks);
            preparedStatement.setString(2, email);
            int rowsNumber = preparedStatement.executeUpdate();
            if (rowsNumber > 0) {
                return numberOfTasks;
            }
        }catch (SQLException ex) {
            System.err.println("Error DB " + ex);
        }
        return -1;
    }


    private Employee getEmployeeFromDataBase(ResultSet resultSet) throws SQLException {
        long idEmployee = resultSet.getLong(1);
        String firstname = resultSet.getString(2);
        String lastname = resultSet.getString(3);
        String email = resultSet.getString(4);
        String password = resultSet.getString(5);
        LocalDateTime arrivalTime = resultSet.getTimestamp(6).toLocalDateTime();
        int numberOfTasksCompleted = resultSet.getInt(7);
        EmployeeStatus status = EmployeeStatus.valueOf(resultSet.getString(8));

        Employee employee = new Employee(arrivalTime, numberOfTasksCompleted, status);
        employee.setId(idEmployee);
        employee.setFirstName(firstname);
        employee.setLastName(lastname);
        employee.setEmail(email);
        employee.setPassword(password);
        employee.setArrivalTime(arrivalTime);
        employee.setNumberOfTasksCompleted(numberOfTasksCompleted);
        employee.setStatus(status);
        return employee;
    }

}
