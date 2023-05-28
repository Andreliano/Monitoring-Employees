package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import org.model.Employee;
import org.services.IObserver;
import org.services.IServices;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class MainEmployeeController {

    private Employee employee;

    private Stage logInStage;

    private Stage mainEmployeeStage;

    private IServices server;

    private TasksEmployeeController tasksController;

    private Scene tasksScene;

    @FXML
    private Spinner<Integer> hour;

    @FXML
    private Spinner<Integer> minute;


    @FXML
    public void initialize(){
        hour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8));
        minute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    @FXML
    public void onPresentButtonClicked() throws Exception {
        String date = LocalDate.now().toString();
        String hour;
        String minute;
        String time;
        if(this.hour.getValue() < 10){
            hour = "0" + this.hour.getValue().toString();
        }else{
            hour = this.hour.getValue().toString();
        }
        if(this.minute.getValue() < 10){
            minute = "0" + this.minute.getValue().toString();
        }else{
            minute = this.minute.getValue().toString();
        }
        time = hour + ":" + minute;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, formatter);
        long idEmployee = server.updateEmployeeStatusToPresentAndArrivalTime(employee.getId(), dateTime);
        if(idEmployee > 0) {
            server.addEmployeeInTable();
        }
    }

    @FXML
    public void onTasksButtonClicked() throws Exception {
        Stage tasksStage = new Stage();
        tasksStage.setScene(this.tasksScene);
        tasksStage.setTitle("Sarcinile lui " + "\"" + employee.getEmail() + "\"");
        tasksController.setMainEmployeeStage(mainEmployeeStage);
        tasksController.setTasksStage(tasksStage);
        tasksController.setServer(server);
        tasksController.setEmployee(employee);
        tasksController.initModel();
        mainEmployeeStage.close();
        tasksStage.show();
    }

    @FXML
    public void employeeLogOut(){
        try {
            mainEmployeeStage.close();
            logInStage.show();
            long idEmployee = server.updateEmployeeStatusToAbsent(employee.getId());
            if(idEmployee > 0) {
                server.addEmployeeInTable();
            }
            server.logoutEmployee(employee, new IObserver() {
                @Override
                public void addEmployee() throws Exception {

                }

                @Override
                public void addTask() throws Exception {

                }

                @Override
                public void updateNumberOfCompletedTasks() throws Exception {

                }
            });
        }catch (Exception ex){
            System.out.println("Logout error " + ex.getMessage());
        }
    }

    public void setEmployee(Employee employee){
        this.employee = employee;
    }

    public void setServer(IServices server){
        this.server = server;
    }

    public void setLogInStage(Stage logInStage){
        this.logInStage = logInStage;
    }

    public void setMainEmployeeStage(Stage mainEmployeeStage){
        this.mainEmployeeStage = mainEmployeeStage;
    }

    public void setTasksParent(Parent tasksParent){
        this.tasksScene = new Scene(tasksParent);
    }

    public void setTasksController(TasksEmployeeController tasksController){
        this.tasksController = tasksController;
    }

}
