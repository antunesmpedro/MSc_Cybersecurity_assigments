package p2p;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class rpcClient {
    private static final Logger logger = Logger.getLogger(rpcClient.class.getName());

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    /** Construct client connecting to node at {@code host:port}. */
    public rpcClient(Inet4Address host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**Ping a node (and wait for his response?)**/
    public void ping(Contact sender, Inet4Address ip, BigInteger randomId) {
        try {
            logger.info("Will try to ping " + ip + " with randomId - " + randomId + " ... ");
            PingRequest request = PingRequest.newBuilder().setSender(sender).setRandomId(randomId).build();
            PingResponse response = blockingStub.Ping(request);
            BigInteger returnedId = response.getRandomId();
            if(returnedId.compareTo(randomId) != 0){
                logger.info("returnedId doesn't match randomId " + "\n" +
                        "returnedId: " + returnedId);
                throw new RuntimeException();
            }
            logger.info("Recieved ping response from: " + ip);

        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "RPC failed", e);
            return;
        }
    }
}
