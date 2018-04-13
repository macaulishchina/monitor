package macaulish.net.tools.console;

import macaulish.net.core.socket.AbstractMessageHandler;
import macaulish.net.test.DefaultSocketMonitor;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleMessageHandler extends AbstractMessageHandler {

    public ConsoleMessageHandler(DefaultSocketMonitor defaultSocketMonitor) {
        super(defaultSocketMonitor);
    }

    @Override
    public void onAcceptMessage(JSONObject message) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        System.out.println(sdf.format(date)+" :"+message.toString());
    }

    public void work(){
        ((Thread)mHardWareMonitor).start();
    }
}
