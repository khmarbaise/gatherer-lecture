package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

class SlidingWindowTest {
  @Test
  void noOperation_withGathererOfSequential() {
    var integerList = List.of(1, 2, 3, 4, 5, 6);

    List<List<Integer>> resultList = integerList.stream()
        .gather(slidingWindow(3))
        .toList();
    System.out.println("resultList = " + resultList);
  }

  @Test
  void noOperation_withGathererOfSequentialDifferentType() {
    var integerList = List.of(List.of(1,2), List.of(2,3), List.of(5,6));

    var resultList = integerList.stream()
        .gather(slidingWindow(2))
        .toList();
    System.out.println("resultList = " + resultList);
  }

  static <T> Gatherer<T, ?, List<T>> slidingWindow(int windowSize) {
    //
    Supplier<List<T>> initializer = () -> new ArrayList<>(windowSize);
    //
    Gatherer.Integrator<List<T>, T, List<T>> integrator = (state, element, downstream) -> {
      state.addLast(element);
      if (state.size() == windowSize) {
        downstream.push(List.copyOf(state));
        state.removeFirst();
      }
      return true;
    };
    //
    BiConsumer<List<T>, Gatherer.Downstream<? super List<T>>> finisher = (state, downstream) -> {
      if (!state.isEmpty()) {
        downstream.push(List.copyOf(state));
      }
    };
    //
    return Gatherer.ofSequential(initializer, integrator, finisher);
  }

}
