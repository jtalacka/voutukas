package LoggerLogic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static String LOG_PATH = System.getProperty("user.dir") + "\\Logs";
    private static String LOG_EVENTS_FILENAME = "EventLogs.txt";
    private static String LOG_PAYLOADS_FILEMAME = "PayloadLogs.txt";

    static FileWriter eventsLogger;
    static FileWriter payloadLogger;
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static File eventLogs = new File(LOG_PATH + "\\" + LOG_EVENTS_FILENAME + ".txt");
    static File payloadLogs;

    public Logger() throws IOException {
        if(eventLogs.createNewFile())
        {
            eventsLogger = new FileWriter(eventLogs);
            eventsLogger.write(formatter.format(new Date()) + "\t" + "New file created \n");
        }
        else
        {
            eventsLogger = new FileWriter(eventLogs,true);
            eventsLogger.write(formatter.format(new Date()) + "\t" + "New session began \n");
        }
        eventsLogger.close();
    }

    public static void LogEvent(String event)
    {
        try {
            eventsLogger = new FileWriter(eventLogs,true);
            eventsLogger.write(formatter.format(new Date()) + "\t" + event + "\n");
            eventsLogger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void LogIncomingPayload(String payload)
    {
        payloadLogs = new File(LOG_PATH + "\\" + "incoming" +formatter.format(new Date()).replace(":","") + ".json");
        LogPayloads(payload);
    }

    public static void LogOutgoingPayload(String payload)
    {
        payloadLogs = new File(LOG_PATH + "\\" +"outgoing" +formatter.format(new Date()).replace(":","") + ".json");
        LogPayloads(payload);
    }

    private static void LogPayloads(String payload) {
        try {
            payloadLogger = new FileWriter(payloadLogs);
            payloadLogger.write(payload.replace(",",",\n"));
            payloadLogger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
