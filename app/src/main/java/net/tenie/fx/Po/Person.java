package net.tenie.fx.Po;

import javafx.beans.property.SimpleStringProperty;

/*   @author tenie */
public class Person {

	private final SimpleStringProperty ids;
	private final SimpleStringProperty firstName;
	private final SimpleStringProperty lastName;
	private final SimpleStringProperty email;

	public Person(String fid, String fName, String lName, String email) {
		this.ids = new SimpleStringProperty(fid);
		this.firstName = new SimpleStringProperty(fName);
		this.lastName = new SimpleStringProperty(lName);
		this.email = new SimpleStringProperty(email);
	}

	public String getIds() {
		return ids.get();
	}

	public void setIds(String id) {
		ids.set(id);
	}

	public String getFirstName() {
		return firstName.get();
	}

	public void setFirstName(String fName) {
		firstName.set(fName);
	}

	public String getLastName() {
		return lastName.get();
	}

	public void setLastName(String fName) {
		lastName.set(fName);
	}

	public String getEmail() {
		return email.get();
	}

	public void setEmail(String fName) {
		email.set(fName);
	}
}
