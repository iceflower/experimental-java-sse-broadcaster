package com.experimental.springboot.sse.rxjava.async;

import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import io.reactivex.rxjava3.processors.PublishProcessor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BroadcastEventPublishManager<T> {
  private final Map<String, PublishProcessor<T>> publishProcessors;

  public BroadcastEventPublishManager() {
    this.publishProcessors = new HashMap<>();
  }

  public synchronized boolean createBroadcastEventPublisher(String publisherName) {

    if(this.publishProcessors.containsKey(publisherName)) {
      throw new RuntimeException("이미 존재하는 BroadcastEventPublisher 입니다.");
    }

    PublishProcessor<T> newPublishProcessor = PublishProcessor.create();
    this.publishProcessors.put(publisherName, newPublishProcessor);

    return this.publishProcessors.containsKey(publisherName);
  }

  public synchronized ConnectableFlowable<T> getSubscriber(String publisherName) {

    PublishProcessor<T> publishProcessor =
        this.publishProcessors.get(publisherName);

    return publishProcessor.timeout(10000000, TimeUnit.MINUTES).publish();


  }

  public synchronized void onNext(String publisherName, T data) {

    if(!this.publishProcessors.containsKey(publisherName)) {
      throw new RuntimeException("존재하지 않는 BroadcastEventPublisher 입니다.");
    }

    this.publishProcessors.get(publisherName)
        .onNext(data);
  }

  public synchronized boolean removeBroadcastEventPublisher(String publisherName) {

    if(!this.publishProcessors.containsKey(publisherName)) {
      throw new RuntimeException("존재하지 않는 BroadcastEventPublisher 입니다.");
    }

    PublishProcessor<T> publishProcessor =
        this.publishProcessors.get(publisherName);

    if(publishProcessor.hasSubscribers()) {
      throw new RuntimeException("subscriber 가 존재합니다.");
    }

    this.publishProcessors.remove(publisherName);
    return !this.publishProcessors.containsKey(publisherName);
  }
}
