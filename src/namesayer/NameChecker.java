package namesayer;

import java.util.Collections;

public class NameChecker {
	
	public static boolean checkNameDuplicates(String name) {
		return Collections.frequency(MainMenu.getAddedList(), name) > 1;
	}
	
	
	public static boolean checkNameExists(String name) {
		return MainMenu.getAddedList().contains(name);
	}
	
}
