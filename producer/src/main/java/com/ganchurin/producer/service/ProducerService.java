package com.ganchurin.producer.service;

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

  @Value("${kafka.producer.topic}")
  private String topic;

  @Value("${kafka.producer.delay}")
  private long delay;

  private final KafkaTemplate<String, String> template;

  private final ListenableFutureCallback<SendResult<String, String>> callback = new ResultLoggingCallback();

  @Autowired
  public ProducerService(KafkaTemplate<String, String> template) {
    this.template = template;
  }

  @Override
  public void run(String... args) throws InterruptedException {
    while (true) {
      String message = getCurrentTime().toString();
      template.send(topic, message).addCallback(callback);
      Thread.sleep(delay);
    }
  }

  private static ZonedDateTime getCurrentTime() {
    return ZonedDateTime.now(ZoneOffset.UTC);
  }
}
