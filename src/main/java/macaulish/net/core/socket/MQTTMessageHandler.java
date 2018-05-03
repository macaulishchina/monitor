package macaulish.net.core.socket;

import macaulish.net.matt.ApolloMQTTClient;
import org.aeonbits.owner.ConfigFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.slf4j.LoggerFactory.getLogger;

public class MQTTMessageHandler extends AbstractMessageHandler {

    private static Logger logger = getLogger(MQTTMessageHandler.class);


    /**
     * 默认MQTT服务器连接信息
     */
    private static String BROKER_URL = "tcp://www.macaulish.top:61613";
    private static String CLIENT_ID = "mozi monitoring center";
    private static String TOPIC_PUBLISH = "MOZI/message";
    private static String TOPIC_SUBSCRIBE = "MOZI/command";
    private static boolean CLEAN_SESSION = false;
    private static boolean QUIET_MODE = false;
    private static String USERNAME = "app";
    private static String PASSWORD = "app";
    private static int MONITOR_PORT = 9192;

    private ApolloMQTTClient mqttPublishClient;
    private ApolloMQTTClient mqttSubscribeClient;


    public MQTTMessageHandler(MonitorConfig config) {
        super(new MoziSocketMonitor(config.monitorPort()));
        config(config);
        try {
            mqttPublishClient = new ApolloMQTTClient(BROKER_URL, CLIENT_ID + " publisher", CLEAN_SESSION, QUIET_MODE, USERNAME, PASSWORD);
            mqttSubscribeClient = new ApolloMQTTClient(BROKER_URL, CLIENT_ID + " subscriber", CLEAN_SESSION, QUIET_MODE, USERNAME, PASSWORD);
            mqttSubscribeClient.subscribe(TOPIC_SUBSCRIBE, 1);
            mqttSubscribeClient.setMessageHandler(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void onAcceptMessage(JSONObject message) {
        try {
            logger.info("MQTTMessageHandler accepted the message from " + message.get("IP") + ":" + message.get("port") + ".");
            logger.info("Message content is '"+message+"'");
            mqttPublishClient.publish(TOPIC_PUBLISH, 1, message);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    /**
     * 更新服务器设置
     */
    public void config(MonitorConfig config) {
        BROKER_URL = config.brokerURL();
        CLIENT_ID = config.clientID();
        TOPIC_PUBLISH = config.topicPublish();
        TOPIC_SUBSCRIBE = config.topicSubscribe();
        CLEAN_SESSION = config.cleanSession();
        QUIET_MODE = config.quietMode();
        USERNAME = config.username();
        PASSWORD = config.password();
        MONITOR_PORT = config.monitorPort();
    }


    @Override
    public void work() {
        logger.info("MQTT message handler is working!");
        logger.info("\nMQTT server url:"+BROKER_URL+"\n" +
                "Client ID:"+CLIENT_ID+"*\n" +
                "Publish topic:"+TOPIC_PUBLISH+"\n" +
                "Subscribe topic:"+TOPIC_SUBSCRIBE+"\n" +
                "Quite mode:"+QUIET_MODE+"\n" +
                "Clean session:"+CLEAN_SESSION+"\n" +
                "Local monitor port:"+MONITOR_PORT+"\n");
        ((Thread) mHardWareMonitor).start();
        logger.info("*********monitor local port "+MONITOR_PORT+"*********");
        logger.info("*********wait for message from MQTT server of topic "+TOPIC_SUBSCRIBE+"*********");
    }

    public static void main(String args[]) {
        logger.info("开始墨子号开发板监控程序 designed by macaulish 默认监听端口号：9192");
        MQTTMessageHandler handler = new MQTTMessageHandler(ConfigFactory.create(MonitorConfig.class));
        handler.work();
    }


    public static String getBrokerUrl() {
        return BROKER_URL;
    }

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getTopicPublish() {
        return TOPIC_PUBLISH;
    }

    public static String getTopicSubscribe() {
        return TOPIC_SUBSCRIBE;
    }

    public static boolean isCleanSession() {
        return CLEAN_SESSION;
    }

    public static boolean isQuietMode() {
        return QUIET_MODE;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static int getMonitorPort() {
        return MONITOR_PORT;
    }

}
