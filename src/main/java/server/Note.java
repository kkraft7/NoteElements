package server;

import server.element.NoteElement;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

// Need tags to indicate relevant search categories
/**
 * This is the base class for notes and to-do items.
 */
@Entity
@Table(name = "NOTES")
public class Note extends NoteElement {
    @Id
    @Column(name="NOTE_ID")
    @GeneratedValue
    private Long noteId;
    private String description;
    private final LocalDateTime created;
    // Using "? extends NoteElement" here produces errors...
    private ArrayList<NoteElement> elements;
    private Map<Class<? extends NoteElement>, List<Integer>> elementLocator;

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
        // This contains a map from Note element class to element indexes in the elements list
        elementLocator = new HashMap<Class<? extends NoteElement>, List<Integer>>();
    }

    public Note(String title) { this(title, null); }

    public String getDescription() { return description; }
    public void setDescription(String newDescription) { description = newDescription; }
    public LocalDateTime getDateCreated() { return created; }

    // ToDo: Can I delete this?
    public ArrayList<NoteElement> getElements() { return elements; }

    /**
     * Add new Note element at the end of the current note.
     * @param newElement The new Note element to be added
     */
    public void addElement(NoteElement newElement) {
        // elements.add(newElement);
        addElement(newElement, elements.size());
    }

    /**
     * Add new Note element at the specified location.
     * @param newElement The new Note element to be added
     * @param index The location (0-based) of the new element
     */
    public <T extends NoteElement> void addElement(T newElement, int index) {
        elements.add(index, newElement);
        updateElementLocator(newElement.getClass(), index);
    }

    /**
     * Return the requested element from the list of {@link Note} elements.
     * The index refers to the position of the {@link Note} element in a list of elements
     * of the same type. So, for example, if the {@link Note} contains two Contacts index
     * 0 would refer to the first one and index 1 would refer to the second (regardless
     * of their position in the overall list of {@link Note} elements).
     *
     * ToDo: Describe how this works...
     * @param elementClass The class of the {@link Note} element to be retrieved
     * @param index The 0-based index of the {@link Note} element to be retrieved
     *              (for elements of the same class type)
     * @return The requested {@link Note} element (or an exception)
     */
    public NoteElement getElement(Class<? extends NoteElement> elementClass, int index) {
        if (!elementLocator.containsKey(elementClass)) {
            throw new RuntimeException("No " + elementClass.getSimpleName() + " element found in this note");
        }
        if (index >= elements.size()) {
            throw new RuntimeException("Invalid index (" + index + ") for note element list (size "
                    + elements.size() + ")");
        }
        // ToDo: Do a sanity check that the returned element is the right class?
        return elements.get(elementLocator.get(elementClass).get(index));
    }

    private void updateElementLocator(Class<? extends NoteElement> elementClass, int index) {
        if (!elementLocator.containsKey(elementClass)) {
            elementLocator.put(elementClass, new ArrayList<>());
        }
        elementLocator.get(elementClass).add(index);
    }

    // Add deleteElement()?

    @Override
    public String toString() {
        String result = "Note:\n" + getName();
        if (description != null && !description.equals("")) {
            result += "\n" + description;
        }
        for (NoteElement elem : elements) {
            // ToDo: Warn if element is null?
            result = result.concat("\n\n" + elem);
        }
        return result;
    }

    // For testing
    protected NoteElement getElement(int index) { return elements.get(index); }

    protected void clear() {
        elements.clear();
        elementLocator.clear();
    }

    // Add clearNote() to get back to original "before" state
}
