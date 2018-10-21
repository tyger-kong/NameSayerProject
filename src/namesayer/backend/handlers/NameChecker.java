package namesayer.backend.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import namesayer.backend.menus.MainMenu;

/**
 *  Class for checking user if user inputs are names in the database. Also for changing strings into string arrays
 *  to make it compatible with the custom ListView cells.
 *  
 * @author Dinith Wannigama
 */
public class NameChecker {
	
	public static boolean checkNameDuplicates(String name) {
		int open = name.lastIndexOf("(");
		if(open != -1) {
			System.out.println(name.substring(0, open));
			return Collections.frequency(MainMenu.getListOfJustNames(), name.substring(0, open)) > 1;
		}
		return Collections.frequency(MainMenu.getListOfJustNames(), name) > 1;
	}
	
	
	public static boolean checkNameExists(String name) {
		int open = name.lastIndexOf("(");
		if(open != -1) {
			System.out.println(name.substring(0, open));
			return MainMenu.getListOfJustNames().contains(name.substring(0, open));
		}
		return MainMenu.getListOfJustNames().contains(name);
	}
	
	// Converts string into string array (for putting into NameListCell)
	public static String[] nameAsArray(String name) {
		List<String> list = new ArrayList<String>();
		String singleName = "";
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == ' ') {
				if(!charCheck((char)singleName.charAt(0))){
					singleName = (char)(singleName.charAt(0) - 32) + singleName.substring(1);
				}
				list.add(singleName);
				singleName = "";
				list.add(" ");
			} else if (name.charAt(i) == '-') {

				if(!charCheck((char)singleName.charAt(0))){
					singleName = (char)(singleName.charAt(0) - 32) + singleName.substring(1);
				}
				list.add(singleName);
				singleName = "";
				list.add("-");
			} else {
				singleName += name.charAt(i);
			}
		}
		if(!charCheck((char)singleName.charAt(0))){
			singleName = (char)(singleName.charAt(0) - 32) + singleName.substring(1);
		}
		list.add(singleName);
		return list.toArray(new String[0]);
	}

	private static boolean charCheck(char c){
		if(Character.isUpperCase(c)){
			return true;
		}
		return false;
	}
	
}
