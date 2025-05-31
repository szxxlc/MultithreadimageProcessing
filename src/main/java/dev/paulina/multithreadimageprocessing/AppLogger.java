package dev.paulina.multithreadimageprocessing;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {

    private static final String LOG_FILE = "logs.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static synchronized void logInfo(String message) {
        log("INFO", message);
    }

    public static synchronized void logWarning(String message) {
        log("WARNING", message);
    }

    public static synchronized void logError(String message, Exception e) {
        log("ERROR", message + " Exception: " + (e != null ? e.getMessage() : "null"));
    }

    private static void log(String level, String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.printf("%s [%s]: %s%n", LocalDateTime.now().format(formatter), level, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
