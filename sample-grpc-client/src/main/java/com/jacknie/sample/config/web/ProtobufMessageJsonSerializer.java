package com.jacknie.sample.config.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;

public class ProtobufMessageJsonSerializer extends JsonSerializer<MessageOrBuilder> {

  private final JsonFormat.Printer printer = JsonFormat.printer().includingDefaultValueFields();

  @Override
  public void serialize(MessageOrBuilder value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeRawValue(printer.print(value));
  }
}
