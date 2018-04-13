package macaulish.net.core.socket;

import org.aeonbits.owner.Config;

@Config.Sources({ "file:./monitor.config"})
public interface MonitorConfig extends Config {

    @Key("broker.url")
    @DefaultValue("tcp://127.0.0.1:61613")
    String brokerURL();

    @Key("client.ID")
    @DefaultValue("mozi monitoring center")
    String clientID();

    @Key("topic.publish")
    @DefaultValue("MOZI/message")
    String topicPublish();

    @Key("topic.subscribe")
    @DefaultValue("MOZI/command")
    String topicSubscribe();

    @Key("session.clean")
    @DefaultValue("true")
    boolean cleanSession();

    @Key("mode.quiet")
    @DefaultValue("true")
    boolean quietMode();

    @Key("server.username")
    @DefaultValue("app")
    String username();

    @Key("server.password")
    @DefaultValue("app")
    String password();

    @Key("monitor.port")
    @DefaultValue("9192")
    int monitorPort();
}
