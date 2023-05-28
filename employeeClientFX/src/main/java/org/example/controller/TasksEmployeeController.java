package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.model.Employee;
import org.model.Task;
import org.model.TaskStatus;
import org.services.IObserver;
import org.services.IServices;

import java.sql.SQLOutput;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TasksEmployeeController implements IObserver {

    private IServices server;

    private Employee employee;
    private Stage mainEmployeeStage;

    private Stage tasksStage;

    private final ObservableList<Task> tasksModel = FXCollections.observableArrayList();

    @FXML
    private ListView<Task> tasksList;

    @FXML
    public void initialize() {
        tasksList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Task> call(ListView<Task> param) {
                return new ListCell<>() {
                    protected void updateItem(Task task, boolean empty) {
                        super.updateItem(task, empty);
                        if (task == null || empty) {
                            setText(null);
                        } else {
                            setDisable(task.isBlocked());
                            String status;
                            if (task.getStatus().equals(TaskStatus.COMPLETED)) {
                                status = "COMPLETATA";
                            } else {
                                status = "NECOMPLETATA";
                            }
                            setText(task.getDescription() + "\n" + "Sarcina trimisa in data de: " + task.getMomentOfSending().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\nStatusul sarcinii este: " + status);
                        }
                    }
                };
            }
        });
        tasksList.setItems(tasksModel);
        tasksList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (tasksList.getSelectionModel().getSelectedItem().getStatus().equals(TaskStatus.COMPLETED)) {
                    tasksList.getSelectionModel().getSelectedItem().setStatus(TaskStatus.UNCOMPLETED);
                    initialize();
                } else {
                    tasksList.getSelectionModel().getSelectedItem().setStatus(TaskStatus.COMPLETED);
                    initialize();
                }
            }
        });
    }

    @FXML
    public void onBackButtonClicked() {
        tasksStage.close();
        mainEmployeeStage.show();
    }

    @FXML
    public void onCompletedTasksButtonClicked() throws Exception {
        List<Long> ids = new ArrayList<>();
        int size = tasksList.getItems().size();
        for (int i = 0; i < size; i++) {
            if (tasksList.getItems().get(i).getStatus().equals(TaskStatus.COMPLETED) && !tasksList.getItems().get(i).isBlocked()) {
                ids.add(tasksList.getItems().get(i).getId());
                tasksList.getItems().get(i).setBlocked(true);
            }
        }

        if (ids.size() > 0) {
            server.updateTasksStatusToCompleted(ids);
            server.updateNumberOfCompletedTasks(ids.size(), employee.getEmail());
            server.updateNumberOfCompletedTasksFromTable();
            initialize();
        }else{
            Alert message=new Alert(Alert.AlertType.ERROR);
            message.initOwner(tasksStage);
            message.setTitle("Eroare de validare!");
            message.setContentText("Trebuie sa bifati cel putin o sarcina ca fiind \"COMPLETATA\"!");
            message.show();
        }
    }

    public void initModel() throws Exception {
        List<Task> tasks = server.findAllTasksOfEmployee(employee.getEmail());
        tasksModel.setAll(tasks);
    }

    public void setServer(IServices server) {
        this.server = server;
    }

    public void setMainEmployeeStage(Stage mainEmployeeStage) {
        this.mainEmployeeStage = mainEmployeeStage;
    }

    public void setTasksStage(Stage tasksStage) {
        this.tasksStage = tasksStage;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public void addEmployee() {

    }

    @Override
    public void addTask() {
        Platform.runLater(() -> {
            try {
                System.out.println("da");
                initModel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void updateNumberOfCompletedTasks() throws Exception {

    }
}
