package sd.tp1.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio on 18/05/16.
 */
public class ReplicationGJONID {
    private String id;
    private int clock;
    private String replica;
    private String operation;
    private List<String> sharedBy;

    public ReplicationGJONID(){
        sharedBy = new ArrayList<>();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(List<String> sharedBy) {
        this.sharedBy = sharedBy;
    }

    public void addSharedBy(String newSharedby) {
        sharedBy.add(newSharedby);
    }

    public void removeSharedBy(String oldSharedby){
        sharedBy.remove(oldSharedby);
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

}
