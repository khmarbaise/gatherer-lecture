package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

class WindowTest {
  @Test
  void noOperation_withGathererOfSequential() {
    var integerList = List.of(1, 2, 3, 4, 5, 6);

    List<List<Integer>> resultList = integerList.stream()
        .gather(window(2))
        .toList();
    System.out.println("resultList = " + resultList);
  }

  /**
   * Params:
   * <T> initializer – the initializer function for the new gatherer
   * <A> integrator – the integrator function for the new gatherer
   * <R> finisher – the finisher function for the new gatherer
   * @param windowSize
   */
  static <T> Gatherer<T, ?, List<T>> window(int windowSize) {
    //
    Supplier<List<T>> initializer = ArrayList::new;
    //
    Gatherer.Integrator<List<T>, T, List<T>> integrator = (state, element, downstream) -> {
      state.add(element);
      if (state.size() == windowSize) {
        downstream.push(List.copyOf(state));
        state.clear();
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
