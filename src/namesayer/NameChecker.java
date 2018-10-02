package namesayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class for checking user if user inputs are names in the database. Also for changing strings into string arrays
// to make it compatible with the custom ListView cells.
public class NameChecker {
	
	public static boolean checkNameDuplicates(String name) {
		return Collections.frequency(MainMenu.getAddedList(), name) > 1;
	}
	
	
	public static boolean checkNameExists(String name) {
		return MainMenu.getAddedList().contains(name);
	}
	
	
	public static String[] nameAsArray(String name) {
		List<String> list = new ArrayList<String>();
		String singleName = "";
		for (int i = 0; i < name.length(); i++) {
			if ((name.charAt(i) == ' ') || (name.charAt(i) == '-')) {
				list.add(singleName);
				singleName = "";
				list.add(Character.toString(name.charAt(i)));
			} else {
				singleName += name.charAt(i);
			}
		}
		list.add(singleName);
		return list.toArray(new String[0]);
	}
	
}
