package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import utils.Configration;
import utils.DBUtils;

/**
 * Created by lijunhong on 17/2/15.
 */
public class MyTask extends TimerTask {

    private static Logger logger = Logger.getLogger(TimerTask.class);

    /**
     * 定时执行的任务
     */
    @Override
    public void run() {
        runSQL();
    }


    /**
     * 执行sql语句
     */
    public void runSQL(){
        Connection connection = null;
        String sql = Configration.sql;
        try {
            connection = DBUtils.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            DBUtils.release(rs,statement,connection);
            if(logger.isInfoEnabled()) logger.info("["+sql+"]sql执行完成");
        } catch (SQLException e) {

            e.printStackTrace();
        }

    }
}
