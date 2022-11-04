package kademlia_p2p;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kademlia_p2p.grpc.KademliaP2P;
import kademlia_p2p.grpc.kademlia_p2pGrpc;


public class RPCClient {

    private final ManagedChannel channel;
    private final kademlia_p2pGrpc.kademlia_p2pBlockingStub blockingStub;

    /** Construct client connecting to node at localhost:port */
    public RPCClient(int port) {
        // Create our client channel that communicate with kademlia grpcServer.
        // We will not be implementing security.
        this.channel = ManagedChannelBuilder.forAddress("localhost", port)
                .usePlaintext()
                .build();

        // stubs to call particular api -> stubs -  generate from proto
        //
        // blocking stub -> kind of makes an SYNCHRONOUS call
        // make request to the server, client will wait for server response
        // stub -> kind of makes an ASYNCHRONOUS call. after sending the request, client will proceed with its operations
        // it will capture the server response whenever the server sends using callbacks!!
        // future stub -> ??
        this.blockingStub = kademlia_p2pGrpc.newBlockingStub(channel);
    }
}
