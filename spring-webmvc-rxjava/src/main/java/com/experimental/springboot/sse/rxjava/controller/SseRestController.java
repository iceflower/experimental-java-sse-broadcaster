package com.experimental.springboot.sse.rxjava.controller;

import com.experimental.springboot.sse.rxjava.async.BroadcastEventPublishManager;
import io.iceflower.spring.boot.rxjava.async.FlowableSseEmitter;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/sse")
public class SseRestController {
  private final BroadcastEventPublishManager<String> broadcastEventPublishManager;


  @PostMapping("/addChannel")
  public void setNewChannel(@RequestParam(required = true, value = "name") String name) {

    broadcastEventPublishManager.createBroadcastEventPublisher(name);
  }


  @PostMapping("/{channelName}/push")
  public void setNewChannel(@RequestBody Map<String, String> body, @PathVariable String channelName) {

    log.info(body.get("message"));
    broadcastEventPublishManager.onNext(channelName, body.get("message"));
  }

  @GetMapping("/{channelName}/emit")
  public FlowableSseEmitter<String> testEmitter(@PathVariable String channelName) {

    Flowable flowable =
        Flowable.interval(1, TimeUnit.SECONDS);

    ConnectableFlowable<String> connectableFlowable =
        broadcastEventPublishManager.getSubscriber(channelName);

    return new FlowableSseEmitter<>(
      connectableFlowable.autoConnect()
    );
  }

}
