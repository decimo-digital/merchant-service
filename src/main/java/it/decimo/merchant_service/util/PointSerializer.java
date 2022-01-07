package it.decimo.merchant_service.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.geo.Point;

import java.io.IOException;
import java.util.HashMap;

/**
 * Serializzatore per il {@see Point}
 */
public class PointSerializer extends JsonSerializer<Point> {

    @Override
    public void serialize(Point value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        final var lat = value.getX();
        final var lng = value.getY();
        final var mappedValue = new HashMap<String, Double>() {
            {
                put("lat", lat);
                put("lng", lng);
            }
        };
        gen.writeString(new ObjectMapper().writeValueAsString(mappedValue));
    }
}
