package com.ganchurin.producer.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class ProducerService implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(ProducerService.class);

  @Value("${kafka.producer.topic}")
  private String topic;

  @Value("${kafka.producer.delay}")
  private long delay;

  private final KafkaTemplate<String, String> template;

  @Autowired
  public ProducerService(KafkaTemplate<String, String> template) {
    this.template = template;
  }

  @Override
  public void run(String... args) throws InterruptedException {
    ListenableFutureCallback<SendResult<String, String>> callback = new ResultLoggingCallback();
    while (true) {
      String message = getCurrentTime().toString();
      template.send(topic, message).addCallback(callback);
      Thread.sleep(delay);
    }
  }

  private static ZonedDateTime getCurrentTime() {
    return ZonedDateTime.now(ZoneOffset.UTC);
  }

  private static class ResultLoggingCallback implements ListenableFutureCallback<SendResult<String, String>> {

    @Override
    public void onFailure(Throwable throwable) {
      LOG.error("Failed to send a message", throwable);
    }

    @Override
    public void onSuccess(SendResult<String, String> result) {
      ProducerRecord<String, String> record = result.getProducerRecord();
      RecordMetadata metadata = result.getRecordMetadata();
      LOG.info("Produced [{}] into [{}-{}]", record.value(), metadata.topic(), metadata.partition());
    }
  }
}
