package sd.tp1.utils;

/**
 * Created by Antonio on 15/05/16.
 */
public class Clock {

    private int clock;
    private int replica;

    public Clock(int clock, int replica) {
        this.clock = clock;
        this.replica = replica;
    }

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public int getReplica() {
        return replica;
    }

    public void setReplica(int replica) {
        this.replica = replica;
    }
}
