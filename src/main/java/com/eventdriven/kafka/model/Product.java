package com.eventdriven.kafka.model;

import com.eventdriven.kafka.enums.Color;
import com.eventdriven.kafka.enums.DesignType;
import com.eventdriven.kafka.enums.ProductType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private Color color;
    private ProductType productType;
    private DesignType designType;
}
