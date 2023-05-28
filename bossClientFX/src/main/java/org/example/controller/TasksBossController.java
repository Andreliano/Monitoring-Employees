package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.model.Employee;
import org.model.Task;
import org.model.TaskStatus;
import org.services.IServices;

import java.time.LocalDateTime;

public class TasksBossController {

    private IServices server;
    private Employee employee;
    @FXML
    private TextArea taskDescription;

    private Stage mainBossStage;

    private Stage tasksStage;

    @FXML
    public void onBackButtonClicked(){
        tasksStage.close();
        mainBossStage.show();
    }

    @FXML
    public void onSendTaskButtonClicked() throws Exception {
        if(taskDescription.getText().length() > 0){
            Task task = new Task(employee.getEmail(), taskDescription.getText(), LocalDateTime.now(), TaskStatus.UNCOMPLETED, false);
            server.addTaskInList(task);
        }else{
            Alert message=new Alert(Alert.AlertType.ERROR);
            message.initOwner(tasksStage);
            message.setTitle("Eroare de validare!");
            message.setContentText("Trebuie sa introduceti o descriere sarcinii inainte de a o trimite unui angajat!");
            message.show();
        }
    }

    public void setMainBossStage(Stage mainBossStage){
        this.mainBossStage = mainBossStage;
    }

    public void setTasksStage(Stage tasksStage){
        this.tasksStage = tasksStage;
    }

    public void setEmployee(Employee employee){
        this.employee = employee;
    }

    public void setServer(IServices server){
        this.server = server;
    }

}
