// HTTP Crawler Client for user to fetch document using socket and parse content as Document using jtidy parser
// @author Christopher Besser
package crawler;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import javax.net.ssl.SSLSocketFactory;
import java.net.SocketTimeoutException;
public class HttpCrawlerClient
{
	// socket argument
	private String host = "";
	private String path = "";
	private int portNum = 80;
	private String url = "";
	// header response
	private String contentType = "";
	private int contentLen = -1;
	private long lastModified = -1;
	private URL urlObject;
	private int code = -1;
	private String body = "";
	private Robot robot = null;
	private boolean secure;
	private String location = "";
	/**
	 * parse url and seperate host, path and portNum, create urlObject
	 * @param url - the URL to parse
	 */
	public void parseURL(String url)
	{
		secure = url.startsWith("https");
		if (!url.startsWith("http"))
		{
			url = "http://" + url;
		}
		try
		{
			urlObject = new URL(url);
		}
		catch (MalformedURLException e)
		{
		}
		this.url = url;
		host = urlObject.getHost();
		path = urlObject.getPath();
		if (!secure)
		{
			portNum = (urlObject.getPort() == -1) ? 80 : urlObject.getPort();
		}
		else
		{
			portNum = (urlObject.getPort() == -1) ? 443 : urlObject.getPort();
		}
	}

	/**
	 * Get the request URL
	 * @return Returns the request URL
	 */
	public String getURL()
	{
		return url;
	}

	/**
	 * send HEAD request and parse HEAD response to check if file is valid
	 */
	public void headRequest()
	{
		try
		{
			Socket socket;
			if (secure)
			{
				SSLSocketFactory fact = (SSLSocketFactory) SSLSocketFactory.getDefault();
				socket = fact.createSocket(host, portNum);
			}
			else
			{
				socket = new Socket(host, portNum);
			}
			// timeout socket after 2 seconds
			socket.setSoTimeout(2000);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.write("HEAD " + path + " HTTP/1.1\r\n");
			out.write("Host: " + host + ":" + portNum + "\r\n");
			out.write("User-Agent: plantcrawler\r\n");
			out.write("Connection: close\r\n\r\n");
			out.flush();
			// parse head response
			InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());
			BufferedReader bufferReader = new BufferedReader(inputReader);
			String nextLine = bufferReader.readLine();
			// check code
			code = Integer.parseInt(nextLine.split(" ")[1]);
			// if connection failed, return
			if ((code != 200) && (code != 301) && (code != 302))
			{
				socket.close();
				return;
			}
			while (nextLine != null)
			{
				System.out.println(nextLine);
				if (nextLine.toLowerCase().contains("location"))
				{
					location = nextLine.substring(nextLine.indexOf(":") + 1);
				}
				if (nextLine.toLowerCase().contains("content-length"))
				{
					contentLen = Integer.parseInt(nextLine.split(":")[1].trim());
				}
				if (nextLine.toLowerCase().contains("content-type"))
				{
					contentType = nextLine.split(":")[1].trim();
				}
				if (nextLine.toLowerCase().contains("last-modified"))
				{
					String timeStr = nextLine.split(":")[1].trim();
					lastModified = getLastModified(timeStr);
				}
				nextLine = bufferReader.readLine();
			}
			socket.close();
		}
		catch (Exception e)
		{
			code = 404;
		}
	}

	/**
	 * Send GET request
	 */
	public void sendGetRequest()
	{
		try
		{
			Socket socket;
			if (secure)
			{
				SSLSocketFactory fact = (SSLSocketFactory) SSLSocketFactory.getDefault();
				socket = fact.createSocket(host, portNum);
			}
			else
			{
				socket = new Socket(host, portNum);
			}
			// Timeout socket after 2 seconds
			socket.setSoTimeout(2000);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.write("GET " + path + " HTTP/1.1\r\n");
			out.write("Host: " + host + ":" + portNum + "\r\n");
			out.write("User-Agent: cis455crawler\r\n");
			out.write("Connection: close\r\n\r\n");
			out.flush();
			// get response body
			body = parseGetResponse(socket.getInputStream());
			socket.close();
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * parse GET response and store content in field body
	 * @param inputStream - the input stream for the response body
	 * @return Returns the response content
	 */
	private String parseGetResponse(InputStream inputStream)
	{
		try
		{
			InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader bufferReader = new BufferedReader(inputReader);
			int emptyLineNum = 0;
			String nextLine = bufferReader.readLine();
			StringBuilder sb = new StringBuilder();
			while (nextLine != null)
			{
				if (emptyLineNum > 0)
				{
					// append after first empty line
					sb.append(nextLine);
				}
				if (nextLine.equals(""))
				{
					// quit after second empty line
					emptyLineNum++;
				}
				nextLine = bufferReader.readLine();
			}
			return sb.toString();
		}
		catch (IOException e)
		{
		}
		return null;
	}

	/**
	 * From MS1: Given string content, generate document using db factory and tidy
	 * @param content - the content to parse
	 * @return Returns the Document's DOM tree
	 */
	public Document generateHTMLDom(String content)
	{
		Document d = null;
		try
		{
			Tidy tidy = new Tidy();
			// HTML true
			tidy.setMakeClean(true);
			tidy.setXHTML(true);
			tidy.setXmlTags(false);
			tidy.setDocType("omit");
			tidy.setShowErrors(0);
			tidy.setEncloseText(true);
			tidy.setShowWarnings(false);
			tidy.setQuiet(true);
			ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			d = tidy.parseDOM(in, null);
			return d;
		}
		catch (Exception e)
		{
		}
		return d;
	}

	/**
	 * parse last modified time, given a string, return a long value
	 * @param timeString - the time
	 * @return - returns the long equivalent of the string
	 */
	private long getLastModified(String timeString)
	{
		SimpleDateFormat simpleDateFormat = null;
		Date d = null;
		if (timeString.charAt(6) == ',')
		{
			simpleDateFormat = new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss z");
		}
		else if (timeString.charAt(3) == ' ')
		{
			simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
		}
		else
		{
			simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		}
		try
		{
			d = simpleDateFormat.parse(timeString);
			return d.getTime();
		}
		catch (ParseException e)
		{
			return 0;
		}
	}

	/**
	 * Get the page robot
	 * @return Returns the robot
	 */
	public Robot downloadRobotRules()
	{
		headRequest();
		if (code != 200)
		{
			return null;
		}
		else
		{
			sendGETRobotRequest();
			return robot;
		}
	}

	/**
	 * Send robot GET request
	 */
	public void sendGETRobotRequest()
	{
		try
		{
			Socket socket;
			if (secure)
			{
				SSLSocketFactory fact = (SSLSocketFactory) SSLSocketFactory.getDefault();
				socket = fact.createSocket(host, portNum);
			}
			else
			{
				socket = new Socket(host, portNum);
			}
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.write("GET " + path + " HTTP/1.1\r\n");
			out.write("Host: " + host + ":" + portNum + "\r\n");
			out.write("User-Agent: cis455crawler\r\n");
			out.write("Connection: close\r\n\r\n");
			out.flush();
			// get response body
			parseGetRobotResponse(socket.getInputStream());
			socket.close();
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * parse GET robot response and store content in field body
	 * @param inputStream - the input response
	 */
	private void parseGetRobotResponse(InputStream inputStream)
	{
		try
		{
			InputStreamReader inputReader = new InputStreamReader(inputStream);
			BufferedReader bufferReader = new BufferedReader(inputReader);
			// initial robot here
			robot = new Robot();
			// default
			String agent = "*";
			String nextLine = bufferReader.readLine();
			while (nextLine != null)
			{
				if (nextLine.indexOf("#") > 0)
				{
					nextLine = nextLine.substring(0, nextLine.indexOf("#"));
				}
				if (nextLine.trim().startsWith("User-agent"))
				{
					agent = nextLine.trim().split(":")[1].trim();
					robot.addAgent(agent);
				}
				else if (nextLine.trim().startsWith("Disallow"))
				{
					String banned = nextLine.trim().split(":")[1].trim();
					robot.addBanned(agent, banned);
				}
				else if (nextLine.trim().startsWith("Allow"))
				{
					String allow = nextLine.trim().split(":")[1].trim();
					robot.addAllow(agent, allow);
				}
				else if (nextLine.trim().startsWith("Crawl-delay"))
				{
					int delay = (int) Math.round(Double.valueOf(nextLine.trim().split(":")[1].trim()));
					robot.addDelay(agent, delay);
				}
				nextLine = bufferReader.readLine();
			}
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * get host
	 * @return Returns the host name
	 */
	public String getHost()
	{
		return this.host;
	}

	/**
	 * get code status
	 * @return - Returns the error code
	 */
	public int getCode()
	{
		return this.code;
	}

	/**
	 * get content type
	 * @return - Returns the content type
	 */
	public String getContentType()
	{
		return this.contentType;
	}

	/**
	 * get content length
	 * @return - Returns the content length
	 */
	public int getContentLength()
	{
		return this.contentLen;
	}

	/**
	 * get last modified as long type
	 * @return - Returns the last modified time
	 */
	public long getLastModified()
	{
		return this.lastModified;
	}

	/**
	 * get content body of HTML file
	 * @return - Returns the file contents
	 */
	public String getBody()
	{
		return this.body;
	}

	/**
	 * Returns if the connection is secure
	 * @return Returns true if the connection is secure
	 */
	public boolean isSecure()
	{
		return secure;
	}

	/**
	 * Get redirect URL
	 * @return Returns the redirect URL
	 */
	public String getRedirectURL()
	{
		return location;
	}

	/**
	 * Get the port number
	 * @return Returns the port number
	 */
	public int getPort()
	{
		return portNum;
	}
}