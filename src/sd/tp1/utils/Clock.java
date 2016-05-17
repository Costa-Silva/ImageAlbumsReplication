package sd.tp1.utils;

/**
 * Created by Antonio on 15/05/16.
 */
public class Clock {

    private int clock;
    private String replica;

    public Clock(int clock, String replica) {
        this.clock = clock;
        this.replica = replica;
    }

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public String getReplica() {
        return replica;
    }

    public void setReplica(String replica) {
        this.replica = replica;
    }
}
