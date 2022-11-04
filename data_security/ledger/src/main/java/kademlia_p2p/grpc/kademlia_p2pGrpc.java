package kademlia_p2p.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: kademlia_p2p.proto")
public final class kademlia_p2pGrpc {

  private kademlia_p2pGrpc() {}

  public static final String SERVICE_NAME = "kademlia_p2p";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.NodeRequest,
      kademlia_p2p.grpc.KademliaP2P.APIResponse> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ping",
      requestType = kademlia_p2p.grpc.KademliaP2P.NodeRequest.class,
      responseType = kademlia_p2p.grpc.KademliaP2P.APIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.NodeRequest,
      kademlia_p2p.grpc.KademliaP2P.APIResponse> getPingMethod() {
    io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.NodeRequest, kademlia_p2p.grpc.KademliaP2P.APIResponse> getPingMethod;
    if ((getPingMethod = kademlia_p2pGrpc.getPingMethod) == null) {
      synchronized (kademlia_p2pGrpc.class) {
        if ((getPingMethod = kademlia_p2pGrpc.getPingMethod) == null) {
          kademlia_p2pGrpc.getPingMethod = getPingMethod = 
              io.grpc.MethodDescriptor.<kademlia_p2p.grpc.KademliaP2P.NodeRequest, kademlia_p2p.grpc.KademliaP2P.APIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "kademlia_p2p", "ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kademlia_p2p.grpc.KademliaP2P.NodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kademlia_p2p.grpc.KademliaP2P.APIResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new kademlia_p2pMethodDescriptorSupplier("ping"))
                  .build();
          }
        }
     }
     return getPingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.NodeRequest,
      kademlia_p2p.grpc.KademliaP2P.StoreResponse> getStoreMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "store",
      requestType = kademlia_p2p.grpc.KademliaP2P.NodeRequest.class,
      responseType = kademlia_p2p.grpc.KademliaP2P.StoreResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.NodeRequest,
      kademlia_p2p.grpc.KademliaP2P.StoreResponse> getStoreMethod() {
    io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.NodeRequest, kademlia_p2p.grpc.KademliaP2P.StoreResponse> getStoreMethod;
    if ((getStoreMethod = kademlia_p2pGrpc.getStoreMethod) == null) {
      synchronized (kademlia_p2pGrpc.class) {
        if ((getStoreMethod = kademlia_p2pGrpc.getStoreMethod) == null) {
          kademlia_p2pGrpc.getStoreMethod = getStoreMethod = 
              io.grpc.MethodDescriptor.<kademlia_p2p.grpc.KademliaP2P.NodeRequest, kademlia_p2p.grpc.KademliaP2P.StoreResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "kademlia_p2p", "store"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kademlia_p2p.grpc.KademliaP2P.NodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kademlia_p2p.grpc.KademliaP2P.StoreResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new kademlia_p2pMethodDescriptorSupplier("store"))
                  .build();
          }
        }
     }
     return getStoreMethod;
  }

  private static volatile io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.FindNodeRequest,
      kademlia_p2p.grpc.KademliaP2P.FindNodeResponse> getFindNodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "findNode",
      requestType = kademlia_p2p.grpc.KademliaP2P.FindNodeRequest.class,
      responseType = kademlia_p2p.grpc.KademliaP2P.FindNodeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.FindNodeRequest,
      kademlia_p2p.grpc.KademliaP2P.FindNodeResponse> getFindNodeMethod() {
    io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.FindNodeRequest, kademlia_p2p.grpc.KademliaP2P.FindNodeResponse> getFindNodeMethod;
    if ((getFindNodeMethod = kademlia_p2pGrpc.getFindNodeMethod) == null) {
      synchronized (kademlia_p2pGrpc.class) {
        if ((getFindNodeMethod = kademlia_p2pGrpc.getFindNodeMethod) == null) {
          kademlia_p2pGrpc.getFindNodeMethod = getFindNodeMethod = 
              io.grpc.MethodDescriptor.<kademlia_p2p.grpc.KademliaP2P.FindNodeRequest, kademlia_p2p.grpc.KademliaP2P.FindNodeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "kademlia_p2p", "findNode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kademlia_p2p.grpc.KademliaP2P.FindNodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kademlia_p2p.grpc.KademliaP2P.FindNodeResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new kademlia_p2pMethodDescriptorSupplier("findNode"))
                  .build();
          }
        }
     }
     return getFindNodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.NodeRequest,
      kademlia_p2p.grpc.KademliaP2P.APIResponse> getFindValueMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "find_value",
      requestType = kademlia_p2p.grpc.KademliaP2P.NodeRequest.class,
      responseType = kademlia_p2p.grpc.KademliaP2P.APIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.NodeRequest,
      kademlia_p2p.grpc.KademliaP2P.APIResponse> getFindValueMethod() {
    io.grpc.MethodDescriptor<kademlia_p2p.grpc.KademliaP2P.NodeRequest, kademlia_p2p.grpc.KademliaP2P.APIResponse> getFindValueMethod;
    if ((getFindValueMethod = kademlia_p2pGrpc.getFindValueMethod) == null) {
      synchronized (kademlia_p2pGrpc.class) {
        if ((getFindValueMethod = kademlia_p2pGrpc.getFindValueMethod) == null) {
          kademlia_p2pGrpc.getFindValueMethod = getFindValueMethod = 
              io.grpc.MethodDescriptor.<kademlia_p2p.grpc.KademliaP2P.NodeRequest, kademlia_p2p.grpc.KademliaP2P.APIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "kademlia_p2p", "find_value"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kademlia_p2p.grpc.KademliaP2P.NodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kademlia_p2p.grpc.KademliaP2P.APIResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new kademlia_p2pMethodDescriptorSupplier("find_value"))
                  .build();
          }
        }
     }
     return getFindValueMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static kademlia_p2pStub newStub(io.grpc.Channel channel) {
    return new kademlia_p2pStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static kademlia_p2pBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new kademlia_p2pBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static kademlia_p2pFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new kademlia_p2pFutureStub(channel);
  }

  /**
   */
  public static abstract class kademlia_p2pImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *rpc join(JoinRequest) returns(APIResponse);
     * </pre>
     */
    public void ping(kademlia_p2p.grpc.KademliaP2P.NodeRequest request,
                     io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.APIResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    /**
     */
    public void store(kademlia_p2p.grpc.KademliaP2P.NodeRequest request,
                      io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.StoreResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getStoreMethod(), responseObserver);
    }

    /**
     */
    public void findNode(kademlia_p2p.grpc.KademliaP2P.FindNodeRequest request,
                         io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.FindNodeResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getFindNodeMethod(), responseObserver);
    }

    /**
     */
    public void findValue(kademlia_p2p.grpc.KademliaP2P.NodeRequest request,
                          io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.APIResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getFindValueMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPingMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kademlia_p2p.grpc.KademliaP2P.NodeRequest,
                kademlia_p2p.grpc.KademliaP2P.APIResponse>(
                  this, METHODID_PING)))
          .addMethod(
            getStoreMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kademlia_p2p.grpc.KademliaP2P.NodeRequest,
                kademlia_p2p.grpc.KademliaP2P.StoreResponse>(
                  this, METHODID_STORE)))
          .addMethod(
            getFindNodeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kademlia_p2p.grpc.KademliaP2P.FindNodeRequest,
                kademlia_p2p.grpc.KademliaP2P.FindNodeResponse>(
                  this, METHODID_FIND_NODE)))
          .addMethod(
            getFindValueMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kademlia_p2p.grpc.KademliaP2P.NodeRequest,
                kademlia_p2p.grpc.KademliaP2P.APIResponse>(
                  this, METHODID_FIND_VALUE)))
          .build();
    }
  }

  /**
   */
  public static final class kademlia_p2pStub extends io.grpc.stub.AbstractStub<kademlia_p2pStub> {
    private kademlia_p2pStub(io.grpc.Channel channel) {
      super(channel);
    }

    private kademlia_p2pStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected kademlia_p2pStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new kademlia_p2pStub(channel, callOptions);
    }

    /**
     * <pre>
     *rpc join(JoinRequest) returns(APIResponse);
     * </pre>
     */
    public void ping(kademlia_p2p.grpc.KademliaP2P.NodeRequest request,
                     io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.APIResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void store(kademlia_p2p.grpc.KademliaP2P.NodeRequest request,
                      io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.StoreResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getStoreMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void findNode(kademlia_p2p.grpc.KademliaP2P.FindNodeRequest request,
                         io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.FindNodeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFindNodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void findValue(kademlia_p2p.grpc.KademliaP2P.NodeRequest request,
                          io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.APIResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFindValueMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class kademlia_p2pBlockingStub extends io.grpc.stub.AbstractStub<kademlia_p2pBlockingStub> {
    private kademlia_p2pBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private kademlia_p2pBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected kademlia_p2pBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new kademlia_p2pBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *rpc join(JoinRequest) returns(APIResponse);
     * </pre>
     */
    public kademlia_p2p.grpc.KademliaP2P.APIResponse ping(kademlia_p2p.grpc.KademliaP2P.NodeRequest request) {
      return blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }

    /**
     */
    public kademlia_p2p.grpc.KademliaP2P.StoreResponse store(kademlia_p2p.grpc.KademliaP2P.NodeRequest request) {
      return blockingUnaryCall(
          getChannel(), getStoreMethod(), getCallOptions(), request);
    }

    /**
     */
    public kademlia_p2p.grpc.KademliaP2P.FindNodeResponse findNode(kademlia_p2p.grpc.KademliaP2P.FindNodeRequest request) {
      return blockingUnaryCall(
          getChannel(), getFindNodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public kademlia_p2p.grpc.KademliaP2P.APIResponse findValue(kademlia_p2p.grpc.KademliaP2P.NodeRequest request) {
      return blockingUnaryCall(
          getChannel(), getFindValueMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class kademlia_p2pFutureStub extends io.grpc.stub.AbstractStub<kademlia_p2pFutureStub> {
    private kademlia_p2pFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private kademlia_p2pFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected kademlia_p2pFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new kademlia_p2pFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *rpc join(JoinRequest) returns(APIResponse);
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<kademlia_p2p.grpc.KademliaP2P.APIResponse> ping(
        kademlia_p2p.grpc.KademliaP2P.NodeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<kademlia_p2p.grpc.KademliaP2P.StoreResponse> store(
        kademlia_p2p.grpc.KademliaP2P.NodeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getStoreMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<kademlia_p2p.grpc.KademliaP2P.FindNodeResponse> findNode(
        kademlia_p2p.grpc.KademliaP2P.FindNodeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFindNodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<kademlia_p2p.grpc.KademliaP2P.APIResponse> findValue(
        kademlia_p2p.grpc.KademliaP2P.NodeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFindValueMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PING = 0;
  private static final int METHODID_STORE = 1;
  private static final int METHODID_FIND_NODE = 2;
  private static final int METHODID_FIND_VALUE = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final kademlia_p2pImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(kademlia_p2pImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PING:
          serviceImpl.ping((kademlia_p2p.grpc.KademliaP2P.NodeRequest) request,
              (io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.APIResponse>) responseObserver);
          break;
        case METHODID_STORE:
          serviceImpl.store((kademlia_p2p.grpc.KademliaP2P.NodeRequest) request,
              (io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.StoreResponse>) responseObserver);
          break;
        case METHODID_FIND_NODE:
          serviceImpl.findNode((kademlia_p2p.grpc.KademliaP2P.FindNodeRequest) request,
              (io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.FindNodeResponse>) responseObserver);
          break;
        case METHODID_FIND_VALUE:
          serviceImpl.findValue((kademlia_p2p.grpc.KademliaP2P.NodeRequest) request,
              (io.grpc.stub.StreamObserver<kademlia_p2p.grpc.KademliaP2P.APIResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class kademlia_p2pBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    kademlia_p2pBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return kademlia_p2p.grpc.KademliaP2P.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("kademlia_p2p");
    }
  }

  private static final class kademlia_p2pFileDescriptorSupplier
      extends kademlia_p2pBaseDescriptorSupplier {
    kademlia_p2pFileDescriptorSupplier() {}
  }

  private static final class kademlia_p2pMethodDescriptorSupplier
      extends kademlia_p2pBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    kademlia_p2pMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (kademlia_p2pGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new kademlia_p2pFileDescriptorSupplier())
              .addMethod(getPingMethod())
              .addMethod(getStoreMethod())
              .addMethod(getFindNodeMethod())
              .addMethod(getFindValueMethod())
              .build();
        }
      }
    }
    return result;
  }
}
