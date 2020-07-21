package server;

import server.categories.*;
import java.util.*;

// ToDo: Add Javadoc!
// ToDo: Add tests
// ToDo: Can (or should) this class be a static singleton (how would that work with Hibernate)?
public class NoteOrganizer {
    // ToDo: Try making these final once I've got the Hibernate stuff working
    private List<Note> notes;
    private Map<String, List<Note>> categories;

    public NoteOrganizer() {
        notes = new ArrayList<>();
        categories = new HashMap<>();
    }

    public void addNote(Note newNote) { notes.add(newNote); }

    public void addCategory(String name) { categories.put(name, new ArrayList<>()); }
    public void addNoteToCategory(String name, Note newNote) {
        addNotesToCategory(name, List.of(newNote));
    }
    public void addNoteToCategory(String name, Note newNote, int position) {
        addNotesToCategory(name, List.of(newNote), position);
    }
    // Add Notes at the front by default
    public void addNotesToCategory(String name, List<Note> notes) {
        addNotesToCategory(name, notes, 0);
    }
    public void addNotesToCategory(String name, List<Note> notes, int position) {
        if (!categories.containsKey(name)) {
            addCategory(name);
        }
        if (position >= categories.get(name).size()) {
            throw new RuntimeException("Invalid index (" + position + ") for " + name
                    + " category (size " + categories.get(name).size() + ")");
        }
        categories.get(name).addAll(position, notes);
    }

    public List<Note> getNotesWithTag(CategoryTag tag) { return getNotesWithTag(tag, notes); }
    public List<Note> getNotesWithTag(CategoryTag tag, List<Note> list) {
        return getNotesWithTags(List.of(tag), list, true);
    }
    public List<Note> getNotesWithAllTags(List<CategoryTag> tags) { return getNotesWithAllTags(tags, notes); }
    public List<Note> getNotesWithAllTags(List<CategoryTag> tags, List<Note> list) {
        return getNotesWithTags(tags, list, true);
    }
    public List<Note> getNotesWithAnyTags(List<CategoryTag> tags) { return getNotesWithAnyTags(tags, notes); }
    public List<Note> getNotesWithAnyTags(List<CategoryTag> tags, List<Note> list) {
        return getNotesWithTags(tags, list, false);
    }
    private List<Note> getNotesWithTags(List<CategoryTag> tags, List<Note> list, boolean allTags) {
        List<Note> notesWithTags = new ArrayList<>();
        for (Note n : list) {
            if ((allTags && n.containsAllTags(tags)) || n.containsAnyTags(tags)) {
                notesWithTags.add(n);
            }
        }
        return notesWithTags;
    }

    public void initialize() {
        // ToDo: Set up standard Note categories (e.g. scheduled, purchase, etc.)
    }
}
