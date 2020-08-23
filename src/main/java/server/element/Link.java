package server.element;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a web link.
 */
public class Link extends NoteElement<Link> {
  // ToDo: See if I can set this to final once I get the Hibernate stuff working
  private URL linkURL;
  private boolean broken = false;

  // No-arg constructor required by Hibernate
  protected Link() {
    super(null, null);
    this.linkURL = null;
  }

  public Link(String linkName, String linkNote, String url) {
    super(linkName, linkNote);
    try {
      this.linkURL = new URL(url);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Failed to create link for malformed URL: " + url);
    }
  }

  public Link(String name, String url) { this(name, null, url); }

  public URL getUrl() { return linkURL; }
  // ToDo: Need setter for Hibernate?

  public boolean isBroken() { return broken; }
  public void setBroken(boolean value) { broken = value; }

  @Override
  public String toString() {
    return "Link:\n" + super.toString() + "\n" + linkURL.toString();
  }
}
