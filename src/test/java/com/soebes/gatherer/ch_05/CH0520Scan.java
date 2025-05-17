package com.soebes.gatherer.ch_05;

import org.junit.jupiter.api.Test;

import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * scan - stateful, inherently ordered, 1:1 operation.
 * https://horstmann.com/unblog/2024-10-01/index.html
 */
class CH0520Scan {

  @Test
  void scanFirst() {
    double initial =  1000.0;
    Double[] transactions = { 100.0, -50.0, 200.0, -150.0 };
    var result = Stream.of(transactions)
        .gather(Gatherers.scan(() -> initial, Double::sum))
        .toList(); // [1100.0, 1050.0, 1250.0, 1100.0]
    System.out.println(result);
//    Stream.of(transactions).reduce(initial, Double::sum);
  }
}
