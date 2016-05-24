package sd.tp1.server;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Created by paulo on 21/05/2016.
 */
public class KafkaPublisher {

    private KafkaProducer<String,String> producer;

    public KafkaPublisher(){
        Properties env = System.getProperties();
        Properties props = new Properties();
        
        props.put("zk.connect", env.getOrDefault("zk.connect", "192.168.43.25:2181/"));
        props.put("bootstrap.servers", env.getOrDefault("bootstrap.servers", "192.168.43.25:9092"));
        props.put("log.retention.ms", 1000);

        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    public void publishEvent(String topic, String event){
        ProducerRecord<String,String> data = new ProducerRecord<>(topic,event);
        System.out.println("Publishing at " + topic + " event: " + event);
        producer.send(data);
    }
}
