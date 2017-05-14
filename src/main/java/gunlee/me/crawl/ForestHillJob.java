package gunlee.me.crawl;

import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import retrofit2.Response;

import java.io.IOException;

/**
 * @author Gun Lee (gunlee01@gmail.com) on 2017. 5. 13.
 */
@Component
@Slf4j
public class ForestHillJob {
    public static final String TOKEN_LINE_SCOUTER = "Wd8jVkD5Fzh7CMl1CTmyOo9ILtWq1MoknQ7kbTMMjQmdU6+cDmfqkwwuE5mB5rLQcFeWCjvjJgnE/MmqT6D+gEsO68vKQh11YygUT7dQmh1JwmWG5mbRqk98Xo1+aBWHllG0AL/6xAp7YMtG9MDVPwdB04t89/1O/w1cDnyilFU=";
    RestTemplate rt = new RestTemplate();
    String url = "http://www.foresthill.kr/html/reserve/reserve01_02.asp";
    long last = 0L;
    long sleep = 300*1000;

    @Scheduled(fixedRate = 10000)
    public void crawlForReservation() {
        log.info("scheduled!");

        if(last+sleep > System.currentTimeMillis()) {
            return;
        }

        checkAndLineMe();
    }

    public void checkAndLineMe() {
        String[] patterns = {
                "'1', 'Rock','07",
                "'1', 'Rock','08",
                "'2', 'Hill','07",
                "'2', 'Hill','08"
        };

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("book_date", "20170520");
        map.add("ThisDate", "undefined");

        String contents = rt.postForObject(url, map, String.class);
        if(contents == null) return;

        for (String p : patterns) {
            if (contents.indexOf(p) >= 0) {
                log.info("[matched] " + p);
                try {
                    pushToLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                last = System.currentTimeMillis();
                break;
            }
        }
    }

    public void pushToLine() throws IOException {
        TextMessage textMessage = new TextMessage("Forrest Hill go to make reservation !!!");
        PushMessage pushMessage = new PushMessage(
                "Udc13afca55bb2fcf770484940cc240dc",
                textMessage
        );

        Response<BotApiResponse> response =
                LineMessagingServiceBuilder
                        .create(TOKEN_LINE_SCOUTER)
                        .build()
                        .pushMessage(pushMessage)
                        .execute();

        log.info(response.code() + " " + response.message());
    }

    @Data
    @AllArgsConstructor
    static class LineMessageFormat {
        String to;
        Message[] messages;
    }

    @Data
    @AllArgsConstructor
    static class Message {
        String type;
        String text;
    }
}
