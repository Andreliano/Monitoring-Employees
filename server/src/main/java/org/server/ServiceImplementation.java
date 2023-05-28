package org.server;

import org.model.Boss;
import org.model.Employee;
import org.model.Task;
import org.persistence.repository.BossRepository;
import org.persistence.repository.EmployeeRepository;
import org.persistence.repository.TaskRepository;
import org.services.IObserver;
import org.services.IServices;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceImplementation implements IServices {
    private BossRepository bossRepository;

    private EmployeeRepository employeeRepository;

    private TaskRepository taskRepository;

    private Map<String, IObserver> loggedClients;

    private final int defaultThreadsNo = 5;

    public ServiceImplementation(){

    }

    public ServiceImplementation(BossRepository bossRepository, EmployeeRepository employeeRepository, TaskRepository taskRepository){
        this.bossRepository = bossRepository;
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized long loginEmployee(Employee employee, IObserver client) throws Exception {
        System.out.println("LogIn Employee From ServiceImpl");
        long id = employeeRepository.checkEmployeeAccountExistence(employee.getEmail(), employee.getPassword());
        if (id != 0) {
            if (loggedClients.get(employee.getEmail()) != null) {
                throw new Exception("Employee already logged in");
            }
            loggedClients.put(employee.getEmail(), client);
        } else {
            throw new Exception("Authentication failed!");
        }
        return id;
    }

    @Override
    public void logoutEmployee(Employee employee, IObserver client) throws Exception {
        IObserver localClient = loggedClients.remove(employee.getEmail());
        if (localClient == null)
            throw new Exception("Employee " + employee.getEmail() + " is not logged in.");
    }

    @Override
    public synchronized void loginBoss(Boss boss, IObserver client) throws Exception {
        System.out.println("LogIn Boss From ServiceImpl");
        long id = bossRepository.checkBossAccountExistence(boss.getEmail(), boss.getPassword());
        if (id != 0){
            if(loggedClients.get(boss.getEmail()) != null){
                throw new Exception("Boss already logged in");
            }
            loggedClients.put(boss.getEmail(), client);
        } else {
            throw new Exception("Authentication failed!");
        }
    }

    @Override
    public void logoutBoss(Boss boss, IObserver client) throws Exception {
        IObserver localClient = loggedClients.remove(boss.getEmail());
        if (localClient == null)
            throw new Exception("Boss " + boss.getEmail() + " is not logged in.");
    }

    @Override
    public long updateEmployeeStatusToPresentAndArrivalTime(long idEmployee, LocalDateTime arrivalTime) throws Exception {
        System.out.println("Update Employee Status FROM ServiceImpl");
        long id = employeeRepository.updateEmployeeStatusToPresentAndArrivalTime(idEmployee, arrivalTime);
        if(id == 0){
            throw new Exception("The employee status could not be updated");
        }
        return id;
    }

    @Override
    public long updateEmployeeStatusToAbsent(long idEmployee){
        System.out.println("Update Employee Status FROM ServiceImpl");
        return employeeRepository.updateEmployeeStatusToAbsent(idEmployee);
    }

    @Override
    public List<Employee> findAllEmployeesWithPresentStatus() throws Exception {
        System.out.println("Find All Employees With Present Status FROM ServiceImpl");
        List<Employee> employees = (List<Employee>) employeeRepository.findAllEmployeesWithPresentStatus();
        if(employees == null){
            throw new Exception("There are no employees present at work");
        }
        return employees;
    }

    @Override
    public void addEmployeeInTable(){
        ExecutorService executorService = Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver client : loggedClients.values()){
            if(client != null){
                executorService.execute(() -> {
                    try {
                        System.out.println("Notify");
                        client.addEmployee();
                    } catch (Exception e) {
                        System.err.println("Error notifying person " + e);
                    }
                });
            }
        }
        executorService.shutdown();
    }

    @Override
    public void updateNumberOfCompletedTasksFromTable() {
        ExecutorService executorService = Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver client : loggedClients.values()){
            if(client != null){
                executorService.execute(() -> {
                    try {
                        System.out.println("Notify");
                        client.updateNumberOfCompletedTasks();
                    } catch (Exception e) {
                        System.err.println("Error notifying person " + e);
                    }
                });
            }
        }
        executorService.shutdown();
    }

    @Override
    public void addTaskInList(Task task) throws Exception {
        System.out.println("Add Task In List FROM ServiceImpl");
        Task result = taskRepository.save(task);
        if(result == null){
            throw new Exception("The task could not be added");
        }
        ExecutorService executorService = Executors.newFixedThreadPool(defaultThreadsNo);
        for(Map.Entry<String, IObserver> entry: loggedClients.entrySet()){
            System.out.println(entry.getValue() + " " + entry.getKey() + " " + task.getEmailEmployee());
            if(entry.getValue() != null && entry.getKey().equals(task.getEmailEmployee())){
                executorService.execute(() -> {
                    try {
                        System.out.println("Notify");
                        entry.getValue().addTask();
                    } catch (Exception e) {
                        System.err.println("Error notifying person " + e);
                    }
                });
            }
        }
        executorService.shutdown();
    }

    @Override
    public List<Task> findAllTasksOfEmployee(String emailEmployee) throws Exception {
        System.out.println("Find All Tasks FROM ServiceImpl");
        List<Task> tasks = (List<Task>) taskRepository.findAllTasksOfEmployee(emailEmployee);
        if(tasks == null){
            throw new Exception("There are no tasks");
        }
        return tasks;
    }

    @Override
    public void updateTasksStatusToCompleted(List<Long> ids) throws Exception {
        System.out.println("Update Tasks Status To Completed FROM ServiceImpl");
        List<Long> idList = taskRepository.updateTasksToCompleted(ids);
        if(idList == null){
            throw new Exception("There are no completed tasks");
        }
    }

    @Override
    public void updateNumberOfCompletedTasks(long numberOfTasks, String email) throws Exception {
        System.out.println("Update Number Of Completed Tasks FROM ServiceImpl");
        long numberOfCompletedTasks = employeeRepository.updateNumberOfTasksCompleted(numberOfTasks, email);
        if(numberOfCompletedTasks == 0){
            throw new Exception("Number Of tasks couldn't be updated");
        }
    }

}
