package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.model.Boss;
import org.services.IServices;

public class LogInBossController {

    private Stage logInStage;
    private IServices server;

    private Scene mainBossScene;

    private MainBossController mainBossController;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    public void bossLogIn(){
        Boss boss = new Boss();
        boss.setEmail(email.getText());
        boss.setPassword(password.getText());

        try {
            server.loginBoss(boss, mainBossController);
            System.out.println("Login boss successfully");

            Stage mainBossStage = new Stage();
            mainBossStage.setScene(mainBossScene);
            mainBossStage.setTitle("Sef");
            mainBossStage.setResizable(false);
            mainBossController.setServer(server);
            mainBossController.setLogInStage(logInStage);
            mainBossController.setMainBossStage(mainBossStage);
            mainBossController.setBoss(boss);
            mainBossController.initModel();

            mainBossStage.show();
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

    public void setParent(Parent mainBossParent){
        this.mainBossScene = new Scene(mainBossParent);
    }

    public void setMainBossController(MainBossController mainBossController){
        this.mainBossController = mainBossController;
    }

    public void setLogInStage(Stage logInStage) {
        this.logInStage = logInStage;
    }


}
