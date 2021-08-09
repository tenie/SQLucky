package net.tenie.fx.test.control;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * 动画demo
 * 
 * @author tenie
 *
 */
public class TimelineEvents extends Application {

	private Timeline timeline;
	private AnimationTimer timer;
	private Integer i = 0;

	@Override
	public void start(Stage stage) {
		Group p = new Group();
		Scene scene = new Scene(p);
		stage.setScene(scene);
		stage.setWidth(500);
		stage.setHeight(500);
		p.setTranslateX(80);
		p.setTranslateY(80);

		// create a circle with effect
		final Circle circle = new Circle(20, Color.rgb(156, 216, 255));
		circle.setEffect(new Lighting());
		// create a text inside a circle
		final Text text = new Text(i.toString());
		text.setStroke(Color.BLACK);
		// create a layout for circle with text inside
		final StackPane stack = new StackPane();
		stack.getChildren().addAll(circle, text);
		stack.setLayoutX(30);
		stack.setLayoutY(30);

		p.getChildren().add(stack);
		stage.show();

		// create a timeline for moving the circle
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);

		//You can add a specific action when each frame is started.
		timer = new AnimationTimer() {
			@Override
			public void handle(long l) {
				text.setText(i.toString());
				i++;
			}

		};

		// create a keyValue with factory: scaling the circle 2times
		KeyValue keyValueX = new KeyValue(stack.scaleXProperty(), 2);
		KeyValue keyValueY = new KeyValue(stack.scaleYProperty(), 2);

		// create a keyFrame, the keyValue is reached at time 2s
		Duration duration = Duration.millis(2000);
		// one can add a specific action when the keyframe is reached
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				stack.setTranslateX(java.lang.Math.random() * 200 - 100);
				// reset counter
				i = 0;
			}
		};

		KeyFrame keyFrame = new KeyFrame(duration, onFinished, keyValueX, keyValueY);

		// add the keyframe to the timeline
		timeline.getKeyFrames().add(keyFrame);

		timeline.play();
		timer.start();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
