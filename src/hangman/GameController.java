package hangman;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GameController {

	private final ExecutorService executorService;
	private final Game game;	
	
	public GameController(Game game) {
		this.game = game;
		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	@FXML
	private VBox board ;
	@FXML
	private Label statusLabel ;
    @FXML
    private Label triesLabel;
    @FXML
    private Label tmpAnswerLabel ;
	@FXML
	private Label enterALetterLabel ;
	@FXML
	private TextField textField ;

    public void initialize() throws IOException {
		System.out.println("in initialize");
		drawHangman();
		addTextBoxListener();
		addDrawingListener();
		setUpStatusLabelBindings();
	}

    private void addDrawingListener() {
        game.movesProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                drawProgress(newValue);
            }
        });
    }

    private void drawProgress(Number newValue) {

        int SPINE_START_X = 100;
        int SPINE_START_Y = 20;
        int SPINE_END_X = SPINE_START_X;
        int SPINE_END_Y = SPINE_START_Y + 50;

        switch (newValue.intValue()) {
            case 1:
                Circle head = new Circle(20);
                head.setTranslateX(10.0f);
                board.getChildren().add(head);
                break;
            case 2:
                Line spine = new Line();
                spine.setStartX(SPINE_START_X);
                spine.setStartY(SPINE_START_Y);
                spine.setEndX(SPINE_END_X);
                spine.setEndY(SPINE_END_Y);
                board.getChildren().add(spine);
                break;

            case 3:

                Line leftArm = new Line();
                leftArm.setStartX(SPINE_START_X);
                leftArm.setStartY(SPINE_START_Y);
                leftArm.setEndX(SPINE_START_X + 40);
                leftArm.setEndY(SPINE_START_Y - 10);
                leftArm.setTranslateX(-20);
                leftArm.setTranslateY(-40);
                board.getChildren().add(leftArm);
                break;

            case 4:
                Line rightArm = new Line();
                rightArm.setStartX(SPINE_START_X);
                rightArm.setStartY(SPINE_START_Y);
                rightArm.setEndX(SPINE_START_X - 40);
                rightArm.setEndY(SPINE_START_Y - 10);
                rightArm.setTranslateX(20);
                rightArm.setTranslateY(-52);
                board.getChildren().add(rightArm);

                break;

            case 5:
                Line leftLeg = new Line();
                leftLeg.setStartX(SPINE_END_X);
                leftLeg.setStartY(SPINE_END_Y);
                leftLeg.setEndX(SPINE_END_X + 25);
                leftLeg.setEndY(SPINE_END_Y + 50);
                leftLeg.setTranslateX(13);
                leftLeg.setTranslateY(-25);
                board.getChildren().add(leftLeg);
                break;

            case 6:
                Line rightLeg = new Line();
                rightLeg.setStartX(SPINE_END_X);
                rightLeg.setStartY(SPINE_END_Y);
                rightLeg.setEndX(SPINE_END_X - 25);
                rightLeg.setEndY(SPINE_END_Y + 50);
                rightLeg.setTranslateX(-13);
                rightLeg.setTranslateY(-77);
                board.getChildren().add(rightLeg);
        }

    }

    private void addTextBoxListener() {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				if(newValue.length() > 0) {
					System.out.print(newValue);
					game.makeMove(newValue);
					textField.clear();
				}
			}
		});
	}

	private void setUpStatusLabelBindings() {

		System.out.println("in setUpStatusLabelBindings");
		statusLabel.textProperty().bind(Bindings.format("%s", game.gameStatusProperty()));
        triesLabel.textProperty().bind(Bindings.format("Attempt %s/6", game.movesProperty()));
        tmpAnswerLabel.textProperty().bind(Bindings.format("%s", game.tmpAnswerProperty()));
//        tmpAnswerLabel.textProperty().bind(new StringBinding() {
//                                               @Override
//                                               protected String computeValue() {
//                                                   return game.getTmpAnswer();
//                                               }
//                                           });
		enterALetterLabel.textProperty().bind(Bindings.format("%s", "Enter a letter:"));
		/*	Bindings.when(
					game.currentPlayerProperty().isNotNull()
			).then(
				Bindings.format("To play: %s", game.currentPlayerProperty())
			).otherwise(
				""
			)
		);
		*/
	}

	private void drawHangman() {


        Line pole = new Line(0.0f, -50.0f, 0.0f, 150.0f);
        pole.setTranslateX(-75);
        pole.setTranslateY(150);
        pole.setStrokeWidth(5.0f);

        Line topBar = new Line(-25.0f, -50.0f, 50.0f, -50.0f);
        topBar.setTranslateX(-40);
//        topBar.setTranslateY(-50);
        topBar.setStrokeWidth(5.0f);

        Line theRope = new Line(0.0f, 50.0f, 0.0f, 125.0f);
        theRope.setTranslateX(0.0f);
//        theRope.setTranslateY(-50);
        theRope.setStrokeWidth(2.0f);

        board.getChildren().add(pole);
        board.getChildren().add(topBar);
        board.getChildren().add(theRope);


//        board.getChildren().addAll(head, spine, leftArm, rightArm, leftLeg, rightLeg);
//		Line line = new Line();
//		line.setStartX(25.0f);
//		line.setStartY(0.0f);
//		line.setEndX(25.0f);
//		line.setEndY(25.0f);
//
//		Line pole = new Line(-25.0f, -50.0f, -25.0f, 25.0f);
//		Line horizLine = new Line(-25.0f, 50.0f, 25.0f, 50.0f);
//
//		Circle c = new Circle();
//		c.setRadius(10);
//		c.setTranslateY(-25.0f);
//
//        board.getChildren().add(pole);
//        board.getChildren().add(horizLine);
////		board.getChildren().add(line);
//		board.getChildren().add(c);

	}
		
	@FXML 
	private void newHangman() {
        board.getChildren().clear();
        drawHangman();
		game.reset();
	}

	@FXML
	private void quit() {
		board.getScene().getWindow().hide();
	}

}