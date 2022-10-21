package com.jacknie.sample.grpc.echo;

import com.jacknie.sample.echo.EchoRequest;
import com.jacknie.sample.echo.EchoResponse;
import com.jacknie.sample.echo.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class EchoGrpcService extends EchoServiceGrpc.EchoServiceImplBase {

  private final EchoService echoService;

  @Override
  public void send(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
    EchoResponse response = echoService.send(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
