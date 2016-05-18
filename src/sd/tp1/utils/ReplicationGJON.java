package sd.tp1.utils;

import java.util.List;
import java.util.Map;

/**
 * Created by Antonio on 18/05/16.
 */
public class ReplicationGJON {
/*
    public static final String REPLICAID= "replicaId";
    public static final String TIMESTAMP= "timestamps";
    public static final String FILENAME= "metadata.txt";
    public static final String DATA= "data";
    public static final String CLOCK= "clock";
    public static final String REPLICA= "replica";
    public static final String SHAREDBY= "sharedBy";
    public static final String KNOWNHOSTS= "knownHosts";
    public static final String OBJECTID= "id";
    public static final String OPERATION ="operation";
 */



    private String replicaId;

    private Map<String,List<List<String>>> timestamps;

    private String clock;
    private String replica;

    private String[] sharedBy;


}
