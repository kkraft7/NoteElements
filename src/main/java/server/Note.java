package server;

import server.element.NoteElement;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Need tags to indicate relevant search categories
/**
 * This is the base class for notes and to-do items.
 */
@Entity
@Table(name = "NOTES")
public class Note extends Node<Note> {
    @Id
    @Column(name="NOTE_ID")
    @GeneratedValue
    private Long noteId;
    private String description;
    private final LocalDateTime created;
    // ToDo: Change this to a map?
    private ArrayList<NoteElement> elements;

    // ToDo: Will this update the created date every time a note is retrieved from the DB?
    protected Note() {
        super(null);
        created = LocalDateTime.now();
    }

    public Note(String title, String desc) {
        super(title);
        this.description = desc;
        created = LocalDateTime.now();
        elements = new ArrayList<>();
    }

    public Note(String title) { this(title, null); }

    public String getDescription() { return description; }
    public void setDescription(String newDescription) { description = newDescription; }
    public LocalDateTime getDateCreated() { return created; }

    public void addNote(Note newNote) { addChildItem(newNote); }
    // Add deleteNote()?

    public ArrayList<NoteElement> getElements() { return elements; }
    public void addElement(NoteElement newElement) { elements.add(newElement); }
}
