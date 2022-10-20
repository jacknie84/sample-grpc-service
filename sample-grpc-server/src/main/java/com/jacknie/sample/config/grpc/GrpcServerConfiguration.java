package com.jacknie.sample.config.grpc;

import io.grpc.ServerBuilder;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerConfiguration implements GrpcServerConfigurer {

  @Override
  public void accept(ServerBuilder<?> serverBuilder) {
    // do nothing...
  }
}
