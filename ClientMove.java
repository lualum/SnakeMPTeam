import java.io.Serializable;

public class ClientMove implements Serializable {
    int turn;
    int id;

    public ClientMove(int turn, int id) {
        this.turn = turn;
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public int getId() {
        return id;
    }
}
