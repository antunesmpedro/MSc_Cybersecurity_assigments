package kademlia_p2p;

import kademlia_p2p.grpc.KademliaP2P;
import kademlia_p2p.grpc.kademlia_p2pGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GRPCDemo {

    private static final int SERVER_PORT = 9090;
    private static final int CLIENT_PORT = 9091;

    private static void server() throws IOException, InterruptedException {
        // build kademlia p2p server instance
        Server server = ServerBuilder.forPort(SERVER_PORT).addService(new Kademlia_p2pService()).build();

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

    private static void stopServer(Server server) throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private static void client() {
        // example bucket node
        KademliaP2P.Node bucket_node = KademliaP2P.Node.newBuilder().setGuid(1).setIpAddress("localhost")
                .setUdpPort(SERVER_PORT).build();

        // bucket
        Map<Integer,KademliaP2P.Node> kBuckets = new HashMap<Integer, KademliaP2P.Node>();
        kBuckets.put(1,bucket_node);
        kBuckets.put(2,bucket_node);
        kBuckets.put(4,bucket_node);
        kBuckets.put(8,bucket_node);

        // bootstrap node
        KademliaP2P.Node bootstrap_node = KademliaP2P.Node.newBuilder().setGuid(0).setIpAddress("localhost")
                .setUdpPort(SERVER_PORT).putAllClosestNodes(kBuckets).build();


        lookup(bootstrap_node,bucket_node);

        return ;
    }


    // developed node request and find node response
    private static void lookup(KademliaP2P.Node requester, KademliaP2P.Node target) {

        // create channel that communicate with kademlia grpcServer
        ManagedChannel channel =  ManagedChannelBuilder.forAddress("localhost",CLIENT_PORT).usePlaintext().build();

        // stubs to call particular api -> stubs -  generate from proto
        //
        // blocking stub -> kind of makes an SYNCHRONOUS call
        // make request to the server, client will wait for server response
        // stub -> kind of makes an ASYNCHRONOUS call. after sending the request, client will proceed with its operations
        // it will capture the server response whenever the server sends using callbacks!!
        // future stub -> ??
        kademlia_p2pGrpc.kademlia_p2pBlockingStub kademliaStub = kademlia_p2pGrpc.newBlockingStub(channel);

        // build the request

        //build node request
        KademliaP2P.FindNodeRequest findNodeRequest = KademliaP2P.FindNodeRequest.newBuilder()
                .setRequester(requester).setTarget(target).build();

        //get Find Node response
        KademliaP2P.FindNodeResponse response = kademliaStub.findNode(findNodeRequest);

        KademliaP2P.Node responseClosestNode = response.getClosestNode();

        // print response
        System.out.println(response.getDefaultResponse().getResponseMessage());
        System.out.println(response.getDefaultResponse().getResponseCode());
        System.out.println(responseClosestNode);

        //recursive way
        int nearestNode = responseClosestNode.getGuid();
        int target_guid = target.getGuid();

        if ( nearestNode != target_guid )
            lookup(responseClosestNode, target);

        return ;
    }
}
