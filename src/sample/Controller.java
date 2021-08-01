package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import javax.naming.Binding;

import static java.awt.Color.CYAN;
import static java.awt.Color.cyan;
import static java.awt.Color.red;

public class Controller {
    TicTacToeModel model = TicTacToeModel.getInstance();

    @FXML
    public Label playerX;
    @FXML
    public Label playerO;
    @FXML
    public Label caseLibre;
    @FXML
    public Label gameOver;
    @FXML
    public GridPane gameZone;

    public void initialize() {
        for (int i = 0; i < TicTacToeModel.BOARD_HEIGHT; i++) {
            for (int j = 0; j < TicTacToeModel.BOARD_WIDTH; j++) {
                TicTacToeSquare mySquare = new TicTacToeSquare(i, j);
                mySquare.setPrefHeight(100);
                gameZone.add(mySquare, j, i);
                mySquare.setOnMousePressed(event -> {
                    gameOver.textProperty().bind(model.getEndOfGameMessage());
                    gameOver.visibleProperty().bind(model.gameOver());
                });
            }

        }
        playerX.textProperty().bind(model.getScore(Owner.FIRST).asString().concat(new SimpleStringProperty(" case pour X")));
        playerO.textProperty().bind(model.getScore(Owner.SECOND).asString().concat(new SimpleStringProperty(" case pour O")));
        playerX.styleProperty().bind(Bindings.when(model.turnProperty().isEqualTo(Owner.FIRST)).then("-fx-background-color : cyan").otherwise("-fx-background-color : red"));
        playerO.styleProperty().bind(Bindings.when(model.turnProperty().isEqualTo(Owner.FIRST)).then("-fx-background-color : red").otherwise("-fx-background-color : cyan"));
        caseLibre.textProperty().bind(model.getScore(Owner.NONE).asString().concat(new SimpleStringProperty(" case libre")));
        gameOver.textProperty().bind(model.getEndOfGameMessage());
        gameOver.visibleProperty().bind(model.gameOver());
    }

    @FXML
    public void restart() {
        model.restart();
        gameOver.visibleProperty().bind(model.gameOver());
        gameOver.textProperty().bind(model.getEndOfGameMessage());
    }
}
