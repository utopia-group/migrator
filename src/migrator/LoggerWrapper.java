package migrator;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class LoggerWrapper {

    private final static boolean DEBUG_MODE = false;

    private static Logger instance = null;

    /**
     * Get the default singleton logger.
     *
     * @return the default singleton logger
     */
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = getStderrLogger();
            instance.setLevel(DEBUG_MODE ? Level.FINE : Level.INFO);
        }
        return instance;
    }

    /**
     * Get a new logger that logs in stderr for a class.
     *
     * @return a logger that logs in stderr
     */
    public static Logger getStderrLogger() {
        Logger logger = Logger.getGlobal();
        try {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(DEBUG_MODE ? Level.FINE : Level.INFO);
            consoleHandler.setFormatter(new CustomFormatter());
            logger.addHandler(consoleHandler);
            logger.setUseParentHandlers(false);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Logger error");
        }
        return logger;
    }

    /**
     * Get a new logger that logs in stdout for a class.
     *
     * @return a logger that logs in stdout
     */
    public static Logger getStdoutLogger() {
        Logger logger = Logger.getGlobal();
        try {
            StreamHandler streamHandler = new StreamHandler(System.out, new SimpleFormatter());
            streamHandler.setLevel(DEBUG_MODE ? Level.FINE : Level.INFO);
            streamHandler.setFormatter(new CustomFormatter());
            logger.addHandler(streamHandler);
            logger.setUseParentHandlers(false);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Logger error");
        }
        return logger;
    }

    /**
     * Get a new logger that logs in the file for a class.
     *
     * @return a logger that logs in the file
     */
    public static Logger getFileLogger() {
        String filepath = "out.log";
        Logger logger = Logger.getGlobal();
        try {
            FileHandler fileHandler = new FileHandler(filepath);
            fileHandler.setLevel(DEBUG_MODE ? Level.FINE : Level.INFO);
            fileHandler.setFormatter(new CustomFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Logger error");
        }
        return logger;
    }

    /**
     * Custom log formatter.
     */
    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(record.getLevel()).append(": ");
            buffer.append(record.getMessage());
            buffer.append(System.lineSeparator());
            return buffer.toString();
        }
    }
}
