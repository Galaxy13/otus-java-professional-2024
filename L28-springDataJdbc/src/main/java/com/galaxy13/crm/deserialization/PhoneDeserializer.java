package com.galaxy13.crm.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.galaxy13.crm.model.Phone;

import java.io.IOException;

public class PhoneDeserializer extends StdDeserializer<Phone> {
    public PhoneDeserializer() {
        this(null);
    }

    public PhoneDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Phone deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node == null) {
            return null;
        }
        String number = node.asText();
        return new Phone(null, number);
    }
}
