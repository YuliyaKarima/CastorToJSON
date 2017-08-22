
import org.exolab.castor.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;

import java.util.*;

public class UsingCastor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// -- create a person to work with
			Person person = new Person("Bob Harris", "125 Foo Street", "222-222-2222", "bob@harris.org",
					"(123) 123-1234", "(123) 123-1234", 34);
			// -- marshal the person object out as a <person>
			FileWriter file = new FileWriter("D:/person_json.json");
			CastorToJSON.marshal(person, file);
			file.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
