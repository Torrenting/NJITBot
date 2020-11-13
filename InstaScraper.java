package me.abem.njit;

import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.model.Account;
import me.postaddict.instagram.scraper.model.Media;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InstaScraper {

    private static final ArrayList<String> accs = new ArrayList<>();
    private static HashMap<String, Date> lastPost = new HashMap<>();
    private static final String channelID = "CHANNEL ID";

    public static void start() {
        accs.add("njitocca");
        accs.add("njitstudentlife");
        accs.add("njit_senate");
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    check();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public static void check() throws IOException {
        OkHttpClient c = new OkHttpClient();
        Instagram instagram = new Instagram(c);
        for(String s : accs) {
            Account a = instagram.getAccountByUsername(s);
            Media m = a.getMedia().getNodes().get(0);
            if(!lastPost.containsKey(s)) {
                ZonedDateTime n = ZonedDateTime.now();
                ZonedDateTime y = n.plusDays(-1);
                if (!m.getCreated().toInstant().isBefore(y.toInstant())) {
                    Bot.sendMessageToChannel(channelID, a, m);
                    lastPost.put(s, m.getCreated());
                }
            } else {
                if (!(lastPost.get(s).equals(m.getCreated()))) {
                    Bot.sendMessageToChannel(channelID, a, m);
                    lastPost.remove(s);
                    lastPost.put(s, m.getCreated());
                }
            }
        }

    }

}
