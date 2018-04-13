/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 */

package macaulish.net.matt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A sample application that demonstrates how to use the Paho MQTT v3.1 Client API in
 * non-blocking waiter mode.
 * <p>
 * It can be run from the command line in one of two modes:
 * - as a publisher, sending a single message to a topic on the server
 * - as a subscriber, listening for messages from the server
 * <p>
 * There are three versions of the sample that implement the same features
 * but do so using using different programming styles:
 * <ol>
 * <li>Sample which uses the API which blocks until the operation completes</li>
 * <li>SampleAsyncWait (this one) shows how to use the asynchronous API with waiters that block until
 * an action completes</li>
 * <li>SampleAsyncCallBack shows how to use the asynchronous API where events are
 * used to notify the application when an action completes<li>
 * </ol>
 * <p>
 * If the application is run with the -h parameter then info is displayed that
 * describes all of the options / parameters.
 */

public abstract class AbstractMQTTClient implements MqttCallback {

    private static Logger logger = LoggerFactory.getLogger(AbstractMQTTClient.class);

    private ExecutorService executorService;// 创建一个线程池

    // Private instance variables
    private MqttAsyncClient client;
    private String brokerUrl;
    private boolean quietMode;
    private MqttConnectOptions conOpt;
    private boolean clean;
    private Throwable ex;
    private Object waiter = new Object();
    private boolean donext = false;
    private String password;
    private String username;


    /**
     * Constructs an instance of the sample client wrapper
     *
     * @param brokerUrl    the url to connect to
     * @param clientId     the client id to connect with
     * @param cleanSession clear state at end of connection or not (durable or non-durable subscriptions)
     * @param quietMode    whether debug should be printed to standard out
     * @param userName     the username to connect with
     * @param password     the password for the user
     * @throws MqttException
     */
    public AbstractMQTTClient(String brokerUrl, String clientId, boolean cleanSession,
                              boolean quietMode, String userName, String password) throws MqttException {
        this.brokerUrl = brokerUrl;
        this.quietMode = quietMode;
        this.clean = cleanSession;
        this.username = userName;
        this.password = password;
        MemoryPersistence dataStore = new MemoryPersistence();
        executorService = Executors.newCachedThreadPool();
        try {
            // Construct the connection options object that contains connection parameters
            // such as cleanSession and LWT
            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(clean);
            if (password != null) {
                conOpt.setPassword(this.password.toCharArray());
            }
            if (userName != null) {
                conOpt.setUserName(this.username);
            }

            // Construct a non-blocking MQTT client instance
            client = new MqttAsyncClient(this.brokerUrl, clientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(this);

        } catch (MqttException e) {
            e.printStackTrace();
            log("Unable to set up client: " + e.toString());
            System.exit(1);
        }
    }

    /**
     * Publish / send a message to an MQTT server
     *
     * @param topic   the name of the topic to publish to
     * @param qos     the quality of service to delivery the message at (0,1,2)
     * @param json    json to send to the MQTT server
     * @throws MqttException
     */
    public void publish(String topic, int qos, JSONObject json) {
        executorService.execute(new PublishThread(topic,qos,json.toString().getBytes()));
    }
    private class PublishThread extends Thread {
        private String topic;
        private int qos;
        private byte[] payload;

        public PublishThread(String topic, int qos, byte[] payload) {
            this.topic = topic;
            this.qos = qos;
            this.payload = payload;
        }

        public void run() {
            try {
                // Connect to the MQTT server
                // issue a non-blocking connect and then use the token to wait until the
                // connect completes. An exception is thrown if connect fails.
                log("Connecting to " + brokerUrl + " with client ID " + client.getClientId());
                IMqttToken conToken = client.connect(conOpt, null, null);
                conToken.waitForCompletion();
                log("Connected");
                String time = new Timestamp(System.currentTimeMillis()).toString();
                log("Publishing at: " + time + " to topic \"" + topic + "\" qos " + qos);

                // Construct the message to send
                MqttMessage message = new MqttMessage(payload);
                message.setQos(qos);

                // Send the message to the server, control is returned as soon
                // as the MQTT client has accepted to deliver the message.
                // Use the delivery token to wait until the message has been
                // delivered
                IMqttDeliveryToken pubToken = client.publish(topic, message, null, null);
                pubToken.waitForCompletion();
                log("Published");

                // Disconnect the client
                // Issue the disconnect and then use a token to wait until
                // the disconnect completes.
                log("Disconnecting");
                IMqttToken discToken = client.disconnect(null, null);
                discToken.waitForCompletion();
                log("Disconnected");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     *
     * @param topic to subscribe to (can be wild carded)
     * @param qos   the maximum quality of service to receive messages at for this subscription
     * @throws MqttException
     */
    public void subscribe(String topic, int qos){
        executorService.execute(new SubscribeThread(topic,qos));
    }

    private class SubscribeThread extends Thread {
        private String topic;
        private int qos;

        public SubscribeThread(String topic, int qos) {
            this.topic = topic;
            this.qos = qos;
        }

        public void run() {
            try {
                // Connect to the MQTT server
                // issue a non-blocking connect and then use the token to wait until the
                // connect completes. An exception is thrown if connect fails.
                log("Connecting to " + brokerUrl + " with client ID " + client.getClientId());
                IMqttToken conToken = client.connect(conOpt, null, null);
                conToken.waitForCompletion();
                log("Connected");
                // Subscribe to the requested topic.
                // Control is returned as soon client has accepted to deliver the subscription.
                // Use a token to wait until the subscription is in place.
                log("Subscribing to topic \"" + topic + "\" qos " + qos);

                IMqttToken subToken = client.subscribe(topic, qos, null, null);
                subToken.waitForCompletion();
                log("Subscribed to topic \"" + topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Utility method to handle logging. If 'quietMode' is set, this method does nothing
     *
     * @param message the message to log
     */
    private void log(String message) {
        if (!quietMode) {
            System.out.println(message);
        }
    }

    /****************************************************************/
    /* Methods to implement the MqttCallback interface              */
    /****************************************************************/

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public abstract void connectionLost(Throwable cause);

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public abstract void deliveryComplete(IMqttDeliveryToken token);

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public abstract void messageArrived(String topic, MqttMessage message);

    /****************************************************************/
    /* End of MqttCallback methods                                  */

    /****************************************************************/

    static void printHelp() {
        System.out.println(
                "Syntax:\n\n" +
                        "    SampleAsyncWait [-h] [-a publish|subscribe] [-t <topic>] [-m <message text>]\n" +
                        "            [-s 0|1|2] -b <hostname|IP address>] [-p <brokerport>] [-i <clientID>]\n\n" +
                        "    -h  Print this help text and quit\n" +
                        "    -q  Quiet mode (default is false)\n" +
                        "    -a  Perform the relevant action (default is publish)\n" +
                        "    -t  Publish/subscribe to <topic> instead of the default\n" +
                        "            (publish: \"Sample/Java/v3\", subscribe: \"Sample/#\")\n" +
                        "    -m  Use <message text> instead of the default\n" +
                        "            (\"Message from MQTTv3 Java client\")\n" +
                        "    -s  Use this QoS instead of the default (2)\n" +
                        "    -b  Use this name/IP address instead of the default (m2m.eclipse.org)\n" +
                        "    -p  Use this port instead of the default (1883)\n\n" +
                        "    -i  Use this client ID instead of SampleJavaV3_<action>\n" +
                        "    -c  Connect to the server with a clean session (default is false)\n" +
                        "     \n\n Security Options \n" +
                        "     -u Username \n" +
                        "     -z Password \n" +
                        "     \n\n SSL Options \n" +
                        "    -v  SSL enabled; true - (default is false) " +
                        "    -k  Use this JKS format key store to verify the client\n" +
                        "    -w  Passpharse to verify certificates in the keys store\n" +
                        "    -r  Use this JKS format keystore to verify the server\n" +
                        " If javax.net.ssl properties have been set only the -v flag needs to be set\n" +
                        "Delimit strings containing spaces with \"\"\n\n" +
                        "Publishers transmit a single message then disconnect from the server.\n" +
                        "Subscribers remain connected to the server and receive appropriate\n" +
                        "messages until <enter> is pressed.\n\n"
        );
    }

}
