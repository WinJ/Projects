import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Locale;
import java.lang.Thread;
import java.net.*;
import java.io.*;
import redis.clients.jedis.Jedis;

public class WebCrawlerRedis {
    private static Jedis jedis;

    public WebCrawlerRedis(String url) {
        CrawlerHead crawler = new CrawlerHead();

        if(jedis.llen("urlList") == 0) crawler.addFirstURL(url);
        crawler.start();

        // only crawling for 10 seconds.
        try {
            Thread.sleep(10000);
        } catch(Exception ex) {}

        crawler.stop();

        //result = crawler.getResult();
    }

    public static void main(String[] args) {
        jedis = new Jedis("10.0.0.120", 6379);
        jedis.auth("heymanimwatchingyourightnow");
        System.out.println("Connection to master server sucessfully");
        //check whether server is running or not
        System.out.println("Server is running: "+jedis.ping());
        //jedis.set("key3", "hello");
        //System.out.println(jedis.get("key1"));

        WebCrawlerRedis crawlerRedis = new WebCrawlerRedis(args[0]);

        jedis.close();
        System.out.println("Redis closed");
    }


    class CrawlerHead extends Thread {
        // public CrawlerHead() {}

        // add root URL in the queue
        public void addFirstURL(String url) {
			jedis.sadd("visitedURL", url);
			jedis.rpush("urlList", url);
        }

        // get all unvisited URL from the webpage of input URL.
        private void parseUrls(String url) {
    		// 3. third way of parsing http address, using Scanner to read all content at once. The same as the second way, but implemented more explicitly here.
    		Scanner sc = new Scanner("");
            try {
                URLConnection site = new URL(url).openConnection();
                InputStream is = site.getInputStream();
                sc = new Scanner(new BufferedInputStream(is), "UTF-8");
            }
            catch (IOException ioe) {
                System.out.println("Could not open " + url + "!");
            }

            if (!sc.hasNextLine()) return;

            String pageContent = sc.useDelimiter(Pattern.compile("\\A")).next();
			jedis.rpush("contentList", pageContent);

        	String regexp = "http(s)?://(\\w+\\.)+(\\w+)";
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(pageContent);

            while(matcher.find()) {
                String newUrl = matcher.group();
				
				if(!jedis.sismember("visitedURL", newUrl)) {
                    jedis.sadd("visitedURL", newUrl);
					jedis.rpush("urlList", newUrl);
                    System.out.println(newUrl);
				}
            }
        }

        @Override
        public void run() {
            while(true) {
                String url = jedis.blpop(0, "urlList").get(1);

                parseUrls(url);
            }
        }
    }
}
