package at.rovo.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * <em>UrlReader</em> reads a HTML pages based on the URL of this page provided as string and loads the content into a
 * BufferedReader if {@link #read(String)} is invoked or directly in a string containing all the page's content using
 * {@link #readPage(String)}.
 * <p>
 * Some pages require cookies to be set to present their content or even redirect to further pages. <em>UrlReader</em>
 * is capable of reading those pages too.
 *
 * @author Roman Vottner
 */
public class UrlReader 
{
	/** The logger of this class */
	private static Logger LOG = LogManager.getLogger(UrlReader.class.getName());
	/** The cookie received from a visited page */
	private String cookie = null;
	/** The original URL of a page */
	private String originURL = null;
	/** The URL after a redirect */
	private String realURL = null;
	/** Specifies if the output should contain line-breaks */
	private boolean includeLineBreaks;

	/**
	 * Creates a new instance of this class.
	 */
	public UrlReader()
	{
		includeLineBreaks = false;
	}

	/**
	 * Creates a new instance of this class and specifies to either include line breaks in the output if
	 * <em>includeLineBreaks</em> is set to true or to omit them if this argument is set to false.
	 *
	 * @param includeLineBreaks
	 * 		If set to true specifies to include line breaks within the output, otherwise line breaks will be omitted
	 */
	public UrlReader(boolean includeLineBreaks)
	{
		this.includeLineBreaks = includeLineBreaks;
	}

	/**
	 * Reads the content of a web document by downloading the content of the specified page.
	 * <p>
	 * If any redirects are necessary, this method will follow these to read the content of the page.
	 *
	 * @param url
	 * 		The URL of the HTML page to load
	 *
	 * @return The content of the HTML page the URL was referring too (after any redirects) contained in a
	 * BufferedReader object
	 *
	 * @throws IOException
	 * 		If an exception during loading the content of the page is thrown
	 * @throws IllegalArgumentException
	 * 		If no valid URL is provided
	 */
	public Scanner read(String url) throws IOException, IllegalArgumentException
	{
		this.checkURL(url);
		
		// used an approach presented by tim_yates at stackoverflow.com
		// (http://stackoverflow.com/questions/7055957/httpurlconnection-to-get-title-of-the-content-and-got-moved-permanently)
		// as pages from nytimes require cookies
		HttpURLConnection httpConn;
		InputStreamReader reader = null;
		this.originURL = url;
		this.realURL = url;
		while (url != null)
		{
			httpConn = (HttpURLConnection)new URL(url).openConnection();
			httpConn.setInstanceFollowRedirects(false);
				
			// If we got a cookie last time round, then add it to our request
			if (cookie != null)
			{
				httpConn.setRequestProperty("Cookie", cookie);
			}

			httpConn.connect();
				
			// Get the response code, and the location to jump to (in case of a redirect)
//			int responseCode = httpConn.getResponseCode();
			url = httpConn.getHeaderField("Location");
			if (url != null)
			{
				this.realURL = url;
			}
			
			// Try and get a cookie the site will set, we will pass this next time round
			cookie = httpConn.getHeaderField("Set-Cookie");

			// TODO: respect page encoding-settings
			reader = new InputStreamReader(httpConn.getInputStream(), StandardCharsets.UTF_8);
		}
		if (reader == null)
		{
			throw new IOException("Could not read input source");
		}
		return new Scanner(reader);
	}

	/**
	 * Reads the content of a web document by downloading the content of the specified page.
	 * <p>
	 * If any redirects are necessary, this method will follow these to read the content of the page.
	 *
	 * @param url
	 * 		The URL of the HTML page to load
	 *
	 * @return The content of the HTML page the URL was referring too (after any redirects) contained in a single String
	 * object
	 *
	 * @throws IllegalArgumentException
	 * 		If no valid URL is provided
	 */
	public String readPage(String url) throws IllegalArgumentException
	{
		this.checkURL(url);
		
		StringBuilder buffer = new StringBuilder();
		try
		{
			Scanner scanner = read(url);
			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine();

				String curHTML = buffer.toString();
				if (curHTML.endsWith(" ") && !line.startsWith(" "))
				{
					buffer.append(line);
				}
				else if (curHTML.endsWith(" ") && line.startsWith(" "))
				{
					buffer.append(line.trim());
				}
				else if (!curHTML.endsWith(" ") && line.startsWith(" "))
				{
					buffer.append(line);
				}
				else if (!line.trim().equals(""))
				{
					buffer.append(" ");
					buffer.append(line);
				}
				// add a line break if requested
				if (this.includeLineBreaks)
				{
					buffer.append("\n");
				}
			}
		}
		catch(IOException ioEx)
		{
			LOG.warn("Could not read {}! Reason: {}", url, ioEx.getLocalizedMessage());
			return null;
		}
		return buffer.toString();
	}

	private void checkURL(String url)
	{
		if (!url.startsWith("http://") && !url.startsWith("https://"))
		{
			throw new IllegalArgumentException("No valid URL provided! Found " + url);
		}
	}
	
	/**
	 * Returns the original URL this instance was requested to load a page from.
	 * 
	 * @return The original URL as provided by a caller
	 */
	public String getOriginURL()
	{
		return this.originURL;
	}

	/**
	 * Returns the URL of a downloaded page after any redirect occurred.
	 *
	 * @return The real URL of the document
	 */
	public String getRealURL()
	{
		return this.realURL;
	}

	/**
	 * An entrance point to downloading pages which uses a URL specified as parameter and prints its content to the
	 * specified log4j2 log file.
	 * <p>
	 * The page to download should be provided as the first argument. Any other arguments will be ignored.
	 *
	 * @param args
	 * 		The arguments passed to UrlReader - only the first is used which should be the URL of the page to read
	 *
	 * @throws IOException
	 * 		If any exception during downloading and reading the page occurred.
	 */
	public static void main(String[] args) throws IOException
	{		
		UrlReader reader = new UrlReader();
		String url = args[0];
		Scanner scanner = reader.read(url);
		LOG.debug("Reading origin url: {}", reader.getOriginURL());
		LOG.debug("Reading real url: {}", reader.getRealURL());
		while (scanner.hasNextLine())
		{
			LOG.debug(scanner.nextLine());
		}
	}
}
