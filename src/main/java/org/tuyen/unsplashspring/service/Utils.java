package org.tuyen.unsplashspring.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
    public String getScheme() {
        Properties prop = new Properties();

        try (InputStream input = Utils.class.getClassLoader().getResourceAsStream("application.properties")) {
            prop.load(input);
            return prop.getProperty("api.base.scheme");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHost() {
        Properties prop = new Properties();

        try (InputStream input = Utils.class.getClassLoader().getResourceAsStream("application.properties")) {
            prop.load(input);
            return prop.getProperty("api.base.host");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
