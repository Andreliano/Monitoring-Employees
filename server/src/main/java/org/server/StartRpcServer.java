package org.server;

import org.networking.servers.AbstractServer;
import org.networking.servers.RpcConcurrentServer;
import org.networking.servers.ServerException;
import org.persistence.database.BossDataBaseRepository;
import org.persistence.database.EmployeeDataBaseRepository;
import org.persistence.database.TaskDataBaseRepository;
import org.persistence.repository.BossRepository;
import org.persistence.repository.EmployeeRepository;
import org.persistence.repository.TaskRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartRpcServer{

    private static int defaultPort = 33333;

    public static void main(String[] args) {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("E:\\CURSURI\\ANUL 2 SEMESTRUL 2\\Ingineria sistemelor soft\\Laborator 3\\Monitoring\\server\\src\\main\\resources\\server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
        }

        BossRepository bossRepository = new BossDataBaseRepository();
        EmployeeRepository employeeRepository = new EmployeeDataBaseRepository();
        TaskRepository taskRepository = new TaskDataBaseRepository();

        ServiceImplementation serviceImplementation = new ServiceImplementation(bossRepository, employeeRepository, taskRepository);

        int chatServerPort = defaultPort;
        try{
            chatServerPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port : " + chatServerPort);
        AbstractServer server = new RpcConcurrentServer(chatServerPort, serviceImplementation);
        try{
            server.start();
        }catch (ServerException e){
            System.err.println("Error starting the server " + e.getMessage());
        }
    }
}