package com.soebes.gatherer.ch_04;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS_SORTED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - stateful - last elements of a stream.
 */
class CH0420LastTest {

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
    Supplier<List<T>> initializer = ArrayList::new;
    //
    Gatherer.Integrator.Greedy<List<T>, T, T> integrator = (state, element, downstream) -> {
      if (downstream.isRejecting()) {
        return false;
      }
      state.add(element);
      return true;
    };
    //
    BiConsumer<List<T>, Gatherer.Downstream<? super T>> finisher =
        (state, downstream) -> {
          state.subList(state.size() - last, state.size()).stream()
              .takeWhile(_ -> !downstream.isRejecting())
              .forEach(downstream::push);
        };
    //
    return Gatherer.ofSequential(initializer, integrator, finisher);
  }
}
