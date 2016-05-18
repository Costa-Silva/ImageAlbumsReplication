package sd.tp1.utils;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Antonio on 18/05/16.
 */
public class TestThisREPLICATION {

    public static void main(String[] args) {
        Gson gson = new Gson();
        ReplicationGJONResponse obj = new ReplicationGJONResponse();


        ReplicationGJONData replicationGJONData = new ReplicationGJONData();
        ReplicationGJONID replicationGJONID = new ReplicationGJONID();

        replicationGJONID.setId("1");
        replicationGJONID.setClock(2);
        replicationGJONID.setReplica("replica14");
        replicationGJONID.addSharedBy("168");




        replicationGJONData.addTimestamps(replicationGJONID);

        obj.setData(replicationGJONData);





        // 1. Java object to JSON, and save into a file
        try {
            PrintStream out = new PrintStream(new FileOutputStream("file.txt"));
            out.println(gson.toJson(obj));

            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. Java object to JSON, and assign to a String
        String jsonInString = gson.toJson(obj);

        System.out.println(jsonInString);

    }
}

