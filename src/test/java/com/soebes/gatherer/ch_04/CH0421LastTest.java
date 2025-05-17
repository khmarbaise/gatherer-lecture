package com.soebes.gatherer.ch_04;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS_SORTED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - stateful - last elements of a stream - Different Implementation
 */
class CH0421LastTest {

  @Test
  void gathererLastWithNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererLast(2))
        .toList();

    assertThat(result).containsExactly(78, 90);
    System.out.println("result = " + result);
  }

  @Test
  void gathererLastWithSortedNumbers() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .gather(gathererLast(3))
        .toList();

    assertThat(result).containsExactly(90, 100, 100);
    System.out.println("result = " + result);
  }

  static <T> Gatherer<T, ?, T> gathererLast(int last) {
    //
    Supplier<Deque<T>> initializer = ArrayDeque::new;
    //
    Gatherer.Integrator.Greedy<Deque<T>, T, T> integrator = (state, element, downstream) -> {
      if (downstream.isRejecting()) {
        return false;
      }

      if (state.size() == last) {
        state.removeFirst();
      }
      state.add(element);
      return true;
    };
    //
    BiConsumer<Deque<T>, Gatherer.Downstream<? super T>> finisher =
        (state, downstream) -> {
          state.stream()
              .takeWhile(_ -> !downstream.isRejecting())
              .forEach(downstream::push);
        };
    //
    return Gatherer.ofSequential(initializer, integrator, finisher);
  }
}
