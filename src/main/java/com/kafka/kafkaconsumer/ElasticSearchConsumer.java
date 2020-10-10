package com.kafka.kafkaconsumer;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ElasticSearchConsumer {

    public static RestHighLevelClient createClient() {
        //https://njvllmugw5:rbl721x67q@kafka-course-4087171591.us-east-1.bonsaisearch.net:443
        String hostname = "kafka-course-4087171591.us-east-1.bonsaisearch.net";
        String username = "njvllmugw5";
        String password = "rbl721x67q";

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username,password));

        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost(hostname,443,"https")
        ).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback(){

            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });

        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
        return client;
    }

    public static KafkaConsumer<String,String> createConsumer(String topic){
        //create producer properties
        Properties properties = new Properties();
        String bootstrapsevers = "127.0.0.1:9092";
        String groupId= "kafka-demo-elasticsearch";
        //String topic = "test-topic";

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapsevers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        //create consumer
        KafkaConsumer<String,String> consumer= new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));
        return  consumer;
    }

    public static void main(String[] args) throws IOException {
        final Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class);

        RestHighLevelClient client = createClient();
        KafkaConsumer<String,String> consumer = createConsumer("twitter_tweets");

        while(true){
            ConsumerRecords<String,String> records =
                    consumer.poll(Duration.ofMillis(100));
            
            for (ConsumerRecord<String,String> record : records){
                logger.info(record.value());
                IndexRequest indexRequest = new IndexRequest(
                        "twitter",
                        "tweets"
                ).source(record.value(), XContentType.JSON);

                IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
                String id = indexResponse.getId();
                logger.info(id);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }

    }
}
