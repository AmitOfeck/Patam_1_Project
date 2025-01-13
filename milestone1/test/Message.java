package test;

import java.util.Date;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(byte[] data) {
        this.data = data;
        this.asText = new String(data, StandardCharsets.UTF_8).trim();
        this.asDouble = tryParseDouble(this.asText);
        this.date = new Date();
    }

    public Message(String asText) {
        this.data = asText.getBytes(StandardCharsets.UTF_8);
        this.asText = asText;
        this.asDouble = tryParseDouble(asText);
        this.date = new Date();
    }

    public Message(double asDouble) {
        this.data = Double.toString(asDouble).getBytes(StandardCharsets.UTF_8);
        this.asText = Double.toString(asDouble);
        this.asDouble = asDouble;
        this.date = new Date();
    }

    private double tryParseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
