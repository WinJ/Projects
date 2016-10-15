import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Scanner;
import java.util.Locale;
import java.lang.Thread;
import java.net.*;
import java.io.*;

public class MultiThreadCrawler {
    private static int N = 7;
    private static List<String> result;

    // start multithread crawler by calling CrawlerThread
    public MultiThreadCrawler(String url, String keyWord) {
        CrawlerThread.addFirstURL(url);
        
        CrawlerThread[] crawlerThread = new CrawlerThread[N];
        for(int i = 0; i < N; i++) {
            crawlerThread[i] = new CrawlerThread(keyWord);
            crawlerThread[i].start();
        }
        
        // only crawling for 2 seconds.
        try {
            Thread.sleep(10000);
        } catch(Exception ex) {}
        
        for(int i = 0; i < N; i++) {
            crawlerThread[i].stop();
        }
        
        result = CrawlerThread.getResult();
    }

    public static void main(String[] args) {
    	MultiThreadCrawler crawler = new MultiThreadCrawler(args[0], args[1]);
    }
}

class CrawlerThread extends Thread {
    private static BlockingQueue<String> q = new LinkedBlockingQueue<String>();
    private static Map<String, Boolean> map = new HashMap<String, Boolean>();
    private static List<String> result = new ArrayList<String>();
    private static int kwCount;
    private static String keyWord;
    
    public CrawlerThread(String keyWord) {
        this.keyWord = keyWord;
    }

    // add root URL in the queue
    public static void addFirstURL(String url) {
        try {
            q.put(url);
        } catch(InterruptedException ex) {
            //ex.printStackTrace();
        }
        kwCount = 0;
    }

    public static List<String> getResult() {
        return result;
    }

    // get all URL from the webpage of input URL.
    private static List<String> parseUrls(String url) {
    	List<String> urlList = new ArrayList<String>();
    	// 1. first way of parsing http address, using BufferedReader.
/*    	
		BufferedReader urlIn = null;
 
		try {
			urlIn = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Cannot open " + url + "!");
		}
 
		String pageContent = "", t = "";
		try {
			while( ( t = urlIn.readLine()) != null){
				pageContent += t;
				//System.out.println(htmlPageContent);
			}
		} catch (IOException e) {	
			//e.printStackTrace();
		}
*/

		// 2. second way of parsing http address, using In class from Princeton.cs algs4 class
/*        String pageContent = "";
        try {
            // open url
            In in = new In(url);
            pageContent = in.readAll();
        } catch (IllegalArgumentException e) {
            System.out.println("Cannot open " + url + "!");
        } // if cannot open, report error.
*/

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

        if (!sc.hasNextLine()) return urlList;

        String pageContent = sc.useDelimiter(Pattern.compile("\\A")).next();

    	String regexp = "http(s)?://(\\w+\\.)+(\\w+)";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(pageContent);

        while(matcher.find()) {
            String newUrl = matcher.group();
            urlList.add(newUrl);
        }

        String regexp_search = keyWord;
        Pattern pattern_search = Pattern.compile(regexp_search);
        Matcher matcher_search = pattern_search.matcher(pageContent);

        while(matcher_search.find()) {
            kwCount++;
        }

        return urlList;
    }
    
    @Override
    public void run() {
        while(true) {
            String url = "";
            try {
                url = q.take();
            } catch(Exception ex) {
                //ex.printStackTrace();
                break;
            }
            
            String domain = "";
            try {
                URL tmp = new URL(url);
                domain = tmp.getHost();
            } catch(MalformedURLException ex) {}
            
            if(!map.containsKey(url) && domain.endsWith("")) { // can specify the target domain here.
                map.put(url, true);
                result.add(url);
                System.out.println(url);
                
                List<String> list = parseUrls(url);
                System.out.println(kwCount);
                for(String s : list) {
                    try {
                        q.put(s);
                    } catch(InterruptedException ex) {
                        //ex.printStackTrace();
                    }
                }
            }
        }
    }
}