package com.soebes.gatherer.ch_02;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;

/**
 * Gatherer - Stateful Sliding Window
 * Sliding window subject:
 * https://stackoverflow.com/questions/53077026/java-8-functional-way-of-getting-consecutive-numbers-of-a-list
 */
class CH0223SlidingWindowTest {

  @Test
  void slidingAverage() {
    var integers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
    var result = integers.stream()
        .gather(slidingWindowWithStatistics(2))
        .toList();

    result.forEach(System.out::println);
  }

  record WindowAverage(List<Integer> items, DoubleSummaryStatistics stats) {}
  static Gatherer<Integer, ?, WindowAverage> slidingWindowWithStatistics(int windowSize) {
    //
    Supplier<List<Integer>> initializer = () -> new ArrayList<>(windowSize);
    //
    Gatherer.Integrator<List<Integer>, Integer, WindowAverage> integrator =
        (state, element, downstream) -> {
          state.addLast(element);
          if (state.size() == windowSize) {
            var statistics = state
                .stream()
                .collect(Collectors.summarizingDouble(s -> s));
            var windowAverage = new WindowAverage(List.copyOf(state), statistics);
            downstream.push(windowAverage);
            state.removeFirst();
          }
          return true;
        };

    //
    return Gatherer.ofSequential(initializer, integrator);
  }
}
