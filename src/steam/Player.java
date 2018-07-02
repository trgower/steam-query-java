package steam;

public class Player {

    private byte index;
    private String name;
    private int score;
    private float duration;

    public Player(byte index, String name, int score, float duration) {
        this.index = index;
        this.name = name;
        this.score = score;
        this.duration = duration;

        if (this.name.length() == 0) {
            this.name = "Unknown";
        }
    }

    public byte getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public float getDuration() {
        return duration;
    }
}
