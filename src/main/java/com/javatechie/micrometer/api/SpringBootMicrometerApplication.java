package com.javatechie.micrometer.api;

import io.micrometer.core.annotation.Timed;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
public class SpringBootMicrometerApplication {
    //	private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    Logger logger = LoggerFactory.getLogger("SpringBootMicrometerApplication");
    Logger logger = LoggerFactory.getLogger(SpringBootMicrometerApplication.class);

    private static RestHighLevelClient restHighLevelClient;
    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9201;
    private static final String SCHEME = "http";
    private static final String INDEX = "persondata";
    private static final String TYPE = "person";

    @GetMapping("/message")
    @Timed(value = "greeting.time", description = "Time taken to return greeting")
    public String getMessage() {
        Person person = new Person();
        person.setPersonId("123");
        person.setName("saqib");
        logger.info("person ka log {}", person);
//		logger.debug("Hello world.");

        return "Working...!!";
    }

    public static void main(String[] args) {
//		SpringApplication springApplication = new SpringApplication(SpringBootMicrometerApplication.class);
//		springApplication.setLogStartupInfo(false);
//		springApplication.setBannerMode(Banner.Mode.OFF);
//
////		springApplication.run(args);
//		SpringApplication.run(String.valueOf(args));

        SpringApplication app = new SpringApplication(SpringBootMicrometerApplication.class);
        app.setLogStartupInfo(false);
        app.setBannerMode(Banner.Mode.OFF);
        // customize start up here
//        makeConnection();
//
//        System.out.println("Inserting a new Person with name Shubham...");
//        Person person = new Person();
//        person.setName("Saqib");
//        person = insertPerson(person);
//        System.out.println("Person inserted --> " + person);

        app.run(args);
    }

    //	writing to elastic working
    //make connection
    private static synchronized RestHighLevelClient makeConnection() {

        if (restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(HOST, PORT_ONE, SCHEME),
                            new HttpHost(HOST, PORT_TWO, SCHEME)));
        }

        return restHighLevelClient;
    }

    private static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

    private static Person insertPerson(Person person){
        person.setPersonId(UUID.randomUUID().toString());
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("personId", person.getPersonId());
        dataMap.put("name", person.getName());
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, person.getPersonId())
                .source(dataMap);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest);
        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex){
            ex.getLocalizedMessage();
        }
        return person;
    }
}
