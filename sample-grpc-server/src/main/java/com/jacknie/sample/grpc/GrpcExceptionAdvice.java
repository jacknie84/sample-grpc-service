package com.jacknie.sample.grpc;

import com.google.protobuf.util.Values;
import com.jacknie.sample.shared.Errors;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

@GrpcAdvice
public class GrpcExceptionAdvice {

  @GrpcExceptionHandler(ConstraintViolationException.class)
  public StatusRuntimeException handleConstraintViolationException(ConstraintViolationException e) {
    String message = e.getMessage();
    String stackTrace = getStackTrace(e);
    Errors.Builder builder = Errors.newBuilder()
      .setMessage(message)
      .setException(e.getClass().getCanonicalName())
      .setStackTrace(stackTrace);
    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
    if (!CollectionUtils.isEmpty(violations)) {
      for (ConstraintViolation<?> violation : violations) {
        applyConstraintViolation(builder.addDetailsBuilder(), violation);
      }
    }
    Metadata trailers = new Metadata();
    Errors errors = builder.build();
    trailers.put(ProtoUtils.keyForProto(Errors.getDefaultInstance()), errors);
    return Status.INVALID_ARGUMENT
      .withCause(e)
      .withDescription(message)
      .asRuntimeException(trailers);
  }

  private String getStackTrace(ConstraintViolationException e) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    return stringWriter.toString();
  }

  private void applyConstraintViolation(Errors.Error.Builder builder, ConstraintViolation<?> violation) {
    builder
      .setCode(generateErrorCode(violation))
      .setMessage(violation.getMessage())
      .getAttributesBuilder()
      .putFields("targetType", Values.of(violation.getRootBeanClass().getCanonicalName()))
      .putFields("propertyPath", Values.of(violation.getPropertyPath().toString()));
  }

  private String generateErrorCode(ConstraintViolation<?> violation) {
    String targetType = violation.getRootBeanClass().getCanonicalName();
    StringBuilder builder = new StringBuilder(targetType)
      .append(".")
      .append(violation.getPropertyPath());
    ConstraintDescriptor<?> descriptor = violation.getConstraintDescriptor();
    if (descriptor instanceof ConstraintDescriptorImpl<?> descriptorImpl) {
      Class<?> annotationType = descriptorImpl.getAnnotationDescriptor().getType();
      builder
        .append(".")
        .append(annotationType.getSimpleName());
    }
    return builder.toString();
  }
}
