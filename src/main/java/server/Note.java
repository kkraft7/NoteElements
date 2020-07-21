package server;

import server.element.NoteElement;
import server.categories.CategoryTag;

import javax.persistence.*;
import java.util.*;
import org.apache.logging.log4j.*;

// Need tags to indicate relevant search categories
/**
 * This is the base class for notes and to-do items.
 */
@Entity
@Table(name = "NOTES")
public class Note extends NoteElement<Note> {
    @Id
    @Column(name="NOTE_ID")
    @GeneratedValue
    private Long noteId;
    private String description;
    // Using "? extends NoteElement" here produces errors...
    private ArrayList<NoteElement> elements;
    private Map<Class<? extends NoteElement>, List<Integer>> elementLocator;
    private List<String> categories;
    private List<CategoryTag> tags;
    private static final Logger LOG = LogManager.getLogger(Note.class);

    // ToDo: Will this update the created date every time a note is retrieved from the DB?
    protected Note() {
        super(null);
    }

    public Note(String title, String desc) {
        super(title, desc);
        elements = new ArrayList<>();
        // This contains a map from Note element class to element indexes in the elements list
        elementLocator = new HashMap<Class<? extends NoteElement>, List<Integer>>();
        this.categories = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public Note(String title) { this(title, null); }

    // ToDo: Can I delete this?
    public ArrayList<NoteElement> getElements() { return elements; }

    /**
     * Add new Note element at the end of the current note.
     * @param newElement The new Note element to be added
     */
    public void addElement(NoteElement<?> newElement) {
        // elements.add(newElement);
        addElement(newElement, elements.size());
    }

    /**
     * Add new Note element at the specified location.
     * @param newElement The new Note element to be added
     * @param index The location (0-based) of the new element
     */
    public void addElement(NoteElement<?> newElement, int index) {
        elements.add(index, newElement);
        updateElementLocator(newElement, index);
    }

    /**
     * Return the requested element from the list of {@link Note} elements.
     * The index refers to the position of the {@link Note} element in a list of elements
     * of the same type. So, for example, if the {@link Note} contains two Contacts index
     * 0 would refer to the first one and index 1 would refer to the second (regardless
     * of their position in the overall list of {@link Note} elements).
     *
     * @param elementClass The class of the {@link Note} element to be retrieved
     * @param index The 0-based index of the {@link Note} element to be retrieved
     *              (for elements of the same class type)
     * @return The requested {@link Note} element (or an exception)
     */
    public NoteElement<?> getElement(Class<? extends NoteElement<?>> elementClass, int index) {
        if (!elementLocator.containsKey(elementClass)) {
            throw new RuntimeException("No " + elementClass.getSimpleName() + " element found in this note");
        }
        if (index >= elementLocator.get(elementClass).size()) {
            throw new RuntimeException("Invalid index (" + index + ") for list of " + elementClass.getSimpleName()
                    + " elements (size " + elementLocator.get(elementClass).size() + ")");
        }
        int elementIndex = elementLocator.get(elementClass).get(index);
        if (elementIndex >= elements.size()) {
            LOG.info(elementClass.getSimpleName() + " element index " + index
                    + " maps to main element index " + elementIndex);
            throw new RuntimeException("Invalid index (" + elementIndex + ") for note element list (size "
                    + elements.size() + ")");
        }
        // ToDo: Do a sanity check that the returned element is the right class?
        return elements.get(elementIndex);
    }

    // ToDo: Add Javadoc
    public boolean hasElement(Class< NoteElement<?>> elementClass) {
        return elementLocator.containsKey(elementClass) && elementLocator.get(elementClass).size() > 0;
    }

    // Explain how this works?
    // I get weird generic errors if I attempt to pass the class here
    private void updateElementLocator(NoteElement<?> element, int index) {
        if (!elementLocator.containsKey(element.getClass())) {
            elementLocator.put(element.getClass(), new ArrayList<>());
        }
        elementLocator.get(element.getClass()).add(index);
    }

    // Add deleteElement()? (also need to update elementLocator)
    // ToDo: Does a Note really need to know what category lists it's in?
    // ToDo: Add Javadoc
    // ToDo: Add tests for categories and tags
    public List<String> getCategories() { return categories; }
    public Note addCategory(String category) { categories.add(category); return this; }
    public Note addCategories(List<String> cats) { categories.addAll(cats); return this; }

    public List<CategoryTag> getTags() { return tags; }
    public Note addTag(CategoryTag tag) { tags.add(tag); return this; }
    public Note addTags(List<CategoryTag> tagList) { tags.addAll(tagList); return this; }
    public boolean containsAllTags(List<CategoryTag> tagList) { return tags.containsAll(tagList); }
    public boolean containsAnyTags(List<CategoryTag> tagList) {
        for (CategoryTag tag : tagList) {
            if (tags.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "Note:\n" + getName();
        if (description != null && !description.equals("")) {
            result += "\n" + description;
        }
        for (NoteElement<?> elem : elements) {
            // ToDo: Warn if element is null?
            result = result.concat("\n\n" + elem);
        }
        return result;
    }

    // For testing
    protected NoteElement<?> getElement(int index) { return elements.get(index); }

    protected void clear() {
        elements.clear();
        elementLocator.clear();
    }

    // Add clearNote() to get back to original "before" state
}
