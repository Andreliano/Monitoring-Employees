package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.model.Boss;
import org.model.Employee;
import org.services.IObserver;
import org.services.IServices;

import java.io.IOException;
import java.util.List;

public class MainBossController implements IObserver {

    private final ObservableList<Employee> employeesModel = FXCollections.observableArrayList();

    private IServices server;

    private Boss boss;

    private Stage logInStage;

    private Stage mainBossStage;

    private TasksBossController tasksController;
    private Scene tasksScene;

    @FXML
    private TableView<Employee> employeesTable;

    @FXML
    private TableColumn<Employee, String> firstName;

    @FXML
    private TableColumn<Employee, String> lastName;

    @FXML
    private TableColumn<Employee, String> email;

    @FXML
    private TableColumn<Employee, String> arrivalTime;

    @FXML
    private TableColumn<Employee, String> numberOfTasksCompleted;


    @FXML
    public void initialize(){
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        arrivalTime.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        numberOfTasksCompleted.setCellValueFactory(new PropertyValueFactory<>("numberOfTasksCompleted"));
        employeesTable.setItems(employeesModel);
    }
    public void initModel() throws Exception {
        List<Employee> employeeList = server.findAllEmployeesWithPresentStatus();
        employeesModel.setAll(employeeList);
    }

    public void setServer(IServices server){
        this.server = server;
    }

    public void setLogInStage(Stage logInStage){
        this.logInStage = logInStage;
    }

    public void setMainBossStage(Stage mainBossStage){
        this.mainBossStage = mainBossStage;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public void setTasksParent(Parent tasksParent){
        this.tasksScene = new Scene(tasksParent);
    }

    public void setTasksController(TasksBossController tasksController){
        this.tasksController = tasksController;
    }

    @FXML
    public void onSelectedEmployee() throws IOException {
        ObservableList<Employee> selectedEmployees = employeesTable.getSelectionModel().getSelectedItems();
        if(selectedEmployees.size() == 1){
            Employee selectedEmployee = selectedEmployees.get(0);
            Stage tasksStage = new Stage();
            tasksStage.setScene(this.tasksScene);
            tasksStage.setTitle("Sarcina");
            tasksController.setMainBossStage(mainBossStage);
            tasksController.setTasksStage(tasksStage);
            tasksController.setEmployee(selectedEmployee);
            tasksController.setServer(server);
            mainBossStage.close();
            tasksStage.show();
        }
    }

    @FXML
    public void bossLogOut(){
        try {
            mainBossStage.close();
            logInStage.show();
            server.logoutBoss(boss, this);
        }catch (Exception ex){
            System.out.println("Logout error " + ex);
        }
    }


    @Override
    public void addEmployee() throws Exception {
        Platform.runLater(() -> {
            try {
                System.out.println("BOSS");
                initModel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void addTask() throws Exception {

    }

    @Override
    public void updateNumberOfCompletedTasks() throws Exception {
        Platform.runLater(() -> {
            try {
                System.out.println("BOSS!!!!");
                initModel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
