package com.jacknie.sample.grpc.echo;

import com.jacknie.sample.echo.EchoRequest;
import com.jacknie.sample.echo.EchoResponse;
import org.springframework.stereotype.Service;

@Service
public class EchoServiceImpl implements EchoService {

  @Override
  public EchoResponse send(EchoRequest request) {
    return EchoResponse.newBuilder()
      .setMessage(request.getMessage())
      .build();
  }
}
