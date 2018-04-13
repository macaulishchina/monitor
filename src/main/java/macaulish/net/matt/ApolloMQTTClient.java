package macaulish.net.matt;

import macaulish.net.core.socket.MQTTMessageHandler;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApolloMQTTClient extends AbstractMQTTClient {

    private static Logger logger = LoggerFactory.getLogger(ApolloMQTTClient.class);

    private MQTTMessageHandler handler;

    /**
     * Constructs an instance of the sample client wrapper
     *
     * @param brokerUrl    the url to connect to
     * @param clientId     the client id to connect with
     * @param cleanSession clear state at end of connection or not (durable or non-durable subscriptions)
     * @param quietMode    whether debug should be printed to standard out
     * @param userName     the username to connect with
     * @param password     the password for the user
     */
    public ApolloMQTTClient(String brokerUrl, String clientId, boolean cleanSession, boolean quietMode, String userName, String password) throws MqttException {
        super(brokerUrl, clientId, cleanSession, quietMode, userName, password);
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.info("connection lost", cause);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        logger.info("Successfully delivery the message to " + token.getClient().getServerURI());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        logger.info("Successfully accept message of topic:" + topic + ",content:" + new String(message.getPayload()));
        try {
            JSONObject json = new JSONObject(new String(message.getPayload()));
            handler.onDeliverMessage(json);
        } catch (NullPointerException e) {
            logger.info("未设置消息处理对象，可以通过调用setMessageHandler()方法实现", e);
        } catch (Exception e) {
            logger.info("消息转发失败，可能时未成功建立Socket连接",e);
        }
    }

    public void setMessageHandler(MQTTMessageHandler handler) {
        this.handler = handler;
    }
}
