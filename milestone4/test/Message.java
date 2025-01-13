package test;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(String msg) {
        this.asText = msg;
        this.data = msg.getBytes();
        
        // System.out.println("Creating Message from String: " + msg);

        double tempDouble;
        try {
            tempDouble = Double.parseDouble(msg);
            // System.out.println("Successfully converted String to Double: " + tempDouble);
        } catch (NumberFormatException e) {
            // System.out.println("Failed to convert String to Double: " + e.getMessage());
            tempDouble = Double.NaN;
        }
        
        this.asDouble = tempDouble;
        this.date = new Date();
        // System.out.println("Message created with date: " + this.date);
    }

    public Message(byte[] bytes) {
        this(new String(bytes));
        // System.out.println("Creating Message from byte array: " + new String(bytes));
    }

    public Message(double number) {
        this(String.valueOf(number));
        // System.out.println("Creating Message from double: " + number);
    }
}
