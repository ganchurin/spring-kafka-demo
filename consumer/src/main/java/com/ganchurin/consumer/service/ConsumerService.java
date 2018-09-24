package com.ganchurin.consumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

  private static final Logger LOG = LoggerFactory.getLogger(ConsumerService.class);

  @Value("${kafka.consumer.delay}")
  private long delay;

  @KafkaListener(topics = "demo")
  public void listen(ConsumerRecord<String, String> record) throws InterruptedException {
    LOG.info("Consumed [{}] from [{}-{}]", record.value(), record.topic(), record.partition());
    Thread.sleep(delay);
  }
}
