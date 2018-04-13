package macaulish.net.core.socket;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoziSocketMonitor extends AbstractSocketMonitor {

    private static Logger logger = LoggerFactory.getLogger(MoziSocketMonitor.class);
    /**
     * 指定绑定的端口，并监听此端口
     *
     * @param port 本地网络端口
     */
    public MoziSocketMonitor(int port) {
        super(port);
    }

    @Override
    protected JSONObject translate(String message) throws RuntimeException{
        //源message示例：861358039515234,01/22,00:09:36,3323.0064,12011.1166,0.41
        JSONObject json = new JSONObject();
        String[] datas = message.split(",");
        json.put("IMEI",datas[0]);
        String date = datas[1]+" "+datas[2];//格式：MM/dd HH:mm:ss
        json.put("date",date);
        json.put("latitude",Float.parseFloat(datas[3]));
        json.put("longitude",Float.parseFloat(datas[4]));
        json.put("speed",Float.parseFloat(datas[5]));
        return json;
    }

    @Override
    protected String translate(JSONObject message){
        return message.get("data").toString()+System.getProperty("line.separator");
    }
}
