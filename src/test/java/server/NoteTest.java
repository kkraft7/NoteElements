package server;

import server.element.*;
import java.net.MalformedURLException;
import org.junit.jupiter.api.*;

public class NoteTest {
    private static Note subNote1;
    private static Note subNote2;

    @BeforeAll
    public static void setUpTest() throws MalformedURLException {
        subNote1 = new Note("Test sub-note 1", "Description for sub-note 1");
        subNote1.addElement(new MediaItem("I'm Your Captain", "Grand Funk", MediaItem.Category.SONG));
        subNote2 = new Note("Test sub-note 2", "Description for sub-note 2");
        subNote2.addElement(new Link("Google", "http://facebook.com"));
    }

    @Test
    public void createNoteWithEachElementType() throws MalformedURLException {
        Note testNote = new Note("Test Note 1", "Description for Test Note 1");

        testNote.addElement(new Contact("Contact1", "234-5678", "123 4th St.", "Menlo Park"));
        testNote.addElement(new MediaItem("Blind Alley", "Fanny", MediaItem.Category.SONG));
        testNote.addElement(new Price(5.00));
        testNote.addElement(new Link("Google", "http://google.com"));
        testNote.addElement(new EventInfo("Deadline", null, "2020-06-12 00:00:00"));
        System.out.println(testNote);
    }

    @Test
    public void createNoteWithSubNotes() {
        Note testNote = new Note("Test Note 2", "Description for Test Note 2");
        testNote.addNote(subNote1);
        testNote.addNote(subNote2);
        System.out.println(testNote);
    }

    // ToDo: Test adding duplicate notes?
}
