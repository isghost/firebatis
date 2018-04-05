package assistant;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 * @author isghost
 * @date 2018/1/7.
 * @desc 获取B站相关信息
 */
public class BilibiliAssistant {

    final String BASE_URL = "http://www.bilibili.com/video/av";
    /**
     * 获得每个月份的最后一个视频Id
     */
    @Test
    public void getLastId() throws IOException, ParseException, InterruptedException {
        Date begin = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2009-07-01 00:00");
        for (int i = 56; i < 102; i++) {
            Date curDate = DateUtils.addMonths(begin, i);
            int aid = binarySearch(DateUtils.addSeconds(curDate, -1));
            System.out.println("time =" + curDate + ", " + aid);
    }
    }

    public int binarySearch(Date targetDate) throws IOException, ParseException, InterruptedException {
        int begin = 7;
        int end = 17961420;
        int resultId = 7;
        while(begin < end - 15){
            Integer mid = getNotNullAid(begin, end);
            if(mid == null){
                break;
            }
            Date publishDate = getPublishDate(mid);
            if(publishDate.compareTo(targetDate) > 0){
                end = mid - 1;
            }
            else{
                resultId = mid;
                begin = mid + 1;
            }
        }
        for (int i = end; i >= begin ; i--) {
            Date publishDate = getPublishDate(i);
            if(publishDate!= null){
                if(publishDate.compareTo(targetDate) <= 0){
                    resultId = i;
                    return i;
                }
            }
        }
        return resultId;
    }

    public Integer getNotNullAid(int begin, int end) throws IOException, InterruptedException {
        int mid = (begin + end) / 2;
        while(mid <= end){
            Thread.sleep(100);
            Document doc = Jsoup.connect(BASE_URL + mid).followRedirects(true).get();
            Element element = doc.selectFirst(".tminfo > time > i");

            if(element != null){
                return mid;
            }
            mid++;
        }
        return null;
    }

    public Date getPublishDate(int aid) throws IOException, ParseException, InterruptedException {
        Thread.sleep(100);
        Document doc = Jsoup.connect(BASE_URL + aid).followRedirects(true).get();

        Element element = doc.selectFirst(".tminfo > time > i");
        if(element == null){
            return null;
        }
        String timeStr = element.text();
        Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(timeStr);
//        System.out.println("url = " + BASE_URL + aid + "   ," + date);
        return date;
    }
}
