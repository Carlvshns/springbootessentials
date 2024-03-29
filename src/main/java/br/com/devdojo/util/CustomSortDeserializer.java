package br.com.devdojo.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.springframework.data.domain.Sort;

public class CustomSortDeserializer extends JsonDeserializer<Sort>{

    @Override
    public Sort deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ArrayNode node = jsonParser.getCodec().readTree(jsonParser);
        Sort.Order[] orders = new Sort.Order[node.size()];
        int i = 0;
        for(JsonNode json : node){
            orders[i] = new Sort.Order(Sort.Direction.valueOf(json.get("direction").asText()),
            json.get("property").asText());
            i++;
        }
        return Sort.by(orders);
    }
}