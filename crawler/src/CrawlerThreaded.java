// Crawler worker for a miltithreaded crawler
// @author: Christopher Besser
package crawler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import org.jsoup.Jsoup;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.Thread;
import java.util.concurrent.locks.ReentrantLock;
import com.planetj.math.rabinhash.RabinHashFunction32;
public class CrawlerThreaded
{
	private int count = 0;
	private int numFiles = 1000000;
	private ReentrantLock countLock = new ReentrantLock();
	private Map<String, Long> timeMap = new HashMap<String, Long>();
	private ReentrantLock timeLock = new ReentrantLock();
	private Map<String, Robot> hostRobotMap = new HashMap<String, Robot>();
	private ReentrantLock hostRobotLock = new ReentrantLock();
	private HashSet<Integer> contentHashes = new HashSet<Integer>();
	private ReentrantLock contentHashLock = new ReentrantLock();
	private RabinHashFunction32 hash = RabinHashFunction32.DEFAULT_HASH_FUNCTION;
	private URLFrontier frontier = new URLFrontier();
	private ReentrantLock frontierLock = new ReentrantLock();
	private HashSet<Integer> URLHashes = new HashSet<Integer>();
	private ReentrantLock URLHashLock = new ReentrantLock();
	private Thread[] workers = null;
	private String[] english = {"that", "have", "which", "this", "from", "they", "would", "the", "build", "target", "hi", "that", "me", "my", "him", "her", "we", "us"};
	/**
	 * Create a new crawler master
	 * @param numFiles - the number of files to crawl
	 * @param textOut - the text output file
	 * @param imageURLsOut - image URL output file
	 */
	public CrawlerThreaded(int numFiles, String textOut, String imageURLsOut)
	{
		try
		{
			this.numFiles = numFiles;
			workers = new Thread[1];
			frontier.add(new HttpURL("https://www.garden.org/plants/group/").getNormalizeURL());
			for (int i = 0; i < workers.length; i++)
			{
				workers[i] = new CrawlerWorker(textOut + i + ".txt", imageURLsOut + i + ".txt");
			}
			for (int i = 0; i < workers.length; i++)
			{
				workers[i].start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Return the current contents of the fronter
	 * @return Returns the frontier
	 */
	private URLFrontier getFrontier()
	{
		return frontier;
	}

	/**
	 * Run the crawler
	 * @param args - the command line arguments
	 */
	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.out.println("Usage: CrawlerThreaded <numFiles> <textOut> <imageURLsOut>");
			return;
		}
		new CrawlerThreaded(Integer.parseInt(args[0]), args[1], args[2]);
	}

	private class CrawlerWorker extends Thread
	{
		private String contentType;
		private int contentLen;
		private long lastModified;
		private boolean secure = false;
		private BufferedWriter textWriter;
		private BufferedWriter imgsWriter;
		private String currentURL;
		/**
		 * constructor.
		 * @param outFile - the webpage dump file
	 	 */
		public CrawlerWorker(String outFile, String imgsOut)
		{
			try
			{
				textWriter = new BufferedWriter(new FileWriter(outFile, false));
				imgsWriter = new BufferedWriter(new FileWriter(imgsOut, false));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		/**
		 * Write to the links file
		 * @param line - the line to write
		 * @param writer - writer to output to
		 */
		public void write(String line, BufferedWriter writer)
		{
			System.out.println(currentURL + "\t" + line);
			try
			{
				writer.write(line + "\n");
				writer.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		/**
		 * Close the writers and flush buffers
		 */
		public void close()
		{
			try
			{
				textWriter.close();
				imgsWriter.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		/**
		 * Main process to start crawling
		 */
		public void run()
		{
			// count the number of files crawled
			while (count < numFiles)
			{
				// Step 1. get a URL
				try
				{
					frontierLock.lock();
					currentURL = frontier.poll();
				}
				finally
				{
					frontierLock.unlock();
				}
				if (currentURL != null)
				{
					System.out.println("Crawling URL: " + currentURL);
				}
				// Step 2. send head and get response to check if document is valid
				HttpCrawlerClient client = new HttpCrawlerClient();
				try
				{
					client.parseURL(currentURL);
				}
				catch (Exception e)
				{
					continue;
				}
				secure = client.isSecure();
				client.headRequest();
				// get header response
				lastModified = client.getLastModified();
				System.out.println(lastModified);
				contentLen = client.getContentLength();
				System.out.println(contentLen);
				contentType = client.getContentType();
				System.out.println(contentType);
				if (contentType.contains(";"))
				{
					contentType = contentType.substring(0, contentType.indexOf(";")).trim();
				}
				// Partly guard against spider traps not flagged in robots.txt by limiting URL length to 200 characters
				if (URLHashes.contains(hash.hash(currentURL)) || (currentURL.length() > 200) || !isValidFile(client, 700))
				{
					continue;
				}
				try
				{
					URLHashLock.lock();
					URLHashes.add(hash.hash(currentURL));
				}
				finally
				{
					URLHashLock.unlock();
				}
				String host = client.getHost();
				// case 2: not crawled yet. check if robot is allowed!
				boolean isValidByRobot = isPolite(client, host, client.getPort(), currentURL);
				if (!isValidByRobot)
				{
					continue;
				}
				if (!timeMap.containsKey(currentURL))
				{
					try
					{
						timeLock.lock();
						timeMap.put(currentURL, 0l);
					}
					finally
					{
						timeLock.unlock();
					}
				}
				// Step 3. download and store in database
				downloadAndStore(client, currentURL);
			}
			close();
		}

		/**
		 * check if current url is allowed by robots.txt from host
		 * @param client - The client used to crawl this page
		 * @param host - the hostname to retrieve robots.txt from
		 * @param currentURL - The currently requested URL
		 * @return Returns true if the host allows robots to access the URL
		 */
		private boolean isPolite(HttpCrawlerClient client, String host, int port, String currentURL)
		{
			Robot robot = hostRobotMap.get(host);
			if (robot == null)
			{
				// download and crawl robot
				robot = downloadRobotRules(currentURL);
				try
				{
					hostRobotLock.lock();
					hostRobotMap.put(host, robot);
				}
				finally
				{
					hostRobotLock.unlock();
				}
				// parse result is null
				if (robot == null)
				{
					return true;
				}
			}
			// already get robot, check if plantcrawler is banned
			return parseRobot(host, port, currentURL, robot, "plantcrawler");
		}

		/**
		 * download and parse robots.txt given url
		 * @param currentURL - the URL whose robots.txt we need
		 * @return Returns the robot specification for the page
		 */
		private Robot downloadRobotRules(String currentURL)
		{
			// create a new client to parse robots.txt
			HttpCrawlerClient robotClient = new HttpCrawlerClient();
			String hostPart;
			try
			{
				hostPart = new URL(currentURL).getHost();
				if (!hostPart.endsWith("/"))
				{
					hostPart += "/";
				}
				String robotURL = hostPart + "robots.txt";
				robotClient.parseURL(robotURL);
				return robotClient.downloadRobotRules();
			}
			catch (MalformedURLException e)
			{
				return null;
			}
		}

		/**
		 * helper function used in isPolite, check if "plantcrawler" is banned by robot
		 * @param host - the hostname whose robots.txt we need
		 * @param currentURL - the URL of the page within the host to access
		 * @param robot - the robots.txt specification
		 * @param agent - the agent described in robots.txt
		 * @return Returns true if the robots.txt allows this crawler
		 */
		private boolean parseRobot(String host, int port, String currentURL, Robot robot, String agent)
		{
			// only check if robot contains agent which should be "plantcrawler" or "*"
			if (robot.containsAgent(agent))
			{
				List<String> allowed = robot.getAllowed(agent);
				List<String> banned = robot.getBanned(agent);
				long lastCrawlTime = robot.getDelay(agent);
				// 1. check allowed
				if (allowed != null)
				{
					for (int i = 0; i < allowed.size(); i++)
					{
						if (checkEquals(currentURL, host + ":" + port + allowed.get(i)))
						{
							return true;
						}
					}
				}
				// 2. check banned
				if (banned != null)
				{
					for (int i = 0; i < banned.size(); i++)
					{
						if (checkEquals(currentURL, host + ":" + port + banned.get(i)))
						{
							return false;
						}
					}
				}
				// 3. check delay time
				if (!checkDelayTime(robot, agent, lastCrawlTime))
				{
					// added to frontier and crawl next time
					try
					{
						frontierLock.lock();
						frontier.add(currentURL);
					}
					finally
					{
						frontierLock.unlock();
					}
					return false;
				}
			}
			// not banned and satisfies delay interval requirement
			return true;
		}

		/**
		 * check if url satisfy minimum crawl delay time interval
		 * @param robot - the robots.txt specification
		 * @param agent - the agent described in robots.txt
		 * @param lastCrawlTime - the last time we crawled this host
		 * @return Returns true if robots.txt allows the crawler to access the page based on cooldowns
		 */
		private boolean checkDelayTime(Robot robot, String agent, long lastCrawlTime)
		{
			long timeInterval = System.currentTimeMillis() - lastCrawlTime;
			long minDelay = 1000 * robot.getDelay(agent);
			if (timeInterval > minDelay)
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		/**
		 * check if url is banned by agent * or agent "plantcrawler"
		 * @param currentURL - the URL we wish to access
		 * @param allowedURL - the specification of allowed URLs
		 * @return Returns true if the crawler is even allowed access to the page
		 */
		private boolean checkEquals(String currentURL, String allowedURL)
		{
			if (currentURL.startsWith("http"))
			{
				currentURL = currentURL.split("//")[1].trim();
			}
			if (currentURL.contains(allowedURL))
			{
				return true;
			}
			return false;
		}

		/**
		 * Send get response, download file and store in database
		 * @param client - the crawler client
		 * @param currentURL - the URL we wish to access
		 */
		private void downloadAndStore(HttpCrawlerClient client, String currentURL)
		{
			// send GET request and crawl the document
			client.sendGetRequest();
			// updated time map
			try
			{
				timeLock.lock();
				timeMap.put(client.getHost(), System.currentTimeMillis());
			}
			finally
			{
				timeLock.unlock();
			}
			// response content body
			String body = client.getBody();
			if ((body == null) || body.equals(""))
			{
				return;
			}
			int hashValue = hash.hash(body);
			// simple content seen test
			if (contentHashes.contains(hashValue))
			{
				return;
			}
			try
			{
				contentHashLock.lock();
				contentHashes.add(hashValue);
			}
			finally
			{
				contentHashLock.unlock();
			}
			Document doc = null;
			if (contentType.trim().contains("text/html"))
			{
				// html: extract and add to queue
				doc = client.generateHTMLDom(body);
				// for bug fixing
				if ((doc == null) || !detectEnglish(body))
				{
					return;
				}
				String host = client.getHost();
				extractAndEnqueue(currentURL, doc, host);
			}
			if (doc != null)
			{
				String title = client.getHost();
				try
				{
					title = Jsoup.parse(body).select("title").first().text();
				}
				catch (NullPointerException e)
				{
				}
				catch (Exception e)
				{
					return;
				}
				if ((count % 1000) == 0)
				{
					System.out.println(count + " pages crawled.");
				}
				long crawlTime = System.currentTimeMillis();
				String noHTML = Jsoup.parse(body).text().toLowerCase().trim();
				write(currentURL + "\t" + noHTML, textWriter);
				extractImageURLs(currentURL, doc, client.getHost(), crawlTime, title);
				try
				{
					countLock.lock();
					count++;
				}
				finally
				{
					countLock.unlock();
				}
			}
			else
			{
				return;
			}
		}

		/**
		 * Simple guess if the page is English. This is by no means foolproof.
		 * @param text - text to test
		 * @return Returns true if the page is likely English
		 */
		private boolean detectEnglish(String text)
		{
			for (String s: english)
			{
				if (text.contains(s))
				{
					return true;
				}
			}
			return false;
		}

		/**
		 * check if response status, content type, and content length is valid
		 * @param client - the Crawler client
		 * @param maxSize - the maximum size of file to crawl
		 * @return Returns true if the file is OK to download (provided the robot does not block the crawler)
	 	*/
		private boolean isValidFile(HttpCrawlerClient client, int maxSize)
		{
			// check code
			if ((client.getCode() == 301) || (client.getCode() == 302))
			{
				String location = client.getRedirectURL().trim();
				if ((location != null) && !location.equals(client.getURL().trim()))
				{
					try
					{
						frontierLock.lock();
						String target = new HttpURL(location).getNormalizeURL();
						frontier.add(target);
					}
					catch (MalformedURLException e)
					{
					}
					finally
					{
						frontierLock.unlock();
					}
				}
				return false;
			}
			else if (client.getCode() != 200)
			{
				return false;
			}
			// check type
			if (!contentType.trim().toLowerCase().contains("text/html"))
			{
				return false;
			}
			// check length
			if (contentLen > 700000)
			{
				return false;
			}
			return true;
		}

		/**
		 * Extract all links from HTML and add to frontier, only apply on HTML file
		 * @param url - the URL of the HTML file
		 * @param doc - the document tree of the file
		 * @param host - the hostname
		 */
		private void extractAndEnqueue(String url, Document doc, String host)
		{
			NodeList nl = doc.getElementsByTagName("a");
			for (int i = 0; i < nl.getLength(); i++)
			{
				Element element = (Element) nl.item(i);
				Node linkNode = element.getAttributeNode("href");
				if (linkNode != null)
				{
					String extractedLink = uniformURL(host, linkNode.getNodeValue().trim() + "", url);
					// Restrict access to only garden.org pages
					if ((extractedLink != null) && (extractedLink.length() > 0) && (extractedLink.startsWith("http://garden.org/plants/") || extractedLink.startsWith("https://garden.org/plants")))
					{
						if (extractedLink.startsWith("/") && !extractedLink.startsWith("//"))
						{
							extractedLink = url + extractedLink;
						}
						if (extractedLink.endsWith("xml"))
						{
							continue;
						}
						if (!extractedLink.endsWith("html") && (extractedLink.charAt(extractedLink.length() - 1) != '/'))
						{
							extractedLink = extractedLink + "/";
						}
						try
						{
							frontierLock.lock();
							String target = new HttpURL(extractedLink).getNormalizeURL();
							frontier.add(target);
						}
						catch (MalformedURLException e)
						{
						}
						finally
						{
							frontierLock.unlock();
						}
					}
				}
			}
		}

		/**
		 * Extract image URLs
		 * @param url - the URL of the HTML file
		 * @param doc - the document tree of the file
		 * @param host - the hostname
		 * @param crawlTime - the time the page was crawled
		 * @param title - the title of the page
		 */
		private void extractImageURLs(String url, Document doc, String host, long crawlTime, String title)
		{
			NodeList nl = doc.getElementsByTagName("img");
			String images = url + "\t";
			boolean write = false;
			HashSet<String> seen = new HashSet<String>();
			for (int i = 0; i < nl.getLength(); i++)
			{
				Element element = (Element) nl.item(i);
				Node linkNode = element.getAttributeNode("src");
				if (linkNode != null)
				{
					HttpURL normURL;
					// Only output to DB if there is at least 1 image linked
					try
					{
						normURL = new HttpURL(linkNode.getNodeValue().trim());
					}
					catch (Exception e)
					{
						continue;
					}
					String extractedLink = normURL.getNormalizeURL();
					if ((extractedLink.length() > 0) && (extractedLink.endsWith(".png") || extractedLink.endsWith(".gif") || extractedLink.endsWith(".jpg") || extractedLink.endsWith(".jpeg")))
					{
						if (!seen.contains(extractedLink))
						{
							images = images + " " + extractedLink;
							write = true;
						}
						seen.add(extractedLink);
					}
				}
			}
			if (write)
			{
				write(images, imgsWriter);
			}
		}

		/**
		 * uniform URL to good format
		 * @param hostName - the host of the URL
		 * @param relativePath - the path from the host we wish to access
		 * @param url - the URL we wish to access
		 * @return Returns the corrected URL for crawling
		 */
		private String uniformURL(String hostName, String relativePath, String url)
		{
			// case 1: relative path already absolute
			if (relativePath.startsWith("www") || relativePath.startsWith("http"))
			{
				return relativePath;
			}
			// Tolerate links missing the http: prefix
			else if (relativePath.startsWith("//"))
			{
				String protocol = secure ? "https" : "http";
				return relativePath.endsWith("/") ? protocol + relativePath : protocol + relativePath + "/";
			}
			// remove first slash in relative path
			else if (relativePath.startsWith("/"))
			{
				relativePath = relativePath.substring(1);
			}
			// case 2: end with /
			if (relativePath.endsWith("/"))
			{
				return "http://" + hostName + "/" + relativePath;
			}
			else
			{
				// case 3: not end with /
				String firstPartURL = url;
				if (url.endsWith("html"))
				{
					firstPartURL = url.substring(0, url.lastIndexOf("/"));
				}
				if (!firstPartURL.endsWith("/"))
				{
					firstPartURL = "/" + firstPartURL;
				}
				// if URL does not end with html, append /
				return firstPartURL + relativePath;
			}
		}
	}
}