package server.element;

// ToDo: Should this be private?
public class NoteElement {
    String name;
    String description;

    public NoteElement(String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
    }

    public NoteElement(String newName) { this(newName, null); }

    public String getName() { return name; }
    public void setName(String newName) { this.name = newName; }
    public String getDescription() { return description; }
    public void setDescription(String newDescription) { description = newDescription; }
}
