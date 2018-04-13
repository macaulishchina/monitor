package macaulish.net.core.socket;

import org.json.JSONObject;

public abstract class AbstractMessageHandler implements _MessageHandler {

    protected _HardWareMonitor mHardWareMonitor;

    public AbstractMessageHandler(_HardWareMonitor hardWareMonitor) {
        this.mHardWareMonitor = hardWareMonitor;
        hardWareMonitor.setMessageHandler(this);
    }

    public abstract void onAcceptMessage(JSONObject message);

//    /**
//     * 将String格式数据转换成JSON格式数据
//     * @param message String格式数据
//     * @return JSON格式数据
//     */
//    public abstract JSONObject translate(String message) throws Exception;
//
//    /**
//     * 将JSON格式数据转换成String格式数据
//     * @param message JSON格式数据
//     * @return String格式数据
//     */
//    public abstract String translate(JSONObject message);

    public void onDeliverMessage(JSONObject message) throws Exception {
        mHardWareMonitor.onDeliverMessage(message);
    }

    /**
     * 消息处理器开始工作调用
     */
    public abstract void work();
}
