// URL Normalizer
// Author: Christopher Besser
package crawler;
import java.net.MalformedURLException;
public class HttpURL
{
	private String host;
	private Integer port;
	private String path;
	private String query;
	private String fragment;
	private boolean secure = false;
	/**
	 * The constructor for HttpURL
	 * @param httpURL - the url
	 * @throws MalformedURLException if the URL is bad
	 */
	public HttpURL(String httpURL) throws MalformedURLException
	{
		httpURL = httpURL.trim();
		secure = httpURL.startsWith("https://");
		String temp = secure ? httpURL.replace("https://", "") : httpURL.replace("http://", "");
		if ((temp == null) || temp.equals("") || temp.equals(httpURL))
		{
			throw new MalformedURLException("@HttpURL: **** Malformed URL ****");
		}
		// get the fragment part */
		if (temp.contains("#"))
		{
			fragment = temp.substring(temp.indexOf("#"));
			temp = temp.substring(0, temp.indexOf("#"));
		}
		else
		{
			fragment = "";
		}
		// get the query part
		if (temp.contains("?"))
		{
			query = temp.substring(temp.indexOf("?"));
			temp = temp.substring(0, temp.indexOf("?"));
		}
		else
		{
			query = "";
		}
		// get the path part
		if (temp.contains("/"))
		{
			path = temp.substring(temp.indexOf("/"));
			temp = temp.substring(0, temp.indexOf("/"));
		}
		else
		{
			path = "/";
		}
		// get the port number part
		if (temp.contains(":"))
		{
			try
			{
				port = Integer.parseInt(httpURL.substring(httpURL.indexOf(":") + 1));
			}
			catch (Exception e)
			{
				port = secure ? 443 : 80;
			}
			temp = temp.substring(0, temp.indexOf(":"));
		}
		else
		{
			port = secure ? 443 : 80;
		}
		// get the host part
		host = temp;
		if (host.equals(""))
		{
			host = null;
			port = null;
			path = null;
			query = null;
			fragment = null;
			throw new MalformedURLException("@HttpURL: **** Host Name is not valid ****");
		}
	}

	/**
	 * Get the host
	 * @return the host
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * Get protocol
	 * @return Returns true if protocol = https
	 */
	public boolean getSecure()
	{
		return secure;
	}

	/**
	 * Get the port
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Get the path
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Get the query
	 * @return the query
	 */
	public String getQuery()
	{
		return query;
	}

	/**
	 * Get the fragment
	 * @return the fragment
	 */
	public String getFragment()
	{
		return fragment;
	}

	/**
	 * Constructs a string representation of this URL.
	 * @return string represent of this URL
	 */
	public String getNormalizeURL()
	{
		StringBuffer temp = new StringBuffer("");
		if (!secure)
		{
			temp.append("http://").append(host).append(":").append(String.valueOf(port)).append(path).append(query).append(fragment);
		}
		else
		{
			temp.append("https://").append(host).append(":").append(String.valueOf(port)).append(path).append(query).append(fragment);
		}
		return temp.toString();
	}
}