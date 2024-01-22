package application;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class CSSelectWindowController implements Initializable {
	@FXML
	private Text CloseButton;
	@FXML
	private TextField ipText;
	@FXML
	private Button selectIpButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
//		selectIpButton.setOnAction(event->{
//		});
	}

	public Text getCloseButton() {
		return CloseButton;
	}

	public TextField getIpText() {
		return ipText;
	}

	public Button getSelectIpButton() {
		return selectIpButton;
	}

}
