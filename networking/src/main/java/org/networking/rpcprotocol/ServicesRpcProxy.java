package org.networking.rpcprotocol;

import org.model.Boss;
import org.model.Employee;
import org.model.Task;
import org.networking.dto.BossDTO;
import org.networking.dto.EmployeeDTO;
import org.networking.dto.EmployeeTaskDTO;
import org.services.IObserver;
import org.services.IServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ServicesRpcProxy implements IServices {
    private final String host;

    private final int port;

    private IObserver client1;

    private IObserver client2;

    private ObjectInputStream input;

    private ObjectOutputStream output;

    private Socket connection;

    private final BlockingQueue<Response> qResponses;

    private volatile boolean finished;

    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qResponses = new LinkedBlockingDeque<>();
    }

    @Override
    public long loginEmployee(Employee employee, IObserver client) throws Exception {
        initializeConnection();
        System.out.println("initializeConnection() done");
        EmployeeDTO employeeDTO = new EmployeeDTO(employee.getEmail(), employee.getPassword());
        System.out.println(employeeDTO.getEmail());
        Request request = new Request.Builder().type(RequestType.LOGIN_EMPLOYEE).data(employeeDTO).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client2 = client;
            return Long.parseLong(response.data().toString());
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            System.out.println("Close connection!");
            closeConnection();
            throw new Exception(err);
        }
        return 0;
    }

    @Override
    public void logoutEmployee(Employee employee, IObserver client) throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(employee.getEmail(), employee.getPassword());
        Request request = new Request.Builder().type(RequestType.LOGOUT_EMPLOYEE).data(employeeDTO).build();
        sendRequest(request);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public void loginBoss(Boss boss, IObserver client) throws Exception {
        initializeConnection();
        System.out.println("initializeConnection() done");
        BossDTO bossDTO = new BossDTO(boss.getEmail(), boss.getPassword());
        System.out.println(bossDTO.getEmail());
        Request request = new Request.Builder().type(RequestType.LOGIN_BOSS).data(bossDTO).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client1 = client;
            return;
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            System.out.println("Close connection!");
            closeConnection();
            throw new Exception(err);
        }
    }

    @Override
    public void logoutBoss(Boss boss, IObserver client) throws Exception {
        BossDTO bossDTO = new BossDTO(boss.getEmail(), boss.getPassword());
        Request request = new Request.Builder().type(RequestType.LOGOUT_BOSS).data(bossDTO).build();
        sendRequest(request);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public void addTaskInList(Task task) throws Exception {
        Request request = new Request.Builder().type(RequestType.ADD_TASK_IN_LIST).data(task).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public List<Task> findAllTasksOfEmployee(String emailEmployee) throws Exception {
        Request request = new Request.Builder().type(RequestType.FIND_ALL_TASKS_OF_EMPLOYEE).data(emailEmployee).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            return (List<Task>) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
        return null;
    }

    @Override
    public void updateTasksStatusToCompleted(List<Long> ids) throws Exception {
        Request request = new Request.Builder().type(RequestType.UPDATE_TASKS_STATUS_TO_COMPLETED).data(ids).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public void updateNumberOfCompletedTasks(long numberOfTasks, String email) throws Exception {
        EmployeeTaskDTO employeeTaskDTO = new EmployeeTaskDTO(email, numberOfTasks);
        Request request = new Request.Builder().type(RequestType.UPDATE_NUMBER_OF_COMPLETED_TASKS).data(employeeTaskDTO).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public long updateEmployeeStatusToPresentAndArrivalTime(long idEmployee, LocalDateTime arrivalTime) throws Exception {
        Employee employee = new Employee();
        employee.setId(idEmployee);
        employee.setArrivalTime(arrivalTime);
        Request request = new Request.Builder().type(RequestType.UPDATE_EMPLOYEE_STATUS_TO_PRESENT).data(employee).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            return Long.parseLong(response.data().toString());
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
        return 0;
    }

    @Override
    public long updateEmployeeStatusToAbsent(long idEmployee) throws Exception {
        Request request = new Request.Builder().type(RequestType.UPDATE_EMPLOYEE_STATUS_TO_ABSENT).data(idEmployee).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            return Long.parseLong(response.data().toString());
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
        return 0;
    }

    @Override
    public List<Employee> findAllEmployeesWithPresentStatus() throws Exception {
        Request request = new Request.Builder().type(RequestType.FIND_ALL_EMPLOYEES_WITH_PRESENT_STATUS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            return (List<Employee>) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
        return null;
    }

    @Override
    public void addEmployeeInTable() throws Exception {
        Request request = new Request.Builder().type(RequestType.ADD_EMPLOYEE_IN_TABLE).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
    }

    @Override
    public void updateNumberOfCompletedTasksFromTable() throws Exception {
        Request request = new Request.Builder().type(RequestType.UPDATE_NUMBER_OF_COMPLETED_TASKS_FROM_TABLE).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new Exception(err);
        }
    }

    private void sendRequest(Request request) throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object " + e);
        }
    }

    private Response readResponse() {
        Response response = null;
        try {
            response = qResponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client1 = null;
            client2 = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdateTable() {
        try {
            if(client1 != null) {
                client1.addEmployee();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleUpdateNumberOfCompletedTasksFromTable() {
        try {
            if(client1 != null) {
                client1.updateNumberOfCompletedTasks();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleUpdateList() {
        try {
            if(client2 != null) {
                client2.addTask();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Response response = (Response) input.readObject();
                    System.out.println("response received " + response);
                    if (response.type() == ResponseType.TABLE_UPDATED) {
                        handleUpdateTable();
                    } else if (response.type() == ResponseType.LIST_UPDATED) {
                        handleUpdateList();
                    } else if (response.type() == ResponseType.NUMBER_OF_COMPLETED_TASKS_FROM_TABLE_UPDATED) {
                        handleUpdateNumberOfCompletedTasksFromTable();
                    } else {
                        try {
                            qResponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }

}
