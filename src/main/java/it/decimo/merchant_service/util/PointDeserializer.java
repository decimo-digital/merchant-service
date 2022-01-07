package it.decimo.merchant_service.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.SneakyThrows;
import org.springframework.data.geo.Point;

import java.io.IOException;

/**
 * Deserializzatore per il {@see Point}
 */
public class PointDeserializer extends JsonDeserializer<Point> {

    @Override
    @SneakyThrows
    public Point deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Double lat = null, lng = null;

        TreeNode tn = p.readValueAsTree();

        if (tn.get("x") != null) {
            lat = Double.parseDouble(tn.get("x").toString());
        } else {
            lat = Double.parseDouble(tn.get("lat").toString());
        }

        if (tn.get("y") != null) {
            lng = Double.parseDouble(tn.get("y").toString());
        } else {
            lng = Double.parseDouble(tn.get("lng").toString());
        }

        if (lat == null || lng == null) {
            throw new Exception("Failed to parse Point");
        }

        return new Point(lat, lng);
    }
}
