package com.jacknie.sample.config.web;

import com.jacknie.sample.shared.Errors;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class GrpcStatusErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
    Map<String, Object> errorAttributes = new LinkedHashMap<>(super.getErrorAttributes(webRequest, options));
    Throwable throwable = getError(webRequest);
    if (throwable instanceof StatusRuntimeException statusRuntimeException) {
      applyStatusRuntimeException(errorAttributes, statusRuntimeException);
    }
    return Collections.unmodifiableMap(errorAttributes);
  }

  private void applyStatusRuntimeException(Map<String, Object> errorAttributes, StatusRuntimeException e) {
    if (e.getStatus().getCode().equals(Status.INVALID_ARGUMENT.getCode())) {
      Metadata trailers = e.getTrailers();
      if (trailers != null) {
        Errors errors = trailers.get(ProtoUtils.keyForProto(Errors.getDefaultInstance()));
        errorAttributes.put("errors", errors);
      }
    }
  }
}
