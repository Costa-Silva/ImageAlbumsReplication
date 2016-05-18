package sd.tp1.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Antonio on 18/05/16.
 */
public class ReplicationGJONData {
    private String replicaId;
    private List<String> knownHosts;
    private List<ReplicationGJONID> timestamps;

    public ReplicationGJONData() {
        knownHosts = new ArrayList<>();
        timestamps = new ArrayList<>();
        this.replicaId = UUID.randomUUID().toString();
    }


    public String getReplicaId() {
        return replicaId;
    }

    public void setReplicaId(String replicaId) {
        this.replicaId = replicaId;
    }

    public List<String> getKnownHosts() {
        return knownHosts;
    }

    public void setKnownHosts(List<String> knownHosts) {
        this.knownHosts = knownHosts;
    }

    public void addKnownHosts(String newHost){
        knownHosts.add(newHost);
    }

    public void removeKnownHosts(String oldHost){
        knownHosts.remove(oldHost);
    }

    public List<ReplicationGJONID> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<ReplicationGJONID> timestamps) {
        this.timestamps = timestamps;
    }


    public void addTimestamps(ReplicationGJONID timestamp){
        timestamps.add(timestamp);
    }

    public void removeTimestamps(ReplicationGJONID timestamp){
        timestamps.remove(timestamp);
    }
}
