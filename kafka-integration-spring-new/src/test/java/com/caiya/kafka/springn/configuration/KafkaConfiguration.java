package com.caiya.kafka.springn.configuration;

import com.caiya.kafka.springn.component.KafkaProperties;
import com.caiya.kafka.springn.core.*;
import com.caiya.kafka.springn.listener.ListenerConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * Kafka相关配置（自动提交）.
 *
 * @author wangnan
 * @since 1.0
 */
@Configuration
public class KafkaConfiguration {

    @Resource
    private KafkaProperties kafkaProperties;

    @Bean
    @ConfigurationProperties(prefix = "kafka.test")
    public KafkaProperties kafkaProperties() {
        return new KafkaProperties();
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        StringSerializer stringSerializer = new StringSerializer();
        return new DefaultKafkaProducerFactory<>(kafkaProperties.getProducerConfig(),
                stringSerializer, stringSerializer);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        if (!CollectionUtils.isEmpty(kafkaProperties.getTopics())) {
            // use the first one as the default topic
            kafkaTemplate.setDefaultTopic(kafkaProperties.getTopics().iterator().next());
        }
        return kafkaTemplate;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        StringDeserializer stringDeserializer = new StringDeserializer();
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.getConsumerConfig(),
                stringDeserializer, stringDeserializer);
    }

    @Bean
    public ListenerConsumer<String, String> listenerConsumer() {
        return new ListenerConsumer<>(consumerFactory());
    }


}
