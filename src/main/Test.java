package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import utils.DBUtils;

import static utils.Configration.logsPath;

/**
 * Created by lijunhong on 17/2/17.
 */
public class Test {
    //测试定时器
    @org.junit.Test
    public void timingTest(){
        System.setProperty("LOG_PATH",logsPath);
        new Timing();
    }


    @org.junit.Test
    public void test(){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtils.getConnection();
            statement = connection.createStatement();
            int c = statement.executeUpdate("DELETE FROM AI3_task WHERE id>=8 AND id<=12");
            
            while (resultSet.next()){
                System.out.println(resultSet.getInt("id"));
                System.out.println(resultSet.getString("name"));
            }
            
//            System.out.println(i);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //-------------------------------------------------------

    private final long MONTH_DAY = 200*24*60*60*1000l;    //一个月的时间
    private final long HOUR_6 = 12*60*60*1000l;           //12小时
    /****
     *
     * 接下来为测试方法:
     *      测试之前准备数据,表结构
     *      批量添加数据入库
     *      时间和id都是随之增长的,时间需要每隔
     *
     *      首先构造模拟数据:
     *
     *      date的起始时间提前一个月
     *      1.每隔三秒在date字段中插入的时候增加一天,同时每次插入的时候,date的时间都会增加10秒
     *
     *      测试:
     *          就按照现场的需求进行测试不做任何变化
     *          表:ai3_tasklob ai3_task
     *          表结构简化:只有时间和id
     *
     * **/
    @org.junit.Test
    public void addData(){
//        addDateTask(new Date().getTime());
        addrule();
    }

    /**
     * 添加时间的方法
     * @param time
     */
    private void addDateTask(long time) {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = simpleDateFormat.format(new Date(time));
        System.out.println(dateStr);
        String task_sql = "insert into ai3_task (REQUESTTIME) value('"+dateStr+"')";
        String tasklob_sql = "insert into ai3_tasklob (REQUESTTIME) value('"+dateStr+"')";
        try {
            connection = DBUtils.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(task_sql);
            statement.executeUpdate(tasklob_sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtils.release(rs,statement,connection);
        }
    }



    private void addrule(){
        long newtime = new Date().getTime();//得到当前时间
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(newtime))+":newtime");

        long beforNewTime = newtime - MONTH_DAY;//一个月前的时间

        System.out.println("newtime:"+newtime);
        System.out.println("Month::"+MONTH_DAY);
        System.out.println("befor"+beforNewTime);


        Random random = new Random();
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(beforNewTime))+":beforNewTime");


        System.out.println();

        while (true){
            int addtime = random.nextInt(5*60*1000)+1;//30分钟
            beforNewTime = beforNewTime+addtime;
            System.out.println("addtime"+addtime);
            System.out.println("befo"+beforNewTime);
            addDateTask(beforNewTime);

        }



    }


}
