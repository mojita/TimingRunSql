package main;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.junit.Test;

/**
 * Created by lijunhong on 17/2/15.
 */
public class TimingTest{

    @Test
    public void test1() {
        Timer timer = new Timer();
        timer.schedule(new MyTask(), 1000, 2000);
        while (true) {
            try {
                int in = System.in.read();
                if (in == 's') {
                    timer.cancel();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void test2(){
//        Timer timer = new Timer();
        while (true){
            Date date = new Date();
            int hours = date.getHours();
            int min = date.getMinutes();
            if(22==hours&&34==min){
                System.out.println("大家好才是真的好");
            }
        }
    }

    @Test
    public void test(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR,0);

    }

}