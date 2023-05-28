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
import java.util.List;

public class ClientRpcWorker implements Runnable, IObserver {
    private final IServices server;
    private final Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientRpcWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected){
            try{
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response!=null){
                    sendResponse(response);
                }
            }catch (IOException | ClassNotFoundException e){
                connected = false;
            }

            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        try{
            input.close();
            output.close();
            connection.close();
        }catch (IOException e){
            System.out.println("Error : " + e);
        }
    }

    private Response handleRequest(Request request){
        Response response = null;
        // verify the instance
        switch (request.type()) {
            case LOGIN_EMPLOYEE -> {
                response = solveLoginEmployee(request);
                System.out.println("LOGIN EMPLOYEE REQUEST...");
            }
            case LOGOUT_EMPLOYEE -> {
                response = solveLogoutEmployee(request);
                System.out.println("LOGOUT EMPLOYEE REQUEST...");
            }
            case LOGIN_BOSS -> {
                response = solveLoginBoss(request);
                System.out.println("LOGIN BOSS REQUEST");
            }
            case LOGOUT_BOSS -> {
                response = solveLogoutBoss(request);
                System.out.println("LOGOUT BOSS REQUEST");
            }
            case UPDATE_EMPLOYEE_STATUS_TO_PRESENT -> {
                response = solveUpdateStatusToPresentEmployee(request);
                System.out.println("UPDATE STATUS TO PRESENT EMPLOYEE REQUEST");
            }
            case UPDATE_EMPLOYEE_STATUS_TO_ABSENT -> {
                response = solveUpdateStatusToAbsentEmployee(request);
                System.out.println("UPDATE STATUS TO ABSENT EMPLOYEE REQUEST");
            }
            case FIND_ALL_EMPLOYEES_WITH_PRESENT_STATUS -> {
                response = solveFindAllEmployeesWithPresentStatus();
                System.out.println("FIND ALL EMPLOYEES WITH PRESENT STATUS REQUEST");
            }
            case ADD_EMPLOYEE_IN_TABLE -> {
                response = solveAddEmployeeInTable();
                System.out.println("ADD EMPLOYEE IN TABLE REQUEST");
            }
            case ADD_TASK_IN_LIST -> {
                response = solveAddTaskInList(request);
                System.out.println("ADD TASK IN LIST REQUEST");
            }
            case FIND_ALL_TASKS_OF_EMPLOYEE -> {
                response = solveFindAllTasksOfEmployee(request);
                System.out.println("FIND ALL TASKS OF EMPLOYEE REQUEST");
            }case UPDATE_TASKS_STATUS_TO_COMPLETED -> {
                 response = solveUpdateTasksStatusToCompleted(request);
                System.out.println("UPDATE TASKS STATUS TO COMPLETED");
            }case UPDATE_NUMBER_OF_COMPLETED_TASKS -> {
                response = solveUpdateNumberOfCompletedTasks(request);
            }
            case UPDATE_NUMBER_OF_COMPLETED_TASKS_FROM_TABLE -> {
                response = solveUpdateNumberOfCompletedTasksFromTable(request);
            }
        }
        return response;
    }

    private Response solveLoginEmployee(Request request){
        EmployeeDTO employeeDTO = (EmployeeDTO) request.data();
        Employee employee = new Employee();
        employee.setEmail(employeeDTO.getEmail());
        employee.setPassword(employeeDTO.getPassword());
        try{
            long id = server.loginEmployee(employee, this);
            return new Response.Builder().type(ResponseType.OK).data(id).build();
        }catch(Exception ex){
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response solveLogoutEmployee(Request request){
        EmployeeDTO employeeDTO = (EmployeeDTO) request.data();
        Employee employee = new Employee();
        employee.setEmail(employeeDTO.getEmail());
        employee.setPassword(employeeDTO.getPassword());
        try{
            server.logoutEmployee(employee, this);
            connected = false;
            return new Response.Builder().type(ResponseType.OK).data(employeeDTO).build();
        }catch (Exception ex){
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response solveLoginBoss(Request request){
        BossDTO bossDTO = (BossDTO) request.data();
        Boss boss = new Boss();
        boss.setEmail(bossDTO.getEmail());
        boss.setPassword(bossDTO.getPassword());
        try{
            server.loginBoss(boss, this);
            return new Response.Builder().type(ResponseType.OK).data(bossDTO).build();
        }catch (Exception ex){
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
     }

     private Response solveLogoutBoss(Request request){
         BossDTO bossDTO = (BossDTO) request.data();
         Boss boss = new Boss();
         boss.setEmail(bossDTO.getEmail());
         boss.setPassword(bossDTO.getPassword());
         try{
             server.logoutBoss(boss, this);
             connected = false;
             return new Response.Builder().type(ResponseType.OK).data(bossDTO).build();
         }catch (Exception ex){
             connected = false;
             return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
         }
     }

     private Response solveFindAllTasksOfEmployee(Request request){
         try{
             String emailEmployee = (String) request.data();
             List<Task> tasks = server.findAllTasksOfEmployee(emailEmployee);
             return new Response.Builder().type(ResponseType.OK).data(tasks).build();
         }catch (Exception ex){
             return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
         }
     }


    private Response solveUpdateStatusToPresentEmployee(Request request){
        Employee employee = (Employee) request.data();
        try{
            long id = server.updateEmployeeStatusToPresentAndArrivalTime(employee.getId(), employee.getArrivalTime());
            return new Response.Builder().type(ResponseType.OK).data(id).build();
        }catch (Exception ex){
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
     }

     private Response solveUpdateStatusToAbsentEmployee(Request request){
         long idEmployee = Long.parseLong(request.data().toString());
         System.out.println(idEmployee);
         try{
             long id = server.updateEmployeeStatusToAbsent(idEmployee);
             return new Response.Builder().type(ResponseType.OK).data(id).build();
         }catch (Exception ex){
             return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
         }
     }

     private Response solveUpdateTasksStatusToCompleted(Request request){
        List<Long> ids = (List<Long>) request.data();
        try{
            server.updateTasksStatusToCompleted(ids);
            return new Response.Builder().type(ResponseType.OK).build();
        } catch (Exception ex) {
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
     }

     private Response solveUpdateNumberOfCompletedTasks(Request request){
        EmployeeTaskDTO employeeTaskDTO = (EmployeeTaskDTO) request.data();
        try{
            server.updateNumberOfCompletedTasks(employeeTaskDTO.getNumberOfTasks(), employeeTaskDTO.getEmail());
            return new Response.Builder().type(ResponseType.OK).build();
        }catch (Exception ex){
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
     }

     private Response solveFindAllEmployeesWithPresentStatus(){
        try{
            List<Employee> employees = server.findAllEmployeesWithPresentStatus();
            return new Response.Builder().type(ResponseType.OK).data(employees).build();
        }catch (Exception ex){
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
     }

     private Response solveAddEmployeeInTable(){
        try{
            server.addEmployeeInTable();
            return new Response.Builder().type(ResponseType.OK).build();
        }catch (Exception ex){
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
     }
     private Response solveAddTaskInList(Request request){
        try{
            Task task = (Task) request.data();
            server.addTaskInList(task);
            return new Response.Builder().type(ResponseType.OK).build();
        } catch (Exception ex) {
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
     }

     private Response solveUpdateNumberOfCompletedTasksFromTable(Request request){
         try{
             server.updateNumberOfCompletedTasksFromTable();
             return new Response.Builder().type(ResponseType.OK).build();
         } catch (Exception ex) {
             return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
         }
     }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response : " + response);
        synchronized (output){
            output.writeObject(response);
            output.flush();
        }
    }

    @Override
    public void addEmployee() {
        try{
            sendResponse(new Response.Builder().type(ResponseType.TABLE_UPDATED).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addTask() {
        try{
            sendResponse(new Response.Builder().type(ResponseType.LIST_UPDATED).build());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateNumberOfCompletedTasks() throws Exception {
        try{
            sendResponse(new Response.Builder().type(ResponseType.NUMBER_OF_COMPLETED_TASKS_FROM_TABLE_UPDATED).build());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
