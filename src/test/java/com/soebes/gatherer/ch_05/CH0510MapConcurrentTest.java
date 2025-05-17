package com.soebes.gatherer.ch_05;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * mapConcurrent - stateful, inherently ordered, 1:1 operation
 */
class CH0510MapConcurrentTest {

  @Test
  void name() {
    var result = Stream.of(1, 2, 10, 20, 30, 40)
        .gather(Gatherers.mapConcurrent(2, n -> {
          try {
            System.out.println("Started " + n + " " + Thread.currentThread().getName());
            Thread.sleep(Duration.ofSeconds(n));
          } catch (InterruptedException e) {
            System.out.println("Task " + n + " was interrupted");
            Thread.currentThread().interrupt();
          }
          return n;
        }))
        .limit(2)
        .toList();

    System.out.println("result = " + result);
  }

}
