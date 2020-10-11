package com.eventdriven.kafka;


import com.eventdriven.kafka.enums.Color;
import com.eventdriven.kafka.enums.ProductType;
import com.eventdriven.kafka.enums.UserId;
import com.eventdriven.kafka.model.Event;
import com.eventdriven.kafka.model.Product;
import com.eventdriven.kafka.model.User;
import com.github.javafaker.Faker;



public class EventGenerator {

    private Faker faker = new Faker();

    public Event generateEvent(){
        return Event.builder()
                .user(genearteUser())
                .product(geneateRandomobject())
                .build();
    }

    private User genearteUser(){
        return User.builder()
                .userId(faker.options().option(UserId.class))
                .username(faker.name().lastName())
                .date(faker.date().birthday())
                .build();
    }

    private Product geneateRandomobject(){
        return Product.builder()
                .color(faker.options().option(Color.class))
                .build();
    }

}
