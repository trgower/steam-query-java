package steam.servers;

public class Player {

    private byte index;
    private String name;
    private Long score;
    private float duration;

    public Player(byte index, String name, Long score, float duration) {
        this.index = index;
        this.name = name;
        this.score = score;
        this.duration = duration;
    }

    public byte getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Long getScore() {
        return score;
    }

    public float getDuration() {
        return duration;
    }
}
