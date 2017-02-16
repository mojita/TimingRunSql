package main;

import static utils.Configration.logsPath;

/**
 * Created by lijunhong on 17/2/15.
 */
public class Application {

    static {
        //设置系统变量日志输出路径
        System.setProperty("LOG_PATH",logsPath);
    }

    public static void main(String[] args){
        new Timing();
    }
}
