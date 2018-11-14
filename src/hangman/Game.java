package hangman;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Game {

	private String answer;
//	private String tmpAnswer;
//	private final ReadOnlyStringWrapper tmpAnswer;
	private StringProperty tmpAnswer;
	private StringProperty triesLeft;
	private String missedLetters;
	private String[] letterAndPosArray;
	private String[] words;
//	private int moves;
	private IntegerProperty moves;
	private int index;
	private final ReadOnlyObjectWrapper<GameStatus> gameStatus;
	private ObjectProperty<Boolean> gameState = new ReadOnlyObjectWrapper<Boolean>();


    public enum GameStatus {
		GAME_OVER {
			@Override
			public String toString() {
				return "Game over!";
			}
		},
		BAD_GUESS {
			@Override
			public String toString() { return "Bad guess..."; }
		},
		GOOD_GUESS {
			@Override
			public String toString() {
				return "Good guess!";
			}
		},
		WON {
			@Override
			public String toString() {
				return "You won!";
			}
		},
		OPEN {
			@Override
			public String toString() {
				return "Game on, let's go!";
			}
		}
	}

	public Game() {
		gameStatus = new ReadOnlyObjectWrapper<GameStatus>(this, "gameStatus", GameStatus.OPEN);
		gameStatus.addListener(new ChangeListener<GameStatus>() {
			@Override
			public void changed(ObservableValue<? extends GameStatus> observable,
								GameStatus oldValue, GameStatus newValue) {
				if (gameStatus.get() != GameStatus.OPEN) {
					log("in Game: in changed");
					//currentPlayer.set(null);
				}
			}

		});



		tmpAnswer = new SimpleStringProperty(this, "tmpAnswer");
//		tmpAnswer = new ReadOnlyStringWrapper(this, "tmpAnswer", "");
//		tmpAnswer = new SimpleStringProperty("a");

		tmpAnswer.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                log("AAA"+newValue);
            }
        });

		moves = new SimpleIntegerProperty();

		init();

//		setRandomWord();
//		prepTmpAnswer();
//		prepLetterAndPosArray();
//		missedLetters = ""; //TODO: move to init so we can reset
////		moves = 0;
//        moves.setValue(0);
//        tmpAnswer.setValue("");
//		gameState.setValue(false); // initial state
//		createGameStatusBinding();
	}

    private void init() {
        setRandomWord();
        prepTmpAnswer();
        prepLetterAndPosArray();
        missedLetters = ""; //TODO: move to init so we can reset
//		moves = 0;
        moves.setValue(0);

//        tmpAnswer.setValue("");
        gameState.setValue(false); // initial state
        createGameStatusBinding();

    }

    private void createGameStatusBinding() {
		List<Observable> allObservableThings = new ArrayList<>();
		ObjectBinding<GameStatus> gameStatusBinding = new ObjectBinding<GameStatus>() {
			{
				super.bind(gameState);
			}
			@Override
			public GameStatus computeValue() {
				log("in computeValue");
				GameStatus check = checkForWinner(index);
				if(check != null ) {
					return check;
				}

				if(tmpAnswer.getValue().trim().length() == 0 && missedLetters.isEmpty()){
					log("new game");
					return GameStatus.OPEN;
				}
				else if (index != -1){
					log("good guess");
					return GameStatus.GOOD_GUESS;
				}
				else {
					moves.setValue(moves.getValue() + 1);
					log("bad guess");
					return GameStatus.BAD_GUESS;
					//printHangman();
				}
			}
		};
		gameStatus.bind(gameStatusBinding);
	}

	public ReadOnlyObjectProperty<GameStatus> gameStatusProperty() {
		return gameStatus.getReadOnlyProperty();
	}
	public GameStatus getGameStatus() {
		return gameStatus.get();
	}

    public StringProperty tmpAnswerProperty() {
        return tmpAnswer;
    }


	public String getTmpAnswer() {
	    log("AICI:" + tmpAnswer.getValue());
	    return tmpAnswer.get();
	}

	public void setTmpAnswer(String s) {
	    tmpAnswer.setValue(s);
    }

    public IntegerProperty movesProperty() {
	    return moves;
    }

	private void setRandomWord() {
		//int idx = (int) (Math.random() * words.length);
//		answer = "apple";//words[idx].trim(); // remove new line character
        LineNumberReader reader = null;
        String relPathToWords = "words.txt";
        File f = new File(relPathToWords);
        int lines = 0;
        try {
            reader = new LineNumberReader(new FileReader(f));
            while ((reader.readLine()) != null);
            lines = reader.getLineNumber();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lines != 0) {
            Random rn = new Random();
            int ran = rn.nextInt(lines + 1);

            try (Stream<String> liness = Files.lines(Paths.get(relPathToWords))) {
                answer = liness.skip(ran).findFirst().get().trim();
                log("NEW WORD: "+ answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        StringBuilder a = new StringBuilder(answer.replaceAll(".", "_"));
//        a.replace(0, a.length(), "_");
        setTmpAnswer(a.toString());
        log("b");
	}

	private void prepTmpAnswer() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < answer.length(); i++) {
			sb.append("_");
		}
//		tmpAnswer = sb.toString();
		tmpAnswer.setValue(sb.toString());
	}

	private void prepLetterAndPosArray() {
		letterAndPosArray = new String[answer.length()];
		for(int i = 0; i < answer.length(); i++) {
			letterAndPosArray[i] = answer.substring(i,i+1);
		}
	}

	private int getValidIndex(String input) {
		int index = -1;
		for(int i = 0; i < letterAndPosArray.length; i++) {
			if(letterAndPosArray[i].equals(input)) {
				index = i;
				letterAndPosArray[i] = "";
				break;
			}
		}
		if (index == -1)
			missedLetters += input;
		return index;
	}

	private int update(String input) {
		int index = getValidIndex(input);
		if(index != -1) {
//			StringBuilder sb = new StringBuilder(answer.replaceAll(".", "_"));
            String t = tmpAnswer.get();
            StringBuilder sb = new StringBuilder(t);
//            StringBuilder sb = new StringBuilder(tmpAnswer.get().replaceAll("."+input+".", "_"));
//            for(int i = 0; i < answer.length(); i++) {
//                sb.append("_");
//            }
			sb.setCharAt(index, input.charAt(0));
			tmpAnswer.setValue(sb.toString());
		}
		return index;
	}

	private static void drawHangmanFrame() {}

	public void makeMove(String letter) {
		log("\nin makeMove: " + letter);
		index = update(letter);
		// this will toggle the state of the game
		gameState.setValue(!gameState.getValue());
	}

	public void reset() {
	    init();
    }

	private int numOfTries() {
		return 6; // TODO, fix me
	}

	public static void log(String s) {
		System.out.println(s);
	}

	private GameStatus checkForWinner(int status) {
		log("in checkForWinner");
		if(tmpAnswer.getValue().equals(answer)) {
			log("won");
			return GameStatus.WON;
		}
		else if(moves.getValue() == numOfTries()) {
			log("game over");
			return GameStatus.GAME_OVER;
		}
		else {
			return null;
		}
	}
}
