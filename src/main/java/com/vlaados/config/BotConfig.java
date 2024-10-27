package com.vlaados.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {

    private final String token;
    private final String ip;
    private final int port;
    private final String ipTip;
    private final int portTip;

    private final String url;


    public BotConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("");
            }
            properties.load(input);
            this.token = properties.getProperty("bot.token");
            this.ip = properties.getProperty("ai.ip");
            this.port = Integer.parseInt(properties.getProperty("ai.port"));
            this.ipTip = properties.getProperty("tip.ip");
            this.portTip = Integer.parseInt(properties.getProperty("tip.port"));
            this.url = properties.getProperty("api.url");



        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration", ex);
        }
    }

    public String getToken() {
        return token;
    }

    public String getIp() {
        return ip;
    }
    public int getPort() {
        return port;
    }

    public String getIpTip() {
        return ipTip;
    }
    public int getPortTip() {
        return portTip;
    }

    public String url() {
        return url;
    }

}