package server;

import server.categories.*;
import java.util.*;

// ToDo: Add Javadoc!
// ToDo: Add DEBUG logging (to add context for exceptions)
// ToDo: Can (or should) this class be a static singleton (how would that work with Hibernate)?
public class NoteOrganizer {
    // ToDo: Try making these final once I've got the Hibernate stuff working
    private List<Note> notes;
    private Map<String, List<Note>> categories;

    public NoteOrganizer() {
        notes = new ArrayList<>();
        categories = new HashMap<>();
    }

    public Note getNote(int index) {
        checkForValidNoteIndex(index);
        return notes.get(index);
    }

    public List<Note> getCategory(String name) {
        checkForValidCategory(name);
        return categories.get(name);
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
        if (position > categories.get(name).size()) {
            throw new RuntimeException("Invalid index (" + position + ") for " + name
                    + " category (size " + categories.get(name).size() + ")");
        }
        categories.get(name).addAll(position, notes);
    }

    public Note deleteNote(int index) {
        checkForValidNoteIndex(index);
        return notes.remove(index);
    }
    // ToDo: Will this work for notes that have been retrieved from the DB?
    public Note deleteNote(Note n) {
        checkForValidNoteObject(n);
        notes.remove(n);
        return n;
    }

    public void deleteCategory(String name) {
        checkForValidCategory(name);
        // ToDo: Only allow this if category is empty (doesn't contain any notes)?
        categories.remove(name);
    }

    // ToDo: Add version that takes a note index? Also return the note?
    public void removeNoteFromCategory(String name, Note n) {
        checkForValidCategory(name);
        if (!categories.get(name).contains(n)) {
            throw new RuntimeException("Unable to locate note '" + n.getName() + "' in category '" + name + "'");
        }
        categories.get(name).remove(n);
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

    private void checkForValidNoteIndex(int index) {
        if (index >= notes.size()) {
            throw new RuntimeException("Invalid index (" + index + ") for note list (size " + notes.size() + ")");
        }
    }

    private void checkForValidNoteObject(Note n) {
        if (!notes.contains(n)) {
            throw new RuntimeException("Failed to locate note: '" + n.getName() + "'");
        }
    }

    private void checkForValidCategory(String name) {
        if (!categories.containsKey(name)) {
            throw new RuntimeException("Unable to locate category '" + name + "'");
        }
    }

    // =================================== For testing purposes ===================================
    protected Note addTestNote() {
        Note testNote = Note.createGenericTestNote();
        addNote(testNote);
        return testNote;
    }
    protected List<Note> addTestNotes(int noteCount) {
        List<Note> noteList = new ArrayList<>();
        for (int i = 0; i < noteCount; i++) {
            noteList.add(addTestNote());
        }
        return noteList;
    }

    protected void clearNotes() { notes.clear(); }
    protected void clearCategories() { categories.clear(); }
    protected int numberOfNotes() { return notes.size(); }
    protected int numberOfCategories() { return categories.size(); }
}
