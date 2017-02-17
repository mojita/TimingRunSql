package main;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.apache.log4j.Logger;

import com.sobey.dcmp.publicutil.stringutil.StringUtil;
import utils.Configration;

/**
 * Created by lijunhong on 17/2/15.
 */
public class Timing {

    private final long ADD_ONE_DAY = 24*60*60*1000l;     //这里是时间的间隔时间
    private String time = Configration.time;

    private static Logger logger = Logger.getLogger(Timing.class);


    public Timing(){
        if(!StringUtil.isEmpty(time)) {
            String[] hour_minute_second = time.split(":");
            if(hour_minute_second.length==3){
                int hour = Integer.parseInt(hour_minute_second[0]);
                int minute = Integer.parseInt(hour_minute_second[1]);
                int second = Integer.parseInt(hour_minute_second[2]);
                startTimer(hour,minute,second);
            }else {
                if(logger.isInfoEnabled()) logger.info("没有设置时间使用的是默认的22点执行");
                startTimer(22,0,0);
            }
        }else {
            if(logger.isInfoEnabled()) logger.info("没有设置时间使用的是默认的22点执行");
            startTimer(22,0,0);         //如果没有设置时间则默认是在22点执行
        }
    }

    /**
     * 时间的设置,根据时间执行任务
     */
    private void startTimer(int hour,int minute,int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,second);

        Date date = calendar.getTime();
        //这里是判断是否在时间之后执行如果是,则增加天数,让其在下一天进行执行
        if(date.before(new Date())){
            date = this.addOneDayDate(date,1);
        }

        //时间定时执行器
        Timer timer = new Timer();
        //执行的任务
        MyTask myTask = new MyTask();
        //根据指定的时间执行任务,间隔时间是一天
        timer.schedule(myTask,date,ADD_ONE_DAY);
    }

    /**
     * 增加天数
     * @param date
     * @param num
     * @return
     */
    public Date addOneDayDate(Date date,int num){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH,num);
        return calendar.getTime();
    }

}
