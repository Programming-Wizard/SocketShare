package application;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class labelDisplayer {
	public void displayLabel(Label label, Text crossMark,String labelText) {
		label.setText(labelText);
		FadeTransition labelFade = new FadeTransition();
		labelFade.setDuration(Duration.millis(2000));
		labelFade.setRate(1.2);
		labelFade.setFromValue(0);
		labelFade.setToValue(10);
		labelFade.setCycleCount(2);
		labelFade.setAutoReverse(true);	
		labelFade.setNode(label);
		labelFade.play();
		FadeTransition markFade = new FadeTransition();
		markFade.setDuration(Duration.millis(2000));
		markFade.setRate(1.2);
		markFade.setFromValue(0);
		markFade.setToValue(10);
		markFade.setCycleCount(2);
		markFade.setAutoReverse(true);	
		markFade.setNode(crossMark);
		markFade.play();
	}
}
