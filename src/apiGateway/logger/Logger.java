package apiGateway.logger;

import java.util.logging.Level;

public class Logger {
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("ProxyLogger");
    private static long startTime;
    private static long endTime;



    public static void warn(String message){
        logger.log(Level.WARNING, message);
    }

    public static void info(String message){
        logger.log(Level.INFO, message);
    }

    public static void error(String message){
        logger.log(Level.SEVERE, message);
    }

    public static void startTimer(){
        startTime = System.nanoTime();
    }

    public static void stopTimer(){
        endTime = System.nanoTime();
    }

    public static String getTimerValue(){
        return String.format("%.3f",((double)endTime - startTime) / 1_000_000_00);
    }

}
