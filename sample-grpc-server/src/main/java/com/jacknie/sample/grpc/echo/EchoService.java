package com.jacknie.sample.grpc.echo;

import com.jacknie.sample.echo.EchoRequest;
import com.jacknie.sample.echo.EchoResponse;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
public interface EchoService {
  EchoResponse send(@Valid EchoRequest request);
}
