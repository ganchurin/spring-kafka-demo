package com.ganchurin.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.Map;
import java.util.TreeMap;

@Configuration
@EnableKafka
public class ConsumerServiceConfig {

  @Value("${kafka.consumer.bootstrap.servers}")
  private String bootstrapServers;

  @Value("${kafka.consumer.group.id}")
  private String groupId;

  @Value("${kafka.consumer.concurrency}")
  private Integer concurrency;

  @Value("${kafka.consumer.autoOffsetReset}")
  private String autoResetOffset;

  @Value("${kafka.consumer.maxPollRecords}")
  private int maxPollRecords;

  @Value("${kafka.consumer.maxPollIntervalMs}")
  private int maxPollIntervalMs;

  @Value("${kafka.consumer.metadataMaxAgeMs}")
  private int metadataMaxAgeMs;

  @Bean
  KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(concurrency);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> kafkaProps = new TreeMap<>();
    kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoResetOffset);
    kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    kafkaProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
    kafkaProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
    kafkaProps.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, metadataMaxAgeMs);
    return kafkaProps;
  }
}
