package com.eventdriven.kafka;

import com.eventdriven.kafka.model.Event;
import com.kafka.kafkaconsumer.ElasticSearchConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final Logger logger = LoggerFactory.getLogger(Main.class);
        EventGenerator eventGenerator = new EventGenerator();

        Properties properties = new Properties();
        String bootstrapsevers = "127.0.0.1:9092";
        //String groupId= "kafka-demo-elasticsearch";
        //String topic = "test-topic";

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapsevers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
       // properties.setProperty(ProducerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        Producer<String,String> producer = new KafkaProducer<String, String>(properties);

        for ( int i = 0 ; i <= 10 ; i++){
            logger.info("Generating event # " + i);

            Event event = eventGenerator.generateEvent();

            String key = extractKey(event);
            String value = extractValue(event);

            ProducerRecord<String,String> record = new ProducerRecord<>("user_tracking",key,value);

            logger.info("record " + key + " :" + value);
            producer.send(record);

            sleep(1000);
        }

    }

    private static String extractValue(Event event) {
        return String.format("%s %s %s",event.getProduct().getDesignType(),event.getProduct().getColor(),event.getProduct().getDesignType());
    }

    private static String extractKey(Event event) {
        return  event.getUser().getUserId().toString();
    }
}
