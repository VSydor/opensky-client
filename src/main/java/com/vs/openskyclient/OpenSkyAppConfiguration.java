package com.vs.openskyclient;

import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.opensky.api.OpenSkyApi;
import org.opensky.model.StateVector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OpenSkyAppConfiguration {

    @Value("${opensky.api.username}")
    private String username;
    @Value("${opensky.api.password}")
    private String password;

    @Bean
    public OpenSkyApi openSkyApi() {
        return new OpenSkyApi(username, password);
    }

    @Value("${kafka.server}")
    private String kafkaServer;

    @Value("${kafka.producer.id}")
    private String kafkaProducerId;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerId);
        return props;
    }

    @Bean
    public ProducerFactory<Long, StateVector> producerStateVectorFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Long, StateVector> kafkaTemplate() {
        KafkaTemplate<Long, StateVector> template = new KafkaTemplate<>(producerStateVectorFactory());
        template.setMessageConverter(new StringJsonMessageConverter());
        return template;
    }

}
