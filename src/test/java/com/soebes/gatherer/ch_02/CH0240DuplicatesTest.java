package com.soebes.gatherer.ch_02;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - Duplicates
 */
class CH0240DuplicatesTest {
  @Test
  void exampleFindDuplicates() {
    var duplicates = INTEGER_NUMBERS.stream()
        .gather(gathererDuplicates())
        .toList();
    assertThat(duplicates)
        .containsExactlyInAnyOrder(100, 10, 11, 5);
    System.out.println("duplicates = " + duplicates);
  }

  @Test
  void exampleFindDuplicatesParallel() {
    var duplicates = INTEGER_NUMBERS.parallelStream()
        .gather(gathererDuplicates())
        .toList();
    assertThat(duplicates)
        .containsExactlyInAnyOrder(100, 10, 11, 5);
    System.out.println("duplicates = " + duplicates);
  }

  /**
   * @param <T> {@link Gatherer}
   * @return {@link Gatherer}
   */
  static <T> Gatherer<? super T, ?, T> gathererDuplicates() {
    //
    Supplier<Map<T, Integer>> initializer = HashMap::new;
    //
    Gatherer.Integrator<Map<T, Integer>, T, T> integrator =
        (state, element, _) -> {
          var orDefault = state.getOrDefault(element, 0);
          state.put(element, orDefault + 1);
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
    //
    return Gatherer.ofSequential(initializer, integrator, finisher);
  }

}
