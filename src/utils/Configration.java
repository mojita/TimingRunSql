package utils;

import java.io.File;
import java.io.FileInputStream;
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
    public static String logsPath;


    static {
        String allPath = Configration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int lastIndexOf = allPath.lastIndexOf(File.separator)+1;
        String path = allPath.substring(0,lastIndexOf);
        path = path+"config.properties";

//        InputStream in = Configration.class.getClassLoader().getResourceAsStream("Config.properties");
        try {
            InputStream in = new FileInputStream(path);
            Properties properties = new Properties();
            properties.load(in);
            user = properties.getProperty("user");
            password = properties.getProperty("password");
            url = properties.getProperty("url");
            time = properties.getProperty("time");
            sql = properties.getProperty("sql");
            className = properties.getProperty("className");
            logsPath = properties.getProperty("logsPath");
            System.out.println(user);
//            System.out.println(password);
            System.out.println(url);
            System.out.println(time);
//            System.out.println(sql);
            System.out.println(logsPath);

//            isCreateLogFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断是否创建日志文件,现在这个方法暂时弃用
     * @throws IOException
     */
    @Deprecated
    private static void isCreateLogFile() throws IOException {
        File file = null;

        file = new File(logsPath+"info.log");
        System.out.println(logsPath+":FIle");
        if(!file.exists()){
            file.createNewFile();
        }

        file = new File(logsPath+"error.log");
        if(!file.exists()){
            file.createNewFile();
        }

    }


}
