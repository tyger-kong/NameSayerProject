package namesayer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied list of entries.
 * @author Caleb Brinkman
 * Taken from: https://gist.github.com/floralvikings/10290131
 */
public class AutoCompleteTextField extends TextField {
	/** The existing autocomplete entries. */
	private final SortedSet<String> entries;

	/** The popup used to select an entry. */
	private ContextMenu entriesPopup;


	/** Construct a new AutoCompleteTextField. */
	public AutoCompleteTextField() {
		super();
		entries = new TreeSet<>();
		entriesPopup = new ContextMenu();

		textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
				if (getText() != null) {
					String txt = null;
					// Check if there are spaces or hyphens before the name (when typed by user)
					if ((getText().lastIndexOf(" ") != -1) || (getText().lastIndexOf("-") != -1)) {
						txt = (getText().lastIndexOf(" ") > getText().lastIndexOf("-")) 
								? getText().substring(getText().lastIndexOf(" ")+1, getText().length()) 
										: getText().substring(getText().lastIndexOf("-")+1, getText().length());
					} else {
						txt = getText();
					}
					

					if (txt.length() == 0) {
						entriesPopup.hide();
					} else {
						LinkedList<String> searchResult = new LinkedList<>();
						searchResult.addAll(entries.subSet(txt, txt + Character.MAX_VALUE));
						if (entries.size() > 0) {
							populatePopup(searchResult);
							if (!entriesPopup.isShowing()) {
								entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
							}
						} else {
							entriesPopup.hide();
						}
					}
				}
			}
		});

		focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
				entriesPopup.hide();
			}
		});

	}


	/**
	 * Get the existing set of autocomplete entries.
	 * @return The existing autocomplete entries.
	 */
	public SortedSet<String> getEntries() {
		return entries; 
	}


	/**
	 * Populate the entry set with the given search results.  Display is limited to maxEntries entries, for performance.
	 * @param searchResult The set of matching strings.
	 */
	private void populatePopup(List<String> searchResult) {
		List<CustomMenuItem> menuItems = new LinkedList<>();
		int maxEntries = 15;
		int count = Math.min(searchResult.size(), maxEntries);
		for (int i = 0; i < count; i++) {
			final String result = searchResult.get(i);
			Label entryLabel = new Label(result);
			CustomMenuItem item = new CustomMenuItem(entryLabel, true);

			item.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {

					String prevWords;
					if ((getText().lastIndexOf(" ") != -1) || (getText().lastIndexOf("-") != -1)) {
						prevWords = (getText().lastIndexOf(" ") > getText().lastIndexOf("-")) 
								? getText().substring(0, getText().lastIndexOf(" ")+1) 
										: getText().substring(0, getText().lastIndexOf("-")+1);
					} else {
						prevWords = "";
					}

					setText(prevWords + result);
					positionCaret((prevWords+result).length());
					entriesPopup.hide();
				}
			});
			menuItems.add(item);
		}
		entriesPopup.getItems().clear();
		entriesPopup.getItems().addAll(menuItems);
	}
	
	
	@Override
	public void replaceText(int start, int end, String text) {
		if (text.matches("[a-zA-Z -]")) {
			super.replaceText(start, end, text);
		}
	}
	
	
	@Override
	public void replaceSelection(String text) {
		if (text.matches("[a-zA-Z -]")) {
			super.replaceSelection(text);
		}
	}

}