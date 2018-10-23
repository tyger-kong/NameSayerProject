package namesayer.backend.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import namesayer.backend.menus.MainMenu;

/**
 * Class for checking user if user inputs are names in the database. Also for changing strings into string arrays
 * to make it compatible with the custom ListView cells.
 */
public class NameChecker {
	
	/**
	 * Checks if there are multiple of the same name in the names database
	 * 
	 * @param name - Name to check for duplicates
	 * @return True if there are duplicates, false otherwise
	 */
	public static boolean checkNameDuplicates(String name) {
		int open = name.lastIndexOf("(");
		if(open != -1) {
			return (Collections.frequency(MainMenu.getListOfJustNames(), name.substring(0, open)) > 1) && MainMenu.getListOfAddedLower().contains(name);
		}
		return (Collections.frequency(MainMenu.getListOfJustNames(), name) > 1) && MainMenu.getListOfAddedLower().contains(name);
	}
	
	
	/**
	 * Checks if a name exists in the names database
	 * 
	 * @param name - Name to check for existence
	 * @return - True if the name exists, false otherwise
	 */
	public static boolean checkNameExists(String name) {
		return MainMenu.getListOfAddedLower().contains(name);
	}
	
	/**
	 * Converts string representation of a name into string array representation of that name (for putting into NameListCell). 
	 * Handles spaces and hyphens by placing them in separate array indices to the actual names
	 * 
	 * @param name - Name to convert to an array
	 * @return - The name as an array
	 */
	public static String[] nameAsArray(String name) {
		List<String> list = new ArrayList<String>();
		String singleName = "";
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == ' ') {
				if(!charCheck((char)singleName.charAt(0))) {
					singleName = (char)(singleName.charAt(0) - 32) + singleName.substring(1);
				}
				list.add(singleName);
				singleName = "";
				list.add(" ");
			} else if (name.charAt(i) == '-') {

				if(!charCheck((char)singleName.charAt(0))) {
					singleName = (char)(singleName.charAt(0) - 32) + singleName.substring(1);
				}
				list.add(singleName);
				singleName = "";
				list.add("-");
			} else {
				singleName += name.charAt(i);
			}
		}
		if(!charCheck((char)singleName.charAt(0))) {
			singleName = (char)(singleName.charAt(0) - 32) + singleName.substring(1);
		}
		list.add(singleName);
		return list.toArray(new String[0]);
	}

	/**
	 * Checks if the specified character is upper case nor not
	 * 
	 * @param c - Character to check
	 * @return - True if c is upper case, false otherwise
	 */
	private static boolean charCheck(char c) {
		if(Character.isUpperCase(c)) {
			return true;
		}
		return false;
	}
	
}
