package server.element;

import java.time.LocalDateTime;

/**
 * Represents an event date or deadline with completion status.
 */
public class EventInfo extends NoteElement {
    LocalDateTime eventDate;
    CompletionStatus status;

    enum CompletionStatus { COMPLETED, CANCELED, MISSED }

    // No-arg constructor required by Hibernate
    protected EventInfo() {
        super(null, null);
        this.eventDate = null;
    }

    public EventInfo(String title, String description, LocalDateTime date) {
        super(title, description);
        this.eventDate = date;
    }

    public LocalDateTime getDate() { return eventDate; }
    public void setDate(LocalDateTime newDate) { this.eventDate = newDate; }

    public CompletionStatus getStatus() { return status; }
    public void setStatus(CompletionStatus newStatus) { this.status = newStatus; }
}
