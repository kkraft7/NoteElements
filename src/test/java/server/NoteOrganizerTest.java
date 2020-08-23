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
// Test adding duplicate Category or Tags
public class NoteOrganizerTest {
    private static final TestOrganizer main = new TestOrganizer();
    private static final Map<CategoryTag, Integer> tagTracker = new HashMap<>();
    private static final String defaultCategoryName = "NewCategory";

    @BeforeAll
    public static void setUpTests() { }    // Currently doesn't seem to be needed
    // ToDo: Put in @BeforeEach?
    // Note newNote = NoteTestHelper.createGenericTestNote();

    @AfterEach
    public void testCleanup() {
        main.clearNotes();
        main.clearCategories();
        tagTracker.clear();
        NoteTestHelper.resetNoteNumber();
    }

    @Test
    public void addNewNote() {
        main.addTestNote();
        assertEquals("Number of notes", 1, main.numberOfNotes());
    }

    @Test
    public void addNewNoteAtASpecifiedLocation() {
        // ToDo: Need new API for this
    }

    @Test
    public void removeOnlyNote() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNote(newNote);
        Note deletedNote = main.deleteNote(newNote);
        assertEquals("Number of notes", 0, main.numberOfNotes());
        assertSame("Note removed", newNote, deletedNote);
    }

    @Test
    public void addMultipleNotes() {
        int expectedCount = 3;
        main.addTestNotes(expectedCount);
        assertEquals("Number of notes", expectedCount, main.numberOfNotes());
    }

    @Test
    public void removeMultipleNotes() {
        int expectedCount = 3;
        main.addTestNotes(expectedCount);
        for (int i = 0; i < expectedCount; i++) {
            Note noteToRemove = main.getNote(0);
            // ToDo: Test the index version too: deleteNote(int index)?
            Note removedNote = main.deleteNote(noteToRemove);
            assertEquals("Number of notes", expectedCount - i - 1, main.numberOfNotes());
            assertSame("Note removed", noteToRemove, removedNote);
        }
    }


    @Test
    public void removeAllNotes() {
        int initialNoteCount = 5;
        main.addTestNotes(initialNoteCount);
        main.getNotes().clear();
        assertEquals("Number of remaining notes after deletion", 0, main.numberOfNotes());
    }

    @Test
    public void removeOneOfMultipleNotes() {
        int initialNoteCount = 5;
        main.addTestNotes(initialNoteCount);
        main.deleteNote(0);
        assertEquals("Number of remaining notes after deletion", initialNoteCount - 1, main.numberOfNotes());
    }

    @Test
    public void removeNoteWithCategories() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNote(newNote);
        String[] categories = new String[] {"category1", "category2", "category3"};
        main.addNoteToCategories(new HashSet<>(Arrays.asList(categories)), newNote);
        main.addCategory("category4");
        main.deleteNote(newNote);
        // Confirm that the note has been deleted from all categories
        for (String name : main.getCategories()) {
            assertFalse("Category '" + name + "' contains note '" + newNote.getName() + "'",
                    main.getCategory(name).contains(newNote));
        }
    }

    @Test
    public void tryRemovingInvalidNoteByIndex() {
        Exception ex = assertThrows(RuntimeException.class, () -> main.deleteNote(1));
        assertEquals("Exception message", "Invalid index (1) for note list (size 0)", ex.getMessage());
    }

    @Test
    public void tryRemovingInvalidNoteByObject() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        Exception ex = assertThrows(RuntimeException.class, () -> main.deleteNote(newNote));
        assertEquals("Exception message", "Failed to locate note: '" + newNote.getName() + "'", ex.getMessage());
    }

    @Test
    public void addNewCategory() {
        main.addCategory(defaultCategoryName);
        assertEquals("Number of categories", 1, main.numberOfCategories());
    }

    @Test
    public void addCategoryWithSpaceInName() {
        String categoryName = "Category name with spaces";
        main.addCategory(categoryName);
        List<Note> category = main.getCategory(categoryName);
        assertEquals("Number of notes in category", 0, category.size());
    }

    @Test
    public void removeLastCategory() {
        main.addCategory(defaultCategoryName);
        main.deleteCategory(defaultCategoryName);
        assertEquals("Number of categories", 0, main.numberOfCategories());
    }

    // ToDo: addMultipleCategories()
    // ToDo: removeOneOfMultipleCategories()

    static final String invalidCategory = "invalidCategory";
    @Test
    public void tryGettingInvalidCategory() {
        Exception ex = assertThrows(RuntimeException.class, () -> main.getCategory(invalidCategory));
        assertEquals("Exception message", "Unable to locate category '" + invalidCategory + "'",
                ex.getMessage());
    }

    @Test
    public void tryRemovingInvalidCategory() {
        Exception ex = assertThrows(RuntimeException.class, () -> main.deleteCategory(invalidCategory));
        String expectedMessage = "Unable to locate category '" + invalidCategory + "'";
        assertTrue("Exception message contains '" + expectedMessage + "'",
                ex.getMessage().contains(expectedMessage));
    }

    @Test
    public void addNoteToCategory() {
        main.addNoteToCategory(defaultCategoryName, NoteTestHelper.createGenericTestNote());
        List<Note> category = main.getCategory(defaultCategoryName);
        assertEquals("Number of notes in category '" + defaultCategoryName + "'",
                1, category.size());
    }

    @Test
    public void removeNoteFromCategory() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNoteToCategory(defaultCategoryName, newNote);
        main.removeNoteFromCategory(defaultCategoryName, newNote);
        List<Note> category = main.getCategory(defaultCategoryName);
        assertEquals("Number of notes in category '" + defaultCategoryName + "'",
                0, category.size());
    }

    @Test
    public void addMultipleNotesToCategory() {
        String categoryName = "testCategory";
        int expectedCount = 3;
        List<Note> noteList = main.addTestNotes(expectedCount);
        main.addNotesToCategory(categoryName, noteList);
        assertEquals("Number of notes in '" + categoryName + "'",
                expectedCount, main.getCategory(categoryName).size());
        for (Note n : noteList) {
            assertTrue("Category " + categoryName + " contains note '" + n.getName() + "'",
                    main.getCategory(categoryName).contains(n));
        }
    }

    @Test
    public void removeMultipleNotesFromCategory() {
        int expectedCount = 3;
        List<Note> noteList = main.addTestNotes(expectedCount);
        main.addNotesToCategory(defaultCategoryName, noteList);
        for (int i = 0; i < expectedCount; i++) {
            main.removeNoteFromCategory(defaultCategoryName, noteList.get(i));
            assertEquals("Number of notes in '" + defaultCategoryName + "'",
                    expectedCount - i - 1, main.getCategory(defaultCategoryName).size());
        }
    }

    // removeOneOfMultipleNotesFromCategory()

    @Test
    public void addNoteToMultipleCategories() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNote(newNote);
        HashSet<String> categories = new HashSet<>(Arrays.asList("category1", "category2", "category3"));
        main.addNoteToCategories(categories, newNote);
        for (String category : categories) {
            assertTrue("Category " + category + " contains note '" + newNote.getName() + "'",
                    main.getCategory(category).contains(newNote));
        }
    }

    @Test
    public void removeNoteFromMultipleCategories() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNote(newNote);
        HashSet<String> categories = new HashSet<>(Arrays.asList("category1", "category2", "category3"));
        main.addNoteToCategories(categories, newNote);
        for (String category : categories) {
            main.removeNoteFromCategory(category, newNote);
            assertFalse("Category " + category + " contains note '" + newNote.getName() + "'",
                    main.getCategory(category).contains(newNote));
        }
    }

    @Test
    public void tryRemovingNoteFromInvalidCategory() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        Exception ex = assertThrows(RuntimeException.class,
                () -> main.removeNoteFromCategory(invalidCategory, newNote));
        assertEquals("Exception message", "Unable to locate category '" + invalidCategory + "'",
                ex.getMessage());
    }

    @Test
    public void tryRemovingInvalidNoteFromCategory() {
        main.addCategory(defaultCategoryName);
        Note newNote = NoteTestHelper.createGenericTestNote();
        Exception ex = assertThrows(RuntimeException.class,
                () -> main.removeNoteFromCategory(defaultCategoryName, newNote));
        assertEquals("Exception message",
                "Unable to locate note '" + newNote.getName() + "' in category '" + defaultCategoryName + "'",
                ex.getMessage());
    }

    @Test
    public void getSingleNoteWithTag() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        CategoryTag mediaType = MediaType.SONG;
        newNote.addTag(mediaType);
        main.addNote(newNote);
        List<Note> taggedNotes = main.getNotesWithTag(mediaType);
        assertEquals("Number of notes with media type " + mediaType, 1, taggedNotes.size());
        assertSame("Note with media type " + mediaType, newNote, taggedNotes.get(0));
    }

    @Test
    public void getMultipleNotesWithSameTag() {
        CategoryTag mediaType = MediaType.SONG;
        int numberOfNotes = 3;
        createNotesWithTags(numberOfNotes, mediaType, main.getNotes());
        List<Note> taggedNotes = main.getNotesWithTag(mediaType);
        assertEquals("Number of notes with media type " + mediaType, numberOfNotes, taggedNotes.size());
    }

    @Test
    public void getMultipleNotesWithVariousTags() {
        int numberOfNotes = 10;
        int total = 0;
        createNotesWithTags(numberOfNotes);
        for (CategoryTag tag : tagTracker.keySet()) {
            List<Note> taggedNotes = main.getNotesWithTag(tag);
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
        main.addTestNotes(numberOfUntaggedNotes);
        assertEquals("Total number of notes", numberOfTaggedNotes + numberOfUntaggedNotes, main.numberOfNotes());
        for (CategoryTag tag : tagTracker.keySet()) {
            List<Note> taggedNotes = main.getNotesWithTag(tag);
            assertEquals("Number of notes with media type " + tag,
                    tagTracker.get(tag).intValue(), taggedNotes.size());
            total += taggedNotes.size();
        }
        assertEquals("Total number of tagged notes", numberOfTaggedNotes, total);
    }

    @Test
    public void tryGettingTaggedNotesFromListContainingOnlyUntaggedNotes() {
        main.addTestNotes(10);    // These are untagged notes
        assertEquals("Number of test tags tracked", tagTracker.size(), 0);
        for (CategoryTag tag : MediaType.get().getTagValues()) {
            List<Note> taggedNotes = main.getNotesWithTag(tag);
            assertEquals("Number of notes with media type " + tag, taggedNotes.size(), 0);
        }
    }

    @Test
    public void tryGettingTaggedNotesFromEmptyList() {
        // The list of test notes is empty by default
        assertEquals("Number of test tags tracked", tagTracker.size(), 0);
        for (CategoryTag tag : MediaType.get().getTagValues()) {
            List<Note> taggedNotes = main.getNotesWithTag(tag);
            assertEquals("Number of notes with media type " + tag, taggedNotes.size(), 0);
        }
    }

    // ToDo: Add test for trying to get tagged note from list that doesn't contain the tag

    @Test
    public void getTaggedNotesWithOrRelationship() {
        createNotesWithMultipleTags();
        List<Note> orResult = main.getNotesWithAnyTags(tagTracker.keySet());
        assertEquals("Number of notes with any queried tags", partitionCount*3, orResult.size());
    }

    @Test
    public void getTaggedNotesWithAndRelationship() {
        createNotesWithMultipleTags();
        List<Note> andResult = main.getNotesWithAllTags(tagTracker.keySet());
        assertEquals("Number of notes with all queried tags", partitionCount, andResult.size());
    }

    // Re-run (at least some) tagging tests with category lists instead of main

    // @Test
    public void testTemplate() {
        throw new RuntimeException("Test not yet implemented");
    }

    // ====================================== Helper Methods ======================================
    // ToDo: Move these to NoteTestHelper
    public void createNotesWithTags(int numberOfNotes) { createNotesWithTags(numberOfNotes, null, main.getNotes()); }
    public void createNotesWithTags(int numberOfNotes, CategoryTag tag) {
        createNotesWithTags(numberOfNotes, tag, main.getNotes());
    }
    // ToDo: Add Category parameter to pass in and test other lists
    public void createNotesWithTags(int numberOfNotes, CategoryTag tag, List<Note> noteList) {
        for (int i = 0; i < numberOfNotes; i++) {
            createNoteWithTag(tag, noteList);
        }
    }
    public void createNoteWithTag(CategoryTag tag) { createNoteWithTag(tag, main.getNotes()); }
    public void createNoteWithTag(CategoryTag tag, List<Note> noteList) {
        CategoryTag newTag = tag == null ? MediaType.get().getRandomTag() : tag;
        Note newNote = NoteTestHelper.createGenericTestNote().addTag(newTag);
        updateTagTracker(newTag);
        System.out.println(newNote);
        noteList.add(newNote);
    }

    // ToDo: Could pass in the list of notes to this method as well
    // ToDo: Create addNote() API that automatically updates tagTracker
    // Creates notes for testing AND and OR tag queries
    static final int partitionCount = 4;
    public void createNotesWithMultipleTags() {
        main.addTestNotes(partitionCount*3);
        for (int i = 0; i < partitionCount*3; i++) {
            if (i < partitionCount*2) {
                main.getNote(i).addTag(MediaType.BOOK);
                updateTagTracker(MediaType.BOOK);
            }
            if (i >= partitionCount) {
                main.getNote(i).addTag(MediaType.FILM);
                updateTagTracker(MediaType.FILM);
            }
        }
    }

    private void updateTagTracker(CategoryTag newTag) {
        if (!tagTracker.containsKey(newTag)) {
            tagTracker.put(newTag, 0);
        }
        tagTracker.put(newTag, tagTracker.get(newTag) + 1);
    }
}
