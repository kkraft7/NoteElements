package server;

import server.element.*;
import server.categories.MediaType;
import java.net.MalformedURLException;
import java.util.*;

import org.junit.jupiter.api.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NoteTest {
    private static final int defaultElementCount = 5;
    private static Note noteWithRandomElements;
    private static Note emptyTestNote;
    private static Note subNote1;
    private static Note subNote2;

    @BeforeAll
    public static void setUpTests() throws MalformedURLException {
        emptyTestNote = NoteTestHelper.createGenericTestNote();
        noteWithRandomElements = NoteTestHelper.createGenericTestNoteWithRandomElements(defaultElementCount);
        subNote1 = NoteTestHelper.createGenericTestNote("Test sub-note");
        subNote1.addElement(new MediaItem("I'm Your Captain", "Grand Funk", MediaType.SONG));
        subNote2 = NoteTestHelper.createGenericTestNote("Test sub-note");
        subNote2.addElement(new Link("Google", "http://facebook.com"));
    }

    @AfterEach
    public void testCleanup() {
        emptyTestNote.clear();
        noteWithRandomElements = NoteTestHelper.createGenericTestNoteWithRandomElements(defaultElementCount);
    }

    @Test
    public void createNoteWithEachElementType() throws MalformedURLException {
        emptyTestNote.addElement(new Contact().setName("Contact1")
                .setPhoneNumber("234-5678").setAddress1("123 4th St.").setCity("Menlo Park"));
/*
        testNote.addElement(new Contact("Contact1", "")
                .setPhoneNumber("234-5678").setAddress1("123 4th St.").setCity("Menlo Park"));
*/
        // testNote.addElement(new Contact("Contact1", "234-5678", "123 4th St.", "Menlo Park"));
        emptyTestNote.addElement(new MediaItem("Blind Alley", "Fanny", MediaType.SONG));
        emptyTestNote.addElement(new Price(5.00));
        emptyTestNote.addElement(new Link("Google", "http://google.com"));
        emptyTestNote.addElement(new EventInfo("Deadline", null, "2020-06-12 00:00:00"));
        emptyTestNote.addElement(new Note("Note1"));

        System.out.println(emptyTestNote);
        // ToDo: Use fluent assertions (no need for Hamcrest matchers)?
        // assertThat("Expected Contact type at position 0", testNote.getElement(0));
        assertSame("Expected Contact type at position 0",
                emptyTestNote.getElement(0), emptyTestNote.getElement(Contact.class, 0));
        assertSame("Expected Contact type at position 0",
                emptyTestNote.getElement(1), emptyTestNote.getElement(MediaItem.class, 0));
        assertSame("Expected Contact type at position 0",
                emptyTestNote.getElement(2), emptyTestNote.getElement(Price.class, 0));
        assertSame("Expected Contact type at position 0",
                emptyTestNote.getElement(3), emptyTestNote.getElement(Link.class, 0));
        assertSame("Expected Contact type at position 0",
                emptyTestNote.getElement(4), emptyTestNote.getElement(EventInfo.class, 0));
    }

    @Test
    public void createNoteWithTwoSubNotes() {
        emptyTestNote.addElement(subNote1);
        emptyTestNote.addElement(subNote2);

        System.out.println(emptyTestNote);
        assertSame("Expected Contact type at position 0",
                emptyTestNote.getElement(0), emptyTestNote.getElement(Note.class, 0));
        assertSame("Expected Contact type at position 0",
                emptyTestNote.getElement(1), emptyTestNote.getElement(Note.class, 1));
    }

    @Test
    public void removeAllElements() {
        System.out.println(noteWithRandomElements);
        assertEquals("Number of Note elements",
                defaultElementCount, noteWithRandomElements.getElements().size());
        noteWithRandomElements.getElements().clear();
        assertEquals("Number of Note elements",0, noteWithRandomElements.getElements().size());
    }

    @Test
    public void removeOneOfMultipleElements() {
        NoteElement<?> element = noteWithRandomElements.getElements().get(0);
        noteWithRandomElements.removeElement(element);
        // This test has randomly failed here:
        assertFalse("Note contains element '" + element.getName() + "'",
                noteWithRandomElements.getElements().contains(element));
        assertEquals("Number of note elements after deletion",
                defaultElementCount - 1, noteWithRandomElements.getElements().size());
    }

    // ToDo: Validate elementLocator after add and remove

    @Test
    public void addElementAtASpecifiedLocation() {
        NoteElement<?> testNoteElement = NoteTestHelper.getRandomNoteElement();
        int elementIndex = 3;
        noteWithRandomElements.addElement(testNoteElement, 3);
        assertEquals("Number of Note elements",
                defaultElementCount + 1, noteWithRandomElements.getElements().size());
        assertSame("Expected note element '" + testNoteElement.getName() + "' at index " + elementIndex,
                testNoteElement, noteWithRandomElements.getElement(3));
    }

    @Test
    public void tryGettingInvalidClassTypeFromElementList() {
        Exception ex = assertThrows(RuntimeException.class, () -> emptyTestNote.getElement(Note.class, 0));
        // System.out.println("Exception message: " + ex.getMessage());
        String expectedMessage = "No Note element found";
        assertTrue("Exception message contains '" + expectedMessage + "'",
                ex.getMessage().contains(expectedMessage));
    }

    @Test
    public void tryGettingInvalidElementIndexFromElementList() throws MalformedURLException {
        emptyTestNote.addElement(new Link("Google", "http://facebook.com"));

        Exception ex = assertThrows(RuntimeException.class, () -> emptyTestNote.getElement(Link.class, 1));
        // System.out.println("Exception message: " + ex.getMessage());
        String expectedMessage = "Invalid index (1) for list of Link elements (size 1)";
        assertEquals("Exception message equals " + expectedMessage, expectedMessage, ex.getMessage());
    }

    // ToDo: Test adding and removing more that one of the same kind of element
    // ToDo: Test adding and removing tags
    // ToDo: Test adding and removing categories (coupled with main program)?
    // ToDo: Test adding tags and categories?
    // ToDo: More negative tests?
    // ToDo: Test adding duplicate notes?
    // ToDo: Test adding a Link element with an invalid URL
}
