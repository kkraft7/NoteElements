package server.element;

import server.categories.MediaType;

/**
** Represents an artwork in a specified medium.
*/
public class MediaItem extends NoteElement<MediaItem> {
  private String creator;
  private final MediaType type;

  // public enum Category { RECORD, SONG, BOOK, FILM, TV_SHOW, VIDEO }

  protected MediaItem() {
    super(null, null);
    this.creator = null;
    this.type = null;
  }

  public MediaItem(String title, String description, String creator, MediaType type) {
    super(title, description);
    this.creator = creator;
    this.type = type;
  }

  public MediaItem(String title, String creator, MediaType type) { this(title, null, creator, type); }

  public String getCreator() { return creator; }
  public void setCreator(String newCreator) { this.creator = newCreator; }

  public MediaType getMediaType() { return type; }   // Do we need a setter for this?

  @Override
  public String toString() {
    return type.name() + ":\n" + super.toString() + "\n" + creator;
  }
}
