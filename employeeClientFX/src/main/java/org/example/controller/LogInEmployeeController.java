package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.model.Employee;
import org.services.IServices;

public class LogInEmployeeController {

    private Stage logInStage;
    private IServices server;

    private Scene mainEmployeeScene;
    private MainEmployeeController mainEmployeeController;

    private TasksEmployeeController tasksEmployeeController;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    public void employeeLogIn(){
        Employee employee = new Employee();
        employee.setEmail(email.getText());
        employee.setPassword(password.getText());
        try {
            long idEmployee = server.loginEmployee(employee, tasksEmployeeController);
            employee.setId(idEmployee);
            System.out.println("Login employee successfully");

            Stage mainEmployeeStage = new Stage();
            mainEmployeeStage.setScene(mainEmployeeScene);
            mainEmployeeStage.setTitle("Angajat");
            mainEmployeeStage.setResizable(false);

            mainEmployeeController.setEmployee(employee);
            mainEmployeeController.setServer(server);
            mainEmployeeController.setLogInStage(logInStage);
            mainEmployeeController.setMainEmployeeStage(mainEmployeeStage);

            mainEmployeeStage.show();
            logInStage.close();


        }catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("Authentication failure");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void setServer(IServices server){
        this.server = server;
    }

    public void setParent(Parent mainEmployeeParent){
        this.mainEmployeeScene = new Scene(mainEmployeeParent);
    }

    public void setMainEmployeeController(MainEmployeeController mainEmployeeController){
        this.mainEmployeeController = mainEmployeeController;
    }

    public void setTasksController(TasksEmployeeController tasksEmployeeController){
        this.tasksEmployeeController = tasksEmployeeController;
    }

    public void setLogInStage(Stage logInStage) {
        this.logInStage = logInStage;
    }

}
