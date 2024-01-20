package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;



public class MainWindowController implements Initializable{
	
	@FXML
		private Button chooseFileButton;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	public Button getChooseFileButton() {
		return chooseFileButton;
	}	
}
