package main;

import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.Test;

import junit.framework.TestCase;
import utils.DBUtils;

/**
 * Created by lijunhong on 17/2/16.
 */
public class MyTaskTest extends TestCase {

    //测试数据库链接是否正常
    @Test
    public void test1(){
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = DBUtils.getConnection();
            statement = connection.createStatement();

            rs = statement.executeQuery("select min(requesttime)  from ai3_task t where t.requesttime<to_date('2016-09-07 18:29:47','yyyy-mm-dd hh24:mi:ss')");
            Date date = null;
            Time time = null;
            if(rs.next()){
                date = rs.getDate(1);
                time = rs.getTime(1);
                String string = rs.getString(1);
//                String string = "2016-08-17 14:24:36";
                System.out.println(string);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                java.util.Date d = simpleDateFormat.parse(string);
                System.out.println(d);


            }
//            rs = statement.executeQuery("select min(taskid),max(taskid) from ai3_task t where t.requesttime<to_date('2016-09-07 18:29:47','yyyy-mm-dd hh24:mi:ss')");
//            if(rs.next()){
//                System.out.println(rs.getInt(1));
//                System.out.println(rs.getInt(2));
//            }
            DBUtils.release(rs,statement,connection);
        } catch (SQLException e) {

            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * 执行第二条sql
     *
     * select min(taskid),max(taskid) from ai3_task t where t.requesttime<to_date('2016-09-07 18:29:47','yyyy-mm-dd hh24:mi:ss')
     *
     * 从最小的时间开始算起每个小时每个小时的增加查询
     */
    @Test
    public void test2(){

    }

    public Map<String,Integer> addTimeQuery(String dateStr){

        return null;
    }

    @Test
    public void test3(){
        long THREE_DAY = 24*3*60*60*1000;
        long newTime = System.currentTimeMillis();

        java.util.Date newDate = null;

        newDate = new java.util.Date(newTime);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newDateStr = simpleDateFormat.format(newDate);
        System.out.println(newDateStr);


        long aftertime = newTime-THREE_DAY;

        java.util.Date after = new java.util.Date(aftertime);

        String afterDateStr = simpleDateFormat.format(after);
        System.out.println(afterDateStr);
        


    }


    @Test
    public void test4(){
        MyTask myTask = new MyTask();
        java.util.Date date = new java.util.Date();
        System.out.println(date.getTime());
        java.util.Date date1 = new java.util.Date(date.getTime());
        System.out.println(date1);
    }

}