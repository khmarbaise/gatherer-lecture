package com.soebes.gatherer.lecture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

public class SlidingWindow {

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

/*

  static <T> Gatherer<T, ?, List<T>> slidingStdAverage(int windowSize) {
    //
    int size = 0;
    long total = 0;
    double average = (double) total / size;

    Supplier<T> initializer = () ->  null;
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
 */

}
