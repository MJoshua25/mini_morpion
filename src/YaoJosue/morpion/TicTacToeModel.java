package YaoJosue.morpion;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;

public class TicTacToeModel {
    /**
     * Taille du plateau de jeu (pour être extensible).
     */
    protected final static int BOARD_WIDTH = 3;
    protected final static int BOARD_HEIGHT = 3;
    /**
     * Nombre de pièces alignés pour gagner (idem).
     */
    private final static int WINNING_COUNT = 3;
    /**
     * Joueur courant
     */
    private final ObjectProperty<Owner> turn = new SimpleObjectProperty<>(Owner.FIRST);
    /**
     * Vainqueur du jeu, NONE si pas de vainqueur.
     */
    private final ObjectProperty<Owner> winner = new SimpleObjectProperty<>(Owner.NONE);
    /**
     * Plateau de jeu.
     */
    private final ObjectProperty<Owner>[][] board;
    /**
     * Positions gagnantes.
     */
    private final BooleanProperty[][] winningBoard;

    /**
     * Constructeur privé.
     */
    private TicTacToeModel() {

        winningBoard = new BooleanProperty[BOARD_HEIGHT][BOARD_WIDTH];
        board = new ObjectProperty[BOARD_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
                winningBoard[i][j] = new SimpleBooleanProperty(false);
            }
        }
    }

    /**
     * @return la seule instance possible du jeu.
     */
    public static TicTacToeModel getInstance() {
        return TicTacToeModelHolder.INSTANCE;
    }

    /**
     * Classe interne selon le pattern singleton.
     */
    TicTacToeModel instance = TicTacToeModel.getInstance();

    private static class TicTacToeModelHolder {
        private static final TicTacToeModel INSTANCE = new TicTacToeModel();
    }

    public void restart() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j].setValue(Owner.NONE);
                winningBoard[i][j].setValue(false);
            }

        }
        winner.setValue(Owner.NONE);
        turn.setValue(Owner.FIRST);


    }

    public final ObjectProperty<Owner> turnProperty() {
        return turn;
    }

    public final ObjectProperty<Owner> getSquare(int row, int column) {
        return board[row][column];
    }

    public final BooleanProperty getWinningSquare(int row, int column) {
        return null;
    }

    /**
     * Cette fonction ne doit donner le bon résultat que si le jeu
     * est terminé. L’affichage peut être caché avant la fin du jeu.
     *
     * @return résultat du jeu sous forme de texte
     */
    public final StringExpression getEndOfGameMessage() {
        String endOfGameMessage;
        switch (winner.getValue()) {
            case FIRST:
                endOfGameMessage = "GameOver . le gagnant est le joueur 1";
                break;
            case SECOND:
                endOfGameMessage = "GameOver . le gagnant est le joueur 2";
                break;
            default:
                endOfGameMessage = "Game Over: Match Nul";
                break;
        }
        return new SimpleStringProperty(endOfGameMessage);
    }

    public void setWinner(Owner winner) {
        this.winner.setValue(winner);
    }

    public boolean validSquare(int row, int column) {
        return board[row][column].getValue() == Owner.NONE;
    }

    public void nextPlayer() {
        turn.set(turn.getValue().opposite());
    }

    /**
     * Jouer dans la case (row, column) quand c’est possible.
     */
    public void play(int row, int column) {


        if (!Boolean.TRUE.equals(legalMove(row, column).getValue())) {
            return;
        }

        board[row][column].setValue(turn.getValue());

        if (colonneWin(column) || diagWin(row, column) || rowWin(row)) {
            setWinner(turn.getValue());

        } else {
            this.nextPlayer();
        }


    }

    /**
     * verification des conditions de victoire
     * on procede par variation  des colonne par ligne si 3 elements sont aligné alors la victoire est possible
     *
     * @return true si c'est le cas
     */
    public boolean colonneWin(int column) {
        boolean result = true;

        for (int i = 0; i < WINNING_COUNT; i++) {

            if (board[i][column].getValue() != turn.getValue()) {
                result = false;
                break;
            }
        }
        return result;
    }


    /**
     * verification des conditions de victoire
     * on procede par variation  des lignes par colonne si 3 elements sont aligné alors la victoire est possible
     *
     * @return true si c'est le cas
     */


    public boolean rowWin(int row) {
        boolean result = true;

        for (int j = 0; j < WINNING_COUNT; j++) {

            if (board[row][j].getValue() != turn.getValue()) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Condition de victoire selon les diagonales
     * Surles deux diagonales il y a ou non les pions d'un meme joueur allignés.
     * alors on retourne vraie
     *
     * @return true si c'est le cas
     */

    public boolean diagWin(int row, int column) {

        return isFirstDiagWin() || isSecondDiagWin();

    }

    private boolean isSecondDiagWin() {
        return board[0][2].getValue() == turn.getValue() && board[1][1].getValue() == turn.getValue() && board[2][0].getValue() == turn.getValue();
    }

    private boolean isFirstDiagWin() {
        return board[0][0].getValue() == turn.getValue() && board[1][1].getValue() == turn.getValue() && board[2][2].getValue() == turn.getValue();
    }

    /**
     * @return true s’il est possible de jouer dans la case
     * c’est-à-dire la case est libre et le jeu n’est pas terminé
     */
    public BooleanBinding legalMove(int row, int column) {

        return new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                return (!gameOver().getValue()) && (board[row][column].getValue() == Owner.NONE);
            }
        };
    }


    public NumberExpression getScore(Owner owner) {
        NumberExpression score = new SimpleIntegerProperty();
        int column;
        int row;
        for (row = 0; row < BOARD_HEIGHT; row++) {
            for (column = 0; column < BOARD_WIDTH; column++) {
                score = score.add(Bindings.when(board[row][column].isEqualTo(owner)).then(1).otherwise(0));
            }
        }
        return score;
    }

    /**
     * @return true si le jeu est terminé
     * (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
     */
    public BooleanBinding gameOver() {
        return new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                boolean resultat = true;
                if (winner.getValue() != Owner.NONE) {
                    return true;
                }
                for (int i = 0; i <= BOARD_HEIGHT - 1; i++) {
                    for (int j = 0; j <= BOARD_WIDTH - 1; j++) {
                        if (board[i][j].getValue() == Owner.NONE && winner.getValue() == Owner.NONE) {
                            resultat = false;
                            break;
                        }
                    }
                }
                return resultat;
            }
        };
    }


}