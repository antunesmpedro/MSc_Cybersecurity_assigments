package p2p;

import java.util.logging.Logger;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class rpcServer implements Runnable{
    private static final Logger logger = Logger.getLogger(rpcServer.class.getName());

    /* The port on which the server should run */
    private int port;
    private Server server;

    public rpcServer(int port){
        this.port = port;
    }

    @Override
    public void run() throws Exception {
        server = ServerBuilder.forPort(port)
                .addService(GreeterGrpc.bindService(new PingImpl()))
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                rpcServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private class PingImpl implements GreeterGrpc.SendPing {

        @Override
        public void Ping(PingRequest req, StreamObserver<PingResponse> responseObserver) {
            PingResponse reply = PingResponse.newBuilder().setRandomId(req.getRandomId()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
