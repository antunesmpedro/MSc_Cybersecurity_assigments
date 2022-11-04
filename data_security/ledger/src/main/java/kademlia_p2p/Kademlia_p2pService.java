package kademlia_p2p;

import kademlia_p2p.grpc.KademliaP2P;
import kademlia_p2p.grpc.kademlia_p2pGrpc.kademlia_p2pImplBase;
import io.grpc.stub.StreamObserver;
import java.lang.String;
import java.util.*;

public class Kademlia_p2pService extends kademlia_p2pImplBase{

    private static final int K_bits = 4;

    // returns xor of x and y
    private static int myXOR(int x, int y) {
        return ( (x|y) & (~x|~y) );
    }

    /**
     * TODO: FINISH IMPLEMENTATIONS OF KADEMLIA SERVICE PING FUNCTION AND ALL THE OTHERS
     *
     * */
    @Override
    public void ping(KademliaP2P.NodeRequest request, StreamObserver<KademliaP2P.APIResponse> responseObserver) {
        //super.ping(request, responseObserver);

        System.out.println("---------- Inside Ping ----------");

        // get request parameters
        int guid = request.getNode().getGuid();
        String ip = request.getNode().getIpAddress();
        int port = request.getNode().getUdpPort();

        // build the api response
        KademliaP2P.APIResponse.Builder response = KademliaP2P.APIResponse.newBuilder();

        /**
         * TODO: find_node function to return something to define

        if ( new_find_node(guid) ) {
            // return success message
            response.setResponseCode(200).setResponseMessage(":) |SUCCESS");
        }
        else{
            // returns failure message
            response.setResponseCode(401).setResponseMessage(":( |NODE "+ guid +"DID NOT RESPOND");
        }
         */
        //sending data back to the client
        responseObserver.onNext(response.build());

        // the grpc connection is closed
        responseObserver.onCompleted();
    }

    @Override
    public void store(KademliaP2P.NodeRequest request, StreamObserver<KademliaP2P.StoreResponse> responseObserver) {
        //super.store(request, responseObserver);

        System.out.println("---------- Inside Store ----------");

        // get request parameters
        int guid = request.getNode().getGuid();
        String ip = request.getNode().getIpAddress();
        int port = request.getNode().getUdpPort();


        // build the store response
        KademliaP2P.StoreResponse.Builder response = KademliaP2P.StoreResponse.newBuilder();
        /**
         * TODO: finish store kademlia service
         */

    }

    @Override
    public void findNode(KademliaP2P.FindNodeRequest request, StreamObserver<KademliaP2P.FindNodeResponse> responseObserver) {
        //super.store(request, responseObserver);

        System.out.println("---------- Inside Find Node ----------");

        // get requester node of request parameters
        KademliaP2P.Node requesterNode = request.getRequester();

        //get target node of request parameters
        int target_guid = request.getTarget().getGuid();
        Map<Integer,KademliaP2P.Node> target_hash_table = request.getTarget().getClosestNodesMap();

        // build the find node response
        KademliaP2P.APIResponse apiResponse;
        KademliaP2P.FindNodeResponse.Builder response = KademliaP2P.FindNodeResponse.newBuilder();

        //calculate closest bucket
        KademliaP2P.Node closestNode  = getClosestNodes(requesterNode, target_guid,response);
        response.setClosestNode(closestNode);

        if (closestNode != null) {
            apiResponse = KademliaP2P.APIResponse.newBuilder().setResponseMessage(":) | FIND NODE SUCCESS").setResponseCode(200)
                .build();
        }
        else {
            apiResponse = KademliaP2P.APIResponse.newBuilder().setResponseMessage(":( | NODE IS INVALID").setResponseCode(400)
                    .build();
        }
        response.setDefaultResponse(apiResponse);

        //sending data back to the client
        responseObserver.onNext(response.build());

        // the grpc connection is closed
        responseObserver.onCompleted();
    }

    private KademliaP2P.Node getClosestNodes(KademliaP2P.Node requesterNode, int target_id, KademliaP2P.FindNodeResponse.Builder response) {

        int distance = Integer.MAX_VALUE;
        Map<Integer,KademliaP2P.Node> requester_hash_table = requesterNode.getClosestNodesMap();
        KademliaP2P.Node nearest = null;

        //foreach distance calcute node
        for(int i=0; i<K_bits; i++) {
            int kademlia_distance = 2^i;
            System.out.println("distanceArray["+i+"] = "+ kademlia_distance);

            // find nearest node
            KademliaP2P.Node bucket = requester_hash_table.get(kademlia_distance);
            int xorResult = myXOR(bucket.getGuid(),target_id);
            if (xorResult < distance){
                distance = xorResult;
                nearest = bucket;
            }
        }
        return nearest;
    }
}
