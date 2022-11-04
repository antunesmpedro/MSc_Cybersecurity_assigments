package kademlia_p2p;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RPCServer implements Runnable{
    private static final int SERVER_PORT = 9090;

    @Override
    public void run() {
        // build kademlia p2p server instance
        Server server = ServerBuilder.forPort(SERVER_PORT).addService(new Kademlia_p2pService()).build();
        try {
            server.start();

            System.out.println("SERVER STARTED AT "+ server.getPort());

            server.awaitTermination(); //Wait until the app is killed so that our server doesn't stop immediately.

            //This is some magic to make sure we don't keep the server running if the app is terminated externally
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    stopServer(server);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }));
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static void stopServer(Server server) throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
