/* SALSA/World Wide Computer Project
 *
 * URI : Universal Resource Identifier
 *
 * By Gregory Haik and Carlos Varela.  v0.1  June, 1999
 *
 * midification for name resolution:
 *   if host name = 'localhost'  or '127.0.0.1, map it to
 *     another address.
 *
 */

package salsa.naming;
import salsa.language.ServiceFactory;

/**
 * Extends the java-URLs so that it accept any protocol suffix.
*/
public  class URI implements java.io.Serializable {
    /**
     * a URL is encapsulated because the URL class is final...
     */
    protected java.net.URL url;
    /*
     * contains the URI's prefix (http, ftp, uan, rmsp,...)
     */
    protected java.lang.String protocol;

    protected String translateLocalHostName(String hostname) {
      String lowerCaseHostName=hostname.toLowerCase();
      if (lowerCaseHostName.equals("localhost")||
          (lowerCaseHostName.equals("127.0.0.1"))) {
          try{
            return ServiceFactory.getTheater().getHost();
          } catch (Exception e) {
            return hostname;
          }
      }
      return hostname;
    }

    /**
     * @return the local identifier on the server.
     */
    public String getIdentifier() {
	return url.getFile();
    }

    /**
     * @return the name of the server.
     */
    public String getHost() {
	return url.getHost();
    }

    public String getHostAndPort() {
        return url.getHost() + ":" + url.getPort();
    }

    /**
     * @return the port the server is listening to.
     */
    public int getPort() {
	return url.getPort();
    }

    /**
     * @return the protocol used by the server.
     */
    public String getProtocol() {
	return this.protocol;
    }

    /**
     * convert a URI to a String.
     * @return the String we're talking about.
     */
    public String toString() {
	String urlString = url.toString();
	return this.protocol + urlString.substring(urlString.indexOf(':'));
    }

    public boolean equals(Object that){
	return
	    ((that instanceof URI) &&
	     (this.url.equals(((URI)that).url)) &&
	     (this.protocol.equals(((URI)that).protocol)));

    }
}

