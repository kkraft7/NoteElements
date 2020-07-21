package server;

import server.element.*;
import server.categories.MediaType;
import java.net.MalformedURLException;
import org.junit.jupiter.api.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NoteTest {
    private static Note testNote;
    private static Note subNote1;
    private static Note subNote2;

    @BeforeAll
    public static void setUpTests() throws MalformedURLException {
        testNote = new Note("Test Note", "Description for Test Note");
        subNote1 = new Note("Test sub-note 1", "Description for sub-note 1");
        subNote1.addElement(new MediaItem("I'm Your Captain", "Grand Funk", MediaType.SONG));
        subNote2 = new Note("Test sub-note 2", "Description for sub-note 2");
        subNote2.addElement(new Link("Google", "http://facebook.com"));
    }

    @AfterEach
    public void testCleanup() {
        testNote.clear();
    }

    @Test
    public void createNoteWithEachElementType() throws MalformedURLException {
        testNote.addElement(new Contact().setName("Contact1")
                .setPhoneNumber("234-5678").setAddress1("123 4th St.").setCity("Menlo Park"));
/*
        testNote.addElement(new Contact("Contact1", "")
                .setPhoneNumber("234-5678").setAddress1("123 4th St.").setCity("Menlo Park"));
*/
        // testNote.addElement(new Contact("Contact1", "234-5678", "123 4th St.", "Menlo Park"));
        testNote.addElement(new MediaItem("Blind Alley", "Fanny", MediaType.SONG));
        testNote.addElement(new Price(5.00));
        testNote.addElement(new Link("Google", "http://google.com"));
        testNote.addElement(new EventInfo("Deadline", null, "2020-06-12 00:00:00"));

        System.out.println(testNote);
        // ToDo: Use fluent assertions (no need for Hamcrest matchers)?
        // assertThat("Expected Contact type at position 0", testNote.getElement(0));
        assertSame("Expected Contact type at position 0",
                testNote.getElement(0), testNote.getElement(Contact.class, 0));
        assertSame("Expected Contact type at position 0",
                testNote.getElement(1), testNote.getElement(MediaItem.class, 0));
        assertSame("Expected Contact type at position 0",
                testNote.getElement(2), testNote.getElement(Price.class, 0));
        assertSame("Expected Contact type at position 0",
                testNote.getElement(3), testNote.getElement(Link.class, 0));
        assertSame("Expected Contact type at position 0",
                testNote.getElement(4), testNote.getElement(EventInfo.class, 0));
    }

    @Test
    public void createNoteWithTwoSubNotes() {
        testNote.addElement(subNote1);
        testNote.addElement(subNote2);

        // System.out.println(testNote);
        assertSame("Expected Contact type at position 0",
                testNote.getElement(0), testNote.getElement(Note.class, 0));
        assertSame("Expected Contact type at position 0",
                testNote.getElement(1), testNote.getElement(Note.class, 1));
    }

    @Test
    public void tryGettingInvalidClassTypeFromElementList() {
        Exception ex = assertThrows(RuntimeException.class, () -> testNote.getElement(Note.class, 0));
        // System.out.println("Exception message: " + ex.getMessage());
        String expectedMessage = "No Note element found";
        assertTrue("Exception message contains " + expectedMessage,
                ex.getMessage().contains(expectedMessage));
    }

    @Test
    public void tryGettingInvalidElementIndexFromElementList() throws MalformedURLException {
        testNote.addElement(new Link("Google", "http://facebook.com"));

        Exception ex = assertThrows(RuntimeException.class, () -> testNote.getElement(Link.class, 1));
        // System.out.println("Exception message: " + ex.getMessage());
        String expectedMessage = "Invalid index (1) for list of Link elements (size 1)";
        assertEquals("Exception message equals " + expectedMessage, expectedMessage, ex.getMessage());
    }

    // ToDo: Add elements at a specified location
    // ToDo: More negative tests?
    // ToDo: Test adding duplicate notes?
}
