package server.element;

/**
** Represents a contact (person or business entity).
*/
public class Contact extends NoteElement {
  private String phoneNumber;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private Integer postalCode;

  // No-arg constructor required by Hibernate
  protected Contact() {
    super(null, null);
    this.phoneNumber = null;
    this.address1 = null;
    this.city = null;
  }

  public Contact(String contactName, String newDesc, String newPhone, String addr1, String addr2,
                 String cityName, String stateName, Integer newCode) {
    super(contactName, newDesc);
    this.phoneNumber = newPhone;
    this.address1 = addr1;
    this.address2 = addr2;
    this.city = cityName;
    this.state = stateName;
    this.postalCode = newCode;
  }

  public Contact(String contactName, String newPhone, String addr1, String cityName) {
    this(contactName, null, newPhone, addr1, null, cityName, null, null);
  }

  public String getPhoneNumber() { return phoneNumber; }
  public void setPhoneNumber(String newPhone) { this.phoneNumber = newPhone; }

  public String getAddress1() { return address1; }
  public void setAddress1(String addr1) { this.address1 = addr1; }

  public String getAddress2() { return address2; }
  public void setAddress2(String addr2) { this.address2 = addr2; }

  public String getCity() { return city; }
  public void setCity(String newCity) { this.city = newCity; }

  public String getState() { return state; }
  public void setState(String newState) { this.state = newState; }

  public Integer getPostalCode() { return postalCode; }
  public void setPostalCode(Integer code) { this.postalCode = code; }

  @Override
  public String toString() {
    // ToDo: Could turn this into a toString() utility (takes a list of Strings)
    String result = "Contact:\n" + super.toString();
    String pc = postalCode == null ? null : postalCode.toString();
    for (String item : new String[] {phoneNumber, address1, address2, city, state, pc}) {
      if (item != null && !item.equals("")) {
        result = result.concat("\n" + item);
      }
    }
    return result;
  }
}
