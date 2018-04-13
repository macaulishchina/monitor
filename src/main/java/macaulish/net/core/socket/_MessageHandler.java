package macaulish.net.core.socket;

import org.json.JSONObject;

/**
 * 消息处理接口
 */
public interface _MessageHandler {

    /**
     * 接受到数据采集端发送的数据时调用的方法
     * @param message 数据采集端发送过来的数据，格式为JSON，其中除了原始数据外，还包含远端的IP和端口
     */
    void onAcceptMessage(JSONObject message);

    /**
     * 发送数据到数据采集端时调用的方法
     * @param message JSON格式的数据
     */
    void onDeliverMessage(JSONObject message) throws Exception;
}
