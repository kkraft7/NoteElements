package server.categories;

import server.Note;

import java.util.ArrayList;
import java.util.List;

// ToDo: May not need this class
public class CategoryList {
    private String name;
    private List<Note> notes;

    // No-arg constructor required by Hibernate
    protected CategoryList() {
        this.name = null;
        this.notes = null; // new ArrayList<Note>()?
    }

    public CategoryList(String listName) {
        this.name = listName;
        this.notes = new ArrayList<Note>();
    }

    // ToDo: Check for null notes List?
    public void addNote(Note newNote) { notes.add(newNote); }
}
