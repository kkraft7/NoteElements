package server;

import org.junit.jupiter.api.Test;
import server.element.*;

import java.net.MalformedURLException;

public class NoteTest {

    @Test
    void createNoteWithEachElementType() throws MalformedURLException {
        Note testNote = new Note("Test Note 1", "Description for Test Note 1");

        testNote.addElement(new Contact("Contact1", "234-5678", "123 4th St.", "Menlo Park"));
        testNote.addElement(new MediaItem("Blind Alley", "Fanny", MediaItem.Category.SONG));
        testNote.addElement(new Price(5.00));
        testNote.addElement(new Link("Google", "http://google.com"));
        testNote.addElement(new EventInfo("Deadline", null, "2020-06-12 00:00:00"));
        System.out.println(testNote);
    }

    // ToDo: Test note with sub-notes
}
