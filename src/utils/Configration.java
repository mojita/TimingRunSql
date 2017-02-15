package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by lijunhong on 17/2/15.
 */
public class Configration {

    public static String user;
    public static String password;
    public static String url;
    public static String time;
    public static String sql;
    public static String className;


    static {
        InputStream in = Configration.class.getClassLoader().getResourceAsStream("Config.properties");
        Properties properties = new Properties();
        try {
            properties.load(in);
            user = properties.getProperty("user");
            password = properties.getProperty("password");
            url = properties.getProperty("url");
            time = properties.getProperty("time");
            sql = properties.getProperty("sql");
            className = properties.getProperty("className");
            System.out.println(user);
            System.out.println(password);
            System.out.println(url);
            System.out.println(time);
            System.out.println(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
