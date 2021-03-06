package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.sobey.dcmp.publicutil.stringutil.StringUtil;
import utils.DBUtils;

/**
 * Created by lijunhong on 17/2/15.
 */
public class MyTask extends TimerTask {

    private static Logger logger = Logger.getLogger(TimerTask.class);
    private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final long THREE_DAY = 24*3*60*60*1000L;             //三天的时间的毫秒值
    private final long ONE_HOUR = 60*60*1000L;                   //一个小时的毫秒值


    /**
     * 定时执行的任务
     *
     *
     */
    @Override
    public void run() {
        String dateStr = timeToCalculate(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date maxDate = simpleDateFormat.parse(dateStr);
            if(!StringUtil.isEmpty(dateStr)){
                Date minDate = getMinRequestTime(dateStr);
                System.out.println(minDate);
                if(minDate!=null){
                    remove(minDate,maxDate);
                }else {
                    if(logger.isInfoEnabled()) logger.info("["+dateStr+"]当前时间前三天没有查询到数据!");
                    return;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("程序执行失败:"+e);
        }


    }


    /**
     *  需求:获取系统当前时间的前三天的最小时间和前三天的最大时间,通过最小时间每次增加一个小时
     *      查询出表中的当前时间段内的minTaskId和maxTaskId,然后执行删除操作,根据minTaskId和
     *      maxTaskId删除AI3_TASk和AI3_TASKLOB表中的他们之间的数据
     *
     *  这里通过递归进行操作,一次增加一小时的毫秒值,并且判断当前最小值是否大于最大值,如果大于则使用最大值来
     *  进行查询操作
     *
     *  这里不在使用递归来进行操作,在java中递归数据量过大,会栈溢出,所以换成while
     * @param minDate 当前系统时间三天前最小的时间
     * @param maxDate 当前系统时间三天前最大的时间
     */
    @Deprecated
    public void recursiveDelete(Date minDate,Date maxDate){
        Map<String,Integer> taskIdMap = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        ///date为当前再早的时间,每次递归增加一个小时
        long addOneHour = minDate.getTime()+ONE_HOUR;
        if(addOneHour<maxDate.getTime()){
            String dateStr = simpleDateFormat.format(new Date(addOneHour));
            taskIdMap = getTaskIdByDateLimit(dateStr);
            deleteByMinTaskIdAndMaxTaskId(taskIdMap);
            recursiveDelete(new Date(addOneHour),maxDate);
        }else {
            String dateStr = simpleDateFormat.format(maxDate);
            taskIdMap = getTaskIdByDateLimit(dateStr);
            deleteByMinTaskIdAndMaxTaskId(taskIdMap);
            if(logger.isInfoEnabled()) logger.info("删除任务结束");
        }
    }

    /**
     * 这个方法是通过上面方法改造的,不在使用递归
     * @param minDate
     * @param maxDate
     */
    public void remove(Date minDate,Date maxDate){
        Map<String,Integer> taskIdMap = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        ///date为当前再早的时间,每次递归增加一个小时


        long addOneHour = minDate.getTime();
        while (true){
            addOneHour = addOneHour+ONE_HOUR;
            if(addOneHour<maxDate.getTime()){
                String dateStr = simpleDateFormat.format(new Date(addOneHour));
                taskIdMap = getTaskIdByDateLimit(dateStr);
                deleteByMinTaskIdAndMaxTaskId(taskIdMap);
            }else {
                String dateStr = simpleDateFormat.format(maxDate);
                taskIdMap = getTaskIdByDateLimit(dateStr);
                deleteByMinTaskIdAndMaxTaskId(taskIdMap);
                if(logger.isInfoEnabled()) logger.info("删除任务结束");
                return;
            }
        }

    }


    /**
     * 执行sql语句
     * select min(requesttime)  from ai3_task t where t.requesttime<to_date('2017-1-23 01:00:00','yyyy-mm-dd hh24:mi:ss');
     *
     * 这个语句找出的是当前 时间 前 最早的 时间
     * 需要传入当前执行时间的参数
     * @param newDateBeforThreeDay 这里的时间是当前时间向前 减去三天的时间
     * @return
     */
    public Date getMinRequestTime(String newDateBeforThreeDay) {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        Date date = null;
        //上面这个是针对oracle的
        String sql = "select min(requesttime) from ai3_task t where t.requesttime<to_date('"+newDateBeforThreeDay+"','yyyy-mm-dd hh24:mi:ss')";

        //下面这条是针对mysql的
//        String sql = "select min(requesttime) from ai3_task t where t.requesttime<str_to_date('"+newDateBeforThreeDay+"','%Y-%m-%d %T')";
        try {
            connection = DBUtils.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                String dateStr = rs.getString(1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
                if(!StringUtil.isEmpty(dateStr)){
                    date = simpleDateFormat.parse(dateStr);
                }else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            DBUtils.release(rs, statement, connection);
            if (logger.isInfoEnabled()) logger.info("[" + sql + "]sql执行完成"+date);
        }
        return date;
    }


    /**
     * 这里四根据时间查询出MinTaskID和MaxTaskID
     * @param dateStr 字符串时间 时间格式为 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public Map<String,Integer> getTaskIdByDateLimit(String dateStr){
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        Map<String,Integer> taskIdMap = null;
        //这条是针对oracle的
        String sql = "select min(taskid),max(taskid) from ai3_task t where t.requesttime<to_date('"+dateStr+"','yyyy-mm-dd hh24:mi:ss')";

        //下面这条是支持mysql的
//        String sql = "select min(taskid),max(taskid) from ai3_task t where t.requesttime<str_to_date('"+dateStr+"','%Y-%m-%d %T')";
        try {
            connection = DBUtils.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            if(rs.next()){
                int minTaskId = rs.getInt(1);
                int maxTaskId = rs.getInt(2);
                if(minTaskId<maxTaskId&&minTaskId!=0&&maxTaskId!=0){
                    taskIdMap = new HashMap<String,Integer>();
                    taskIdMap.put("minTaskId",minTaskId);
                    taskIdMap.put("maxTaskId",maxTaskId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("获取任MinTaskId和MaxTaskId失败");
        }finally {
            DBUtils.release(rs,statement,connection);
        }

        return taskIdMap;
    }


    /**
     *  通过MinTaskId和MaxTaskId删除数据
     * delete from ai3_tasklob where taskid>=147429008 and taskid<=147474759;
     * delete from ai3_task where taskid>=147429008 and taskid<=147474759;
     *
     * @param taskIdMap minTaskId maxTaskId
     */
    public void deleteByMinTaskIdAndMaxTaskId(Map<String,Integer> taskIdMap){
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        if(taskIdMap!=null&&taskIdMap.size()>=2) {

            String minTaskId = String.valueOf(taskIdMap.get("minTaskId"));
            String maxTaskId = String.valueOf(taskIdMap.get("maxTaskId"));
            String deleteAi3Tasklob_sql = "delete from ai3_tasklob where taskid>="+minTaskId+" and taskid<="+maxTaskId;
            String countAI3Tasklob_sql = "select count(*) from ai3_tasklob where taskid>="+minTaskId+" and taskid<="+maxTaskId;
            String deleteAi3Task_sql = "delete from ai3_task where taskid>="+minTaskId+" and taskid<="+maxTaskId;
            String countAI3_Task_sql = "select count(*) from ai3_task where taskid>="+minTaskId+" and taskid<="+maxTaskId;
            try {
                int countDelete = 0;
                int countQuery = 0;
                connection = DBUtils.getConnection();
                statement = connection.createStatement();

                //查询AI3_tasklob两个taskId之间的总数
                rs = statement.executeQuery(countAI3Tasklob_sql);
                if(rs.next())
                    countQuery = rs.getInt(1);
                if(logger.isInfoEnabled()) logger.info("查询AI3_tasklob表["+minTaskId+"到"+maxTaskId+"]之间的数据,共"+countQuery+"条");

                //删除Ai3_task两个taskId之间的数据
                countDelete = statement.executeUpdate(deleteAi3Tasklob_sql);


                if(logger.isInfoEnabled()) logger.info("删除AI3_tasklob表["+minTaskId+"到"+maxTaskId+"]之间的数据,共"+countDelete+"条");

                if(countQuery>countDelete&&logger.isInfoEnabled())
                    logger.warn("AI3_tasklob表["+minTaskId+"到"+maxTaskId+"],查询到"+countQuery+"条,删除"+countDelete+"条");


                //查询AI3_task两个taskId之间的总数
                rs = statement.executeQuery(countAI3_Task_sql);
                if(rs.next())
                    countQuery = rs.getInt(1);

                if(logger.isInfoEnabled()) logger.info("查询AI3_task表["+minTaskId+"到"+maxTaskId+"]之间的数据,共"+countQuery+"条");

                //删除AI3_task两个taskId之间的数据
                countDelete = statement.executeUpdate(deleteAi3Task_sql);

                if(logger.isInfoEnabled()) logger.info("删除AI3_task表taskID["+minTaskId+"到"+maxTaskId+"]之间的数据,共"+countDelete+"条");

                if(countQuery>countDelete&&logger.isInfoEnabled())
                    logger.warn("AI3_task表["+minTaskId+"到"+maxTaskId+"],查询到"+countQuery+"条,删除"+countDelete+"条");



            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("执行删除操作失败["+minTaskId+"到"+maxTaskId+"]");
            }finally {
                DBUtils.release(rs,statement,connection);
            }
        }
    }


    /**
     * 将传入进来的long的时间值减去三天的毫秒值,并且返回出经过格式化的String 时间格式
     * 得到当前时间前三天的时间的String
     * @return
     */
    public String timeToCalculate(long newTime){
        long newTimeBeforThreeTime = newTime-THREE_DAY;
        Date beforThreeDay = new Date(newTimeBeforThreeTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateStr = simpleDateFormat.format(beforThreeDay);
        return dateStr;
    }

}
