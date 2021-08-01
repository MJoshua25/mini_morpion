package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

public class TicTacToeSquare extends TextField {

    private static TicTacToeModel model = TicTacToeModel.getInstance();

    private ObjectProperty<Owner> ownerProperty =
            new SimpleObjectProperty<>(Owner.NONE);

    private BooleanProperty winnerProperty =
            new SimpleBooleanProperty(false);

    public ObjectProperty<Owner> ownerProperty() {
        return ownerProperty;
    }

    public BooleanProperty WinnerProperty() {
        return winnerProperty;
    }

    public TicTacToeSquare(final int row, final int column) {
        //Background b=this.getBackground();
        ownerProperty.bind(model.getSquare(row, column));
        this.textProperty().bind(Bindings.when(ownerProperty.isEqualTo(Owner.FIRST)).then("X").otherwise(Bindings.when(ownerProperty.isEqualTo(Owner.SECOND)).then("O").otherwise("")));
        this.setEditable(false);
        setOnMouseClicked(event ->
        {
            if (this.ownerProperty.getValue() == Owner.NONE) {
                model.play(row, column);
            }
        });

        setOnMouseEntered(event -> {

            if (this.ownerProperty.getValue() == Owner.NONE) {
                this.setStyle("-fx-background-color:green");

            } else {
                this.setStyle("-fx-background-color:red");

            }
        });

        setOnMouseExited(event -> {
            this.setStyle("fx-background-color:white");
        });

    }
}
