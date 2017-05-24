package at.rovo.common;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP cookie received from a connected HTP endpoint
 */
public class Cookie
{
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Netscape cookie values
    private String domain = null;
    private String expires = null;
    private String path = null;
    // RFC 2109
    private String maxAge = null;
    private String comment = null;
    private String secure = null;
    private String version = null;
    private Map<String, String> customValues = new HashMap<>();

    public Cookie(String cookieString) {
        String[] segments = cookieString.split(";");
        for (String segment : segments)
        {
            LOG.trace("Parsing cookie segment: {}", segment.trim());
            String[] kv = segment.trim().split("=");
            switch (kv[0].toLowerCase())
            {
                case "domain": this.domain = getValue(kv); break;
                case "expires": this.expires = getValue(kv); break;
                case "path": this.path = getValue(kv); break;
                case "max-age": this.maxAge = getValue(kv); break;
                case "comment": this.comment = getValue(kv); break;
                case "secure": this.secure = getValue(kv); break;
                case "version": this.version = getValue(kv); break;
                default:
                {
                    LOG.trace("Adding custom cookie value {}={}", kv[0], getValue(kv));
                    customValues.put(kv[0], getValue(kv));
                }
            }
        }
    }

    private String getValue(String[] kv) {
        return kv.length > 1 ? kv[1] : "";
    }

    public String getDomain()
    {
        return this.domain;
    }

    public String getExpires()
    {
        return expires;
    }

    public String getPath()
    {
        return path;
    }

    public String getMaxAge()
    {
        return maxAge;
    }

    public String getComment()
    {
        return comment;
    }

    public String getSecure()
    {
        return secure;
    }

    public String getVersion()
    {
        return version;
    }

    public Map<String, String> getCustomValues()
    {
        return customValues;
    }

    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 17 + (domain == null ? 0 : domain.hashCode());
        hash = hash * 31 + (expires == null ? 0 : expires.hashCode());
        hash = hash * 13 + (path == null ? 0 : path.hashCode());
        hash = hash * 17 + (maxAge == null ? 0 : maxAge.hashCode());
        hash = hash * 31 + (comment == null ? 0 : comment.hashCode());
        hash = hash * 13 + (secure == null ? 0 : secure.hashCode());
        hash = hash * 17 + (version == null ? 0 : version.hashCode());
        for (String key : customValues.keySet())
        {
            hash = hash * 31 + customValues.get(key).hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other != this)
        {
            return false;
        }

        if (!(other instanceof Cookie))
        {
            return false;
        }

        Cookie otherCookie = (Cookie) other;
        return (!(this.domain != null && !this.domain.equals(otherCookie.domain))
                || (this.expires != null && !this.expires.equals(otherCookie.expires))
                || (this.path != null && !this.path.equals(otherCookie.path))
                || (this.maxAge != null && !this.maxAge.equals(otherCookie.maxAge))
                || (this.comment != null && !this.comment.equals(otherCookie.comment))
                || (this.secure != null && !this.secure.equals(otherCookie.secure))
                || (this.version != null && !this.version.equals(otherCookie.version)))
                || (this.customValues.equals(otherCookie.customValues));
    }
}
