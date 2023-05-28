package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LogInEmployeeController;
import org.example.controller.MainEmployeeController;
import org.example.controller.TasksEmployeeController;
import org.networking.rpcprotocol.ServicesRpcProxy;
import org.services.IServices;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartRpcEmployeeClientFX extends Application {

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
        LogInEmployeeController logInController = logInLoader.getController();
        logInController.setServer(server);

        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("/main-view.fxml"));
        Parent mainEmployeeRoot = mainLoader.load();

        MainEmployeeController mainEmployeeController = mainLoader.getController();

        FXMLLoader tasksLoader = new FXMLLoader();
        tasksLoader.setLocation(getClass().getResource("/tasks-view.fxml"));
        Parent tasksRoot = tasksLoader.load();
        TasksEmployeeController tasksController = tasksLoader.getController();
        mainEmployeeController.setTasksParent(tasksRoot);
        mainEmployeeController.setTasksController(tasksController);
        logInController.setTasksController(tasksController);

        logInController.setParent(mainEmployeeRoot);
        logInController.setMainEmployeeController(mainEmployeeController);
        logInController.setLogInStage(primaryStage);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Employee");
        primaryStage.show();

    }
}