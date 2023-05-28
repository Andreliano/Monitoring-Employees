package org.networking.servers;

import org.networking.rpcprotocol.ClientRpcWorker;
import org.services.IServices;

import java.net.Socket;

public class RpcConcurrentServer extends AbsConcurrentServer{

    private final IServices server;

    public RpcConcurrentServer(int port, IServices s) {
        super(port);
        this.server = s;
        System.out.println("ObjectConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcWorker worker = new ClientRpcWorker(server, client);
        return new Thread(worker);
    }
}
