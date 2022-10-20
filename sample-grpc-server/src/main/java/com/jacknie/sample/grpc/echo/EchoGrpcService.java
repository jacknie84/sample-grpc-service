package com.jacknie.sample.grpc.echo;

import com.jacknie.sample.echo.EchoRequest;
import com.jacknie.sample.echo.EchoResponse;
import com.jacknie.sample.echo.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class EchoGrpcService extends EchoServiceGrpc.EchoServiceImplBase {

  @Override
  public void send(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
    EchoResponse response = EchoResponse.newBuilder()
      .setMessage(request.getMessage())
      .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
