package com.jacknie.sample.config.web;

import com.google.protobuf.MessageOrBuilder;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
      .addInterceptor(new ErrorHttpStatusCodeInterceptor())
      .addPathPatterns("/error");
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
    return builder -> builder.serializerByType(MessageOrBuilder.class, new ProtobufMessageJsonSerializer());
  }

  @Bean
  public GrpcStatusErrorAttributes errorAttributes() {
    return new GrpcStatusErrorAttributes();
  }
}
