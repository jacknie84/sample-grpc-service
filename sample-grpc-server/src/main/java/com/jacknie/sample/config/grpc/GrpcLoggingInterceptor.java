package com.jacknie.sample.config.grpc;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.jacknie.sample.shared.Errors;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

@Slf4j
@Setter
public class GrpcLoggingInterceptor implements ServerInterceptor {

  /**
   * 로그 레벨
   */
  @Nullable
  private LoggingLevel loggingLevel;

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    log.info("call {}, attribute {}", call.getMethodDescriptor().getFullMethodName(), call.getAttributes());
    LoggingLevel loggingLevel = this.loggingLevel == null ? LoggingLevel.SIMPLE : this.loggingLevel;
    if (loggingLevel.equals(LoggingLevel.DETAIL)) {
      LoggingServerCall<ReqT, RespT> loggingServerCall = new LoggingServerCall<>(call);
      ServerCall.Listener<ReqT> listener = next.startCall(loggingServerCall, headers);
      return new LoggingServerCallListener<>(listener);
    } else {
      return next.startCall(call, headers);
    }
  }

  public enum LoggingLevel {
    SIMPLE, DETAIL
  }

  private static class LoggingServerCallListener<ReqT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

    private final JsonFormat.Printer printer = JsonFormat.printer().includingDefaultValueFields();

    protected LoggingServerCallListener(ServerCall.Listener<ReqT> delegate) {
      super(delegate);
    }

    @SneakyThrows
    @Override
    public void onMessage(ReqT message) {
      if (message instanceof MessageOrBuilder request) {
        log.info("request: {}", printer.print(request));
      }
      super.onMessage(message);
    }
  }

  private static class LoggingServerCall<ReqT, RespT> extends ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> {

    private final JsonFormat.Printer printer = JsonFormat.printer().includingDefaultValueFields();

    protected LoggingServerCall(ServerCall<ReqT, RespT> delegate) {
      super(delegate);
    }

    @SneakyThrows
    @Override
    public void sendMessage(RespT message) {
      if (message instanceof MessageOrBuilder response) {
        log.info("response: {}", printer.print(response));
      }
      super.sendMessage(message);
    }

    @SneakyThrows
    @Override
    public void close(Status status, Metadata trailers) {
      if (!status.isOk()) {
        Metadata.Key<Errors> key = ProtoUtils.keyForProto(Errors.getDefaultInstance());
        Errors errors = trailers.get(key);
        if (errors != null) {
          log.info("error: {}", printer.print(errors));
        }
      }
      super.close(status, trailers);
    }
  }
}
