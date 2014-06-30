package at.rovo.common;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * <em>UrlReader</em> reads a HTML pages based on the URL of this page provided
 * as string and loads the content into a BufferedReader if
 * {@link #read(String)} is invoked or directly in a string containing all the
 * page's content using {@link #readPage(String)}.
 * </p>
 * <p>
 * Some pages require cookies to be set to present their content or even
 * redirect to further pages. <em>UrlReader</em> is capable of reading those
 * pages too.
 * </p>
 * 
 * @author Roman Vottner
 */
public class UrlReader 
{
	/** The logger of this class **/
	private static Logger logger = LogManager.getLogger(UrlReader.class.getName());
	/** The cookie received from a visited page **/
	private String cookie = null;
	/** The original URL of a page **/
	private String originURL = null;
	/** The URL after a redirect **/
	private String realURL = null;
	
	/**
	 * <p>
	 * Creates a new instance of this class.
	 * </p>
	 */
	public UrlReader()
	{
		
	}
	
	/**
	 * <p>
	 * Reads the content of a web document by downloading the content of the
	 * specified page.
	 * </p>
	 * <p>
	 * If any redirects are necessary, this method will follow these to read the
	 * content of the page.
	 * </p>
	 * 
	 * @param url
	 *            The URL of the HTML page to load
	 * @return The content of the HTML page the URL was referring too (after any
	 *         redirects) contained in a BufferedReader object
	 * @throws IOException
	 *             If an exception during loading the content of the page is
	 *             thrown
	 * @throws IllegalArgumentException
	 *             If no valid URL is provided
	 */
	public BufferedReader read(String url) throws IOException, IllegalArgumentException
	{
		this.checkURL(url);
		
		// used an approach presented by tim_yates at stackoverflow.com
		// (http://stackoverflow.com/questions/7055957/httpurlconnection-to-get-title-of-the-content-and-got-moved-permanently)
		// as pages from nytimes require cookies
		HttpURLConnection httpConn = null;
		InputStreamReader reader = null;
		this.originURL = url;
		this.realURL = url;
		while (url != null)
		{
			httpConn = (HttpURLConnection)new URL(url).openConnection();
			httpConn.setInstanceFollowRedirects(false);
				
			// If we got a cookie last time round, then add it to our request
			if (cookie != null)
				httpConn.setRequestProperty("Cookie", cookie);
				
			httpConn.connect();
				
			// Get the response code, and the location to jump to (in case of a redirect)
//			int responseCode = httpConn.getResponseCode();
			url = httpConn.getHeaderField("Location");
			if (url != null)
				this.realURL = url;
			
			// Try and get a cookie the site will set, we will pass this next time round
			cookie = httpConn.getHeaderField("Set-Cookie");
			
			reader = new InputStreamReader(httpConn.getInputStream(), StandardCharsets.UTF_8);
//			reader = new InputStreamReader(new URL(url).openStream(), "8859_1");
		}
		return new BufferedReader(reader);
	}
	
	/**
	 * <p>
	 * Reads the content of a web document by downloading the content of the
	 * specified page.
	 * </p>
	 * <p>
	 * If any redirects are necessary, this method will follow these to read the
	 * content of the page.
	 * </p>
	 * 
	 * @param url
	 *            The URL of the HTML page to load
	 * @return The content of the HTML page the URL was referring too (after any
	 *         redirects) contained in a single String object
	 * @throws IllegalArgumentException
	 *             If no valid URL is provided
	 */
	public String readPage(String url) throws IllegalArgumentException
	{
		this.checkURL(url);
		
		StringBuffer buffer = new StringBuffer();
		try
		{
			BufferedReader reader = read(url);
			String line = reader.readLine();
			String curHTML = "";
			while (line != null)
			{
				curHTML = buffer.toString();
				if (curHTML.endsWith(" ") && !line.startsWith(" "))
					buffer.append(line);
				else if (curHTML.endsWith(" ") && line.startsWith(" "))
					buffer.append(line.trim());
				else if (!curHTML.endsWith(" ") && line.startsWith(" "))
					buffer.append(line);
				else
					buffer.append(" "+line);
				line = reader.readLine();
			}
		}
		catch(IOException ioEx)
		{
			logger.warn("Could not read {}! Reason: {}", url, ioEx.getLocalizedMessage());
			return null;
		}
		return buffer.toString();
	}

	private void checkURL(String url)
	{
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			throw new IllegalArgumentException("No valid URL provided! Found "+url);
	}
	
	/**
	 * <p>
	 * Returns the original URL this instance was requested to load a page from.
	 * </p>
	 * 
	 * @return The original URL as provided by a caller
	 */
	public String getOriginURL()
	{
		return this.originURL;
	}
	
	/**
	 * <p>Returns the URL of a downloaded page after any redirect occurred.</p>
	 * 
	 * @return The real URL of the document
	 */
	public String getRealURL()
	{
		return this.realURL;
	}
	
	/**
	 * <p>
	 * An entrance point to downloading pages which uses a URL specified as
	 * parameter and prints its content to the specified log4j2 log file.
	 * </p>
	 * <p>
	 * The page to download should be provided as the first argument. Any other
	 * arguments will be ignored.
	 * </p>
	 * 
	 * @param args
	 *            The arguments passed to UrlReader - only the first is used
	 *            which should be the URL of the page to read
	 * @throws IOException
	 *             If any exception during downloading and reading the page
	 *             occurred.
	 */
	public static void main(String[] args) throws IOException
	{		
		UrlReader reader = new UrlReader();
		String url = args[0];
		BufferedReader br = reader.read(url);
		logger.debug("Reading origin url: {}", reader.getOriginURL());
		logger.debug("Reading real url: {}", reader.getRealURL());
		String line = br.readLine();
		while (line != null)
		{
			logger.debug(line);
			line = br.readLine();
		}
	}
}
