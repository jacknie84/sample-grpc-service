package com.jacknie.sample.web.echo;

import com.jacknie.sample.echo.EchoRequest;
import com.jacknie.sample.echo.EchoResponse;
import com.jacknie.sample.echo.EchoServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/echo")
public class EchoController {

  @GrpcClient("echo-module")
  private EchoServiceGrpc.EchoServiceBlockingStub echoService;

  @PostMapping
  public ResponseEntity<?> echo(@RequestBody Echo dto) {
    EchoRequest request = EchoRequest.newBuilder()
      .setMessage(dto.getMessage())
      .build();
    EchoResponse response = echoService.send(request);
    return ResponseEntity.ok(response);
  }
}
