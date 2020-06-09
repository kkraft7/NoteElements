package server.element;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents an item price.
 */
public class Price extends NoteElement {
    BigDecimal price;

    // No-arg constructor required by Hibernate
    protected Price() {
        super(null, null);
        this.price = null;
    }

    public Price(String title, String description, BigDecimal itemPrice) {
        super(title, description);
        this.price = itemPrice.setScale(2, RoundingMode.UNNECESSARY);
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal newPrice) { this.price = newPrice; }
}
