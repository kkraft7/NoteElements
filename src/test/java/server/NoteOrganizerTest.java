package server;

import org.junit.jupiter.api.*;
import server.categories.CategoryTag;
import server.categories.MediaType;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

// ToDo: Figure out JUnit assertThat() with matchers (and replace assertTrue())
// Confirm that new notes and categories (and notes in categories) are added at the end
// Add multiple notes to a category at a specified position
// Test standard Note categories (set up in initialize())
public class NoteOrganizerTest {
    private static NoteOrganizer notes;
    private static Map<CategoryTag, Integer> tagTracker;
    private static List<CategoryTag> allMediaTags;
    private static Random rand;
    private static final String defaultCategoryName = "NewCategory";

    @BeforeAll
    public static void setUpTests() {
        notes = new NoteOrganizer();
        tagTracker = new HashMap<>();
        allMediaTags = Arrays.asList(MediaType.values());
        rand = new Random();
    }

    @AfterEach
    public void testCleanup() {
        notes.clearNotes();
        notes.clearCategories();
        tagTracker.clear();
    }

    @Test
    public void addNewNote() {
        notes.addTestNote();
        assertEquals("Number of notes", 1, notes.numberOfNotes());
    }

    @Test
    public void removeLastNote() {
        Note newNote = Note.createGenericTestNote();
        notes.addNote(newNote);
        Note deletedNote = notes.deleteNote(newNote);
        assertEquals("Number of notes", 0, notes.numberOfNotes());
        assertSame("Note removed", newNote, deletedNote);
    }

    @Test
    public void tryRemovingInvalidNoteByIndex() {
        Exception ex = assertThrows(RuntimeException.class, () -> notes.deleteNote(1));
        assertEquals("Exception message", "Invalid index (1) for note list (size 0)", ex.getMessage());
    }

    @Test
    public void tryRemovingInvalidNoteByObject() {
        Note newNote = Note.createGenericTestNote();
        Exception ex = assertThrows(RuntimeException.class, () -> notes.deleteNote(newNote));
        assertEquals("Exception message", "Failed to locate note: '" + newNote.getName() + "'", ex.getMessage());
    }

    @Test
    public void addMultipleNotes() {
        int expectedCount = 3;
        notes.addTestNotes(expectedCount);
        assertEquals("Number of notes", expectedCount, notes.numberOfNotes());
    }

    @Test
    public void removeMultipleNotes() {
        int expectedCount = 3;
        notes.addTestNotes(expectedCount);
        for (int i = 0; i < expectedCount; i++) {
            Note noteToRemove = notes.getNote(0);
            // ToDo: Test the index version too: deleteNote(int index)?
            Note removedNote = notes.deleteNote(noteToRemove);
            assertEquals("Number of notes", expectedCount - i - 1, notes.numberOfNotes());
            assertSame("Note removed", noteToRemove, removedNote);
        }
    }

    @Test
    public void addNewCategory() {
        notes.addCategory(defaultCategoryName);
        assertEquals("Number of categories", 1, notes.numberOfCategories());
    }

    @Test
    public void addCategoryWithSpaceInName() {
        String categoryName = "Category name with spaces";
        notes.addCategory(categoryName);
        List<Note> category = notes.getCategory(categoryName);
        assertEquals("Number of notes in category", 0, category.size());
    }

    @Test
    public void removeLastCategory() {
        notes.addCategory(defaultCategoryName);
        notes.deleteCategory(defaultCategoryName);
        assertEquals("Number of categories", 0, notes.numberOfCategories());
    }

    @Test
    public void tryGettingInvalidCategory() {
        String categoryName = "invalidCategory";
        Exception ex = assertThrows(RuntimeException.class, () -> notes.getCategory(categoryName));
        assertEquals("Exception message", "Unable to locate category '" + categoryName + "'", ex.getMessage());
    }

    @Test
    public void tryRemovingInvalidCategory() {
        String categoryName = "invalidCategory";
        Exception ex = assertThrows(RuntimeException.class, () -> notes.deleteCategory(categoryName));
        String expectedMessage = "Unable to locate category '" + categoryName + "'";
        assertTrue("Exception message contains '" + expectedMessage + "'",
                ex.getMessage().contains(expectedMessage));
    }

    @Test
    public void addNoteToCategory() {
        notes.addNoteToCategory(defaultCategoryName, Note.createGenericTestNote());
        List<Note> category = notes.getCategory(defaultCategoryName);
        assertEquals("Number of notes in category '" + defaultCategoryName + "'",
                1, category.size());
    }

    @Test
    public void removeNoteFromCategory() {
        Note newNote = Note.createGenericTestNote();
        notes.addNoteToCategory(defaultCategoryName, newNote);
        notes.removeNoteFromCategory(defaultCategoryName, newNote);
        List<Note> category = notes.getCategory(defaultCategoryName);
        assertEquals("Number of notes in category '" + defaultCategoryName + "'",
                0, category.size());
    }

    @Test
    public void addMultipleNotesToCategory() {
        String categoryName = "testCategory";
        int expectedCount = 3;
        List<Note> noteList = notes.addTestNotes(expectedCount);
        notes.addNotesToCategory(categoryName, noteList);
        assertEquals("Number of notes in '" + categoryName + "'",
                expectedCount, notes.getCategory(categoryName).size());
        for (Note n : noteList) {
            assertTrue("Category " + categoryName + " contains note '" + n.getName() + "'",
                    notes.getCategory(categoryName).contains(n));
        }
    }

    @Test
    public void removeMultipleNotesFromCategory() {
        String categoryName = "testCategory";
        int expectedCount = 3;
        List<Note> noteList = notes.addTestNotes(expectedCount);
        notes.addNotesToCategory(categoryName, noteList);
        for (int i = 0; i < expectedCount; i++) {
            notes.removeNoteFromCategory(categoryName, noteList.get(i));
            assertEquals("Number of notes in '" + categoryName + "'",
                    expectedCount - i - 1, notes.getCategory(categoryName).size());
        }
    }

    @Test
    public void tryRemovingNoteFromInvalidCategory() {
        String categoryName = "invalidCategory";
        Note newNote = Note.createGenericTestNote();
        Exception ex = assertThrows(RuntimeException.class, () -> notes.removeNoteFromCategory(categoryName, newNote));
        assertEquals("Exception message", "Unable to locate category '" + categoryName + "'", ex.getMessage());
    }

    @Test
    public void tryRemovingInvalidNoteFromCategory() {
        String categoryName = "newCategory";
        notes.addCategory(categoryName);
        Note newNote = Note.createGenericTestNote();
        Exception ex = assertThrows(RuntimeException.class, () -> notes.removeNoteFromCategory(categoryName, newNote));
        assertEquals("Exception message",
                "Unable to locate note '" + newNote.getName() + "' in category '" + categoryName + "'",
                ex.getMessage());
    }

    // Tag tests
    @Test
    public void getSingleNoteWithTag() {
        Note newNote = Note.createGenericTestNote();
        CategoryTag mediaType = MediaType.SONG;
        newNote.addTag(mediaType);
        notes.addNote(newNote);
        List<Note> taggedNotes = notes.getNotesWithTag(mediaType);
        assertEquals("Number of notes with media type " + mediaType, 1, taggedNotes.size());
        assertSame("Note with media type " + mediaType, newNote, taggedNotes.get(0));
    }

    @Test
    public void getMultipleNotesWithSameTag() {
        CategoryTag mediaType = MediaType.SONG;
        int numberOfNotes = 3;
        createNotesWithTags(numberOfNotes, mediaType);
        List<Note> taggedNotes = notes.getNotesWithTag(mediaType);
        assertEquals("Number of notes with media type " + mediaType, numberOfNotes, taggedNotes.size());
    }

    @Test
    public void getMultipleNotesWithVariousTags() {
        int numberOfNotes = 10;
        int total = 0;
        createNotesWithTags(numberOfNotes);
        for (CategoryTag tag : tagTracker.keySet()) {
            List<Note> taggedNotes = notes.getNotesWithTag(tag);
            assertEquals("Number of notes with media type " + tag,
                    tagTracker.get(tag).intValue(), taggedNotes.size());
            total += taggedNotes.size();
        }
        assertEquals("Total number of tagged notes", numberOfNotes, total);
    }

    @Test
    public void getTaggedNotesFromListContainingTaggedAndUntaggedNotes() {
        int numberOfTaggedNotes = 10;
        int numberOfUntaggedNotes = 5;
        int total = 0;
        createNotesWithTags(numberOfTaggedNotes);
        // This is identical to the above test except for this line adding untagged notes
        notes.addTestNotes(numberOfUntaggedNotes);
        assertEquals("Total number of notes", numberOfTaggedNotes + numberOfUntaggedNotes, notes.numberOfNotes());
        for (CategoryTag tag : tagTracker.keySet()) {
            List<Note> taggedNotes = notes.getNotesWithTag(tag);
            assertEquals("Number of notes with media type " + tag,
                    tagTracker.get(tag).intValue(), taggedNotes.size());
            total += taggedNotes.size();
        }
        assertEquals("Total number of tagged notes", numberOfTaggedNotes, total);
    }

    @Test
    public void tryGettingTaggedNotesFromListContainingOnlyUntaggedNotes() {
        notes.addTestNotes(10);     // These are untagged notes
        assertEquals("Number of test tags tracked", tagTracker.size(), 0);
        for (CategoryTag tag : allMediaTags) {
            List<Note> taggedNotes = notes.getNotesWithTag(tag);
            assertEquals("Number of notes with media type " + tag, taggedNotes.size(), 0);
        }
    }

    @Test
    public void tryGettingTaggedNotesFromEmptyList() {
        // The list of test notes is empty by default
        assertEquals("Number of test tags tracked", tagTracker.size(), 0);
        for (CategoryTag tag : allMediaTags) {
            List<Note> taggedNotes = notes.getNotesWithTag(tag);
            assertEquals("Number of notes with media type " + tag, taggedNotes.size(), 0);
        }
    }

    // Re-run (at least some) tagging tests with category lists instead of main
    // Tests for AND and OR tag queries

    @Test
    public void testTemplate() {
        // throw new RuntimeException("Test not yet implemented");
    }

    // ====================================== Helper Methods ======================================
    public void createNotesWithTags(int numberOfNotes) { createNotesWithTags(numberOfNotes, null); }
    // ToDo: Add Category parameter to pass in and test other lists
    public void createNotesWithTags(int numberOfNotes, CategoryTag tag) {
        for (int i = 0; i < numberOfNotes; i++) {
            Note newNote = Note.createGenericTestNote();
            // Move this to MediaType and call it getRandomType()?
            CategoryTag newTag = tag == null ? allMediaTags.get(rand.nextInt(allMediaTags.size())) : tag;
            newNote.addTag(newTag);
            if (!tagTracker.containsKey(newTag)) {
                tagTracker.put(newTag, 0);
            }
            tagTracker.put(newTag, tagTracker.get(newTag) + 1);
            notes.addNote(newNote);
        }
    }
}
