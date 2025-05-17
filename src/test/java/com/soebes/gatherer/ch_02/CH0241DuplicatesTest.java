package com.soebes.gatherer.ch_02;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - Duplicates - Parallel
 */
class CH0241DuplicatesTest {

  @Test
  void exampleFindDuplicatesParallel() {
    var duplicates = INTEGER_NUMBERS
        .parallelStream()
        .gather(gathererDuplicates())
        .toList();
    assertThat(duplicates).containsExactlyInAnyOrder(100, 10, 11, 5);
    System.out.println("duplicates = " + duplicates);
  }

  static <T> Gatherer<? super T, ?, T> gathererDuplicates() {
    Supplier<Map<T, Integer>> initializer = () -> new HashMap<>();
    /**
     * {@link Gatherer.Integrator}
     */
    Gatherer.Integrator<Map<T, Integer>, T, T> integrator =
        (state, element, _) -> {
          state.put(element, state.getOrDefault(element, 0) + 1);
          return true;
        };
    //
    BiConsumer<Map<T, Integer>, Gatherer.Downstream<? super T>> finisher =
        (state, downstream) -> {
          state.forEach((k, v) -> {
            if (v >= 2) {
              downstream.push(k);
            }
          });
        };

    /**
     * The combiner only has to "combine" two "states". Nothing more.
     * {@link BinaryOperator}.
     */
    BinaryOperator<Map<T, Integer>> combiner = (s1, s2) -> {
      s1.forEach((k, v) -> {
        var s1def = s2.getOrDefault(k, 0);
        s2.put(k, v + s1def);
      });
      return s2;
    };
    //
    return Gatherer.of(initializer, integrator, combiner, finisher);
  }
}
