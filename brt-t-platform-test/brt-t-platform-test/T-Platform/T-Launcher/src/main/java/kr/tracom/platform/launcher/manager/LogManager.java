package kr.tracom.platform.launcher.manager;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LogManager {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(LogManager.class);
    private static String cachedLevel = "";

    public static void setLogLevel(String logLevel) {
        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.toLevel(logLevel));
    }

    public static void changeLevel() {
        String logLevel = "debug";

        Level level = Level.toLevel(logLevel.toUpperCase());  // default to Level.DEBUG
        if (cachedLevel.equalsIgnoreCase(level.levelStr)) {
            logger.debug("level: {} not changed", cachedLevel);
            return;
        }
        logger.info("level will change from: {} to: {}", cachedLevel, level.levelStr);
        cachedLevel = level.levelStr;
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggerList = loggerContext.getLoggerList();
        loggerList.stream().forEach(tmpLogger -> tmpLogger.setLevel(level));

        logger.debug("debug message");
        logger.info("info message");
        logger.error("error message");
    }
}
