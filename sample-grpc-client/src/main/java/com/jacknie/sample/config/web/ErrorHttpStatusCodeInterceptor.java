package com.jacknie.sample.config.web;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.NestedServletException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class ErrorHttpStatusCodeInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    while (throwable instanceof NestedServletException servletException) {
      Throwable cause = servletException.getCause();
      throwable = cause != null ? cause : throwable;
    }
    findHttpStatusCode(throwable)
      .ifPresent(httpStatusCode -> request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, httpStatusCode));
    return true;
  }

  private Optional<Integer> findHttpStatusCode(Throwable throwable) {
    if (throwable instanceof StatusRuntimeException statusRuntimeException) {
      Status.Code statusCode = statusRuntimeException.getStatus().getCode();
      return Optional.of(switch (statusCode) {
        case NOT_FOUND -> HttpServletResponse.SC_NOT_FOUND;
        case INVALID_ARGUMENT -> HttpServletResponse.SC_BAD_REQUEST;
        default -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      });
    }
    return Optional.empty();
  }
}
