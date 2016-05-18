package sd.tp1.utils;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Antonio on 18/05/16.
 */
public class TestThisREPLICATION {

    public static void main(String[] args) {
        Gson gson = new Gson();
        ReplicationGJONResponse obj = new ReplicationGJONResponse();


        ReplicationGJONData replicationGJONData = new ReplicationGJONData();


        // 1. Java object to JSON, and save into a file
        try {
            gson.toJson(obj, new FileWriter("file.json"));


        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. Java object to JSON, and assign to a String
        String jsonInString = gson.toJson(obj);

        System.out.println(jsonInString);

    }
}

