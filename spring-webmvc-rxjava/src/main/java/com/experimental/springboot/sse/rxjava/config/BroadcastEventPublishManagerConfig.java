package com.experimental.springboot.sse.rxjava.config;

import com.experimental.springboot.sse.rxjava.async.BroadcastEventPublishManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BroadcastEventPublishManagerConfig {

  @Bean
  public BroadcastEventPublishManager<String> broadcastEventPublishManager() {

    return new BroadcastEventPublishManager<>();
  }
}
