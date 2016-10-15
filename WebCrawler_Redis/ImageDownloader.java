import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;
import redis.clients.jedis.Jedis;

public class ImageDownloader {
	private static Jedis jedis;
	private void saveImage(String imageURL, String destDir) throws IOException {
	    URL url = new URL(imageURL);
		String fileName = url.getFile();
		String destName = destDir + fileName.substring(fileName.lastIndexOf("/"));
		
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destName);
		
		byte[] b = new byte[2048];
		int length;
		
		while((length = is.read(b)) != -1) {
		    os.write(b, 0, length);
		}
		
		is.close();
		os.close();
	}
	
	public void downloader(String destDir) {
        while(jedis.llen("contentList") > 0) {
        // while(true) {
            String pageContent = jedis.blpop(0, "contentList").get(1);
			
			String regexp = "http(s)?://(\\w+\\.)+(\\w+/)+(\\w+.jpg)";
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(pageContent);

            while(matcher.find()) {
                String imageURL = matcher.group();
                jedis.rpush("imageList", imageURL);
				try {
		            saveImage(imageURL, destDir);
		        } catch(IOException e) {
		            System.out.println("Cannot download image!");
		        }
            }
        }
	}
	
	public static void main(String[] args) {
	    jedis = new Jedis("10.0.0.120", 6379);
        jedis.auth("heymanimwatchingyourightnow");
        System.out.println("Connection to master server sucessfully");
        System.out.println("Server is running: "+jedis.ping());

		new ImageDownloader().downloader("./images/");
		
		jedis.close();
        System.out.println("Redis client closed");

	}
}