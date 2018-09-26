package com.ganchurin.producer.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

class ResultLoggingCallback implements ListenableFutureCallback<SendResult<String, String>> {

  private static final Logger LOG = LoggerFactory.getLogger(ResultLoggingCallback.class);

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
