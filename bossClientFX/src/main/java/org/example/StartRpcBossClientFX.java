package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LogInBossController;
import org.example.controller.MainBossController;
import org.example.controller.TasksBossController;
import org.networking.rpcprotocol.ServicesRpcProxy;
import org.services.IServices;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartRpcBossClientFX extends Application {

    private static final int defaultPort = 33333;

    private static final String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("IN CLIENT START");
        Properties clientProps = new Properties();
        try{
            clientProps.load(new FileReader("E:\\CURSURI\\ANUL 2 SEMESTRUL 2\\Ingineria sistemelor soft\\Laborator 3\\Monitoring\\employeeClientFX\\src\\main\\resources\\appemployeeclient.properties"));
            System.out.println("Client props set.");
            clientProps.list(System.out);
        }catch (IOException e){
            System.err.println("CANNOT FIND appclient.properties" + e);
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultPort;

        try{
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        }catch (NumberFormatException ex){
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IServices server = new ServicesRpcProxy(serverIP, serverPort);

        FXMLLoader logInLoader=new FXMLLoader();
        logInLoader.setLocation(getClass().getResource("/logIn-view.fxml"));
        Parent root = logInLoader.load();
        LogInBossController logInController = logInLoader.getController();
        logInController.setServer(server);

        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("/main-view.fxml"));
        Parent mainEmployeeRoot = mainLoader.load();
        MainBossController mainBossController = mainLoader.getController();

        FXMLLoader tasksLoader = new FXMLLoader();
        tasksLoader.setLocation(getClass().getResource("/tasks-view.fxml"));
        Parent tasksRoot = tasksLoader.load();
        TasksBossController tasksController = tasksLoader.getController();
        mainBossController.setTasksParent(tasksRoot);
        mainBossController.setTasksController(tasksController);

        logInController.setParent(mainEmployeeRoot);
        logInController.setMainBossController(mainBossController);
        logInController.setLogInStage(primaryStage);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Boss");
        primaryStage.show();
    }
}