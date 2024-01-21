package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class MainWindowController implements Initializable{
	
	@FXML
	private Button chooseFileButton;
	@FXML
	private Button CreateAndSendButton;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		CreateAndSendButton.setOnAction(event->{
			openWritingWindow();
		});
		
	}
	
	private void openWritingWindow() {
		FXMLLoader CreateFileLoader = new FXMLLoader(getClass().getResource("/CreateFileWindow.fxml"));
		try {
			Stage CreateFileStage = new Stage();
			Parent CreateFileRoot = CreateFileLoader.load();
			Scene CreateFileScene = new Scene(CreateFileRoot);
			
			CreateFileStage.setScene(CreateFileScene);
			CreateFileStage.setTitle("Send File");
			CreateFileStage.setMaximized(true);
			CreateFileStage.setResizable(true); 
			CreateFileStage.show();
			CreateFileStage.setOnHidden(event->{
//	        	controller.getChooseFileButton().setDisable(false);
	        });
			CreateFileStage.getScene().getAccelerators().put(KeyCombination.keyCombination("CTRL+W"), new Runnable() {
				@Override
				public void run() {
//					controller.getChooseFileButton().setDisable(false);
					CreateFileStage.close();
				}
			});
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public Button getChooseFileButton() {
		return chooseFileButton;
	}	
	public Button getCreateAndSendButton() {
		return CreateAndSendButton;
	}
}
