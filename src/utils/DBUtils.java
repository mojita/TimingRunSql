package utils;

import java.sql.*;

import org.apache.log4j.Logger;

/**
 * Created by lijunhong on 17/2/15.
 */
public class DBUtils {
    private final static Logger logger = Logger.getLogger(DBUtils.class);
    static {
        try {
            Class.forName(Configration.className);
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("数据库驱动读取失败"+e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Configration.url,Configration.user,Configration.password);
    }


    public static void release(ResultSet rs, Statement stat, Connection conn){
        if(rs!=null){
            try{
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            rs = null;
        }

        if(stat!=null){
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stat = null;
        }

        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }
}
