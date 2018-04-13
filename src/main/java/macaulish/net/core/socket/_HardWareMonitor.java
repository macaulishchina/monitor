package macaulish.net.core.socket;

import org.json.JSONObject;

/**
 * 硬件采集端通信接口
 */
public interface _HardWareMonitor {

    /**
     * 设置数据处理对象
     * @param messageHandler 数据处理对象，负责监听器和上层应用之间的数据交换
     */
    void setMessageHandler(_MessageHandler messageHandler);

    /**
     * 当接受到数据采集端发送过来的信息时调用的方法
     * @param message JSON格式的数据
     * @param messageHandler 消息处理接口
     */
    void onAcceptMessage(JSONObject message, _MessageHandler messageHandler);

    /**
     * 当接受指令发送消息到数据采集端时调用的方法
     * @param message JSON格式的数据
     */
    void onDeliverMessage(JSONObject message) throws Exception;


}
