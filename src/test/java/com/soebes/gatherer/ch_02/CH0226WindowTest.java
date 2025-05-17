package com.soebes.gatherer.ch_02;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - Stateful Window - Fixed size - with finisher
 */
class CH0226WindowTest {


  @Test
  void window() {
    var result = INTEGER_NUMBERS
        .stream()
        .gather(gathererWindow(2))
        .toList();
    System.out.println("result = " + result);
    assertThat(result)
        .containsExactly(
            List.of(100, 1),
            List.of(10, 11),
            List.of(5, 10),
            List.of(11, 5),
            List.of(100, 75),
            List.of(78, 90)
        );
  }

  @Test
  void windowDifferentSize() {
    var numbers = List.of(100, 1, 10, 11, 5, 10, 11, 5, 100);
    var result = numbers
        .stream()
        .gather(gathererWindow(3))
        .toList();
    System.out.println("result = " + result);
    assertThat(result)
        .containsExactly(
            List.of(100, 1, 10),
            List.of(11, 5, 10),
            List.of(11, 5, 100)
        );
  }

  @Test
  void windowDifferentSizeOdd() {
    var numbers = List.of(1,2,3,4,5);
    var result = numbers
        .stream()
        .gather(gathererWindow(2))
        .toList();
    System.out.println("result = " + result);
    assertThat(result)
        .containsExactly(
            List.of(1,2),
            List.of(3,4),
            List.of(5)
        );
  }

  /**
   * Params:
   * <T> initializer – the initializer function for the new gatherer
   * <A> integrator – the integrator function for the new gatherer
   * <R> finisher – the finisher function for the new gatherer
   * @param windowSize  size of the window.
   */
  static <T> Gatherer<T, ?, List<T>> gathererWindow(int windowSize) {
    //
    Supplier<List<T>> initializer = () -> new ArrayList<>(windowSize);
    //
    Gatherer.Integrator<List<T>, T, List<T>> integrator =
        (state, element, downstream) -> {
          state.add(element);
          if (state.size() == windowSize) {
            downstream.push(List.copyOf(state));
            state.clear();
          }
          return true;
        };
    //
    BiConsumer<List<T>, Gatherer.Downstream<? super List<T>>> finisher =
        (state, downstream) -> {
          if (!state.isEmpty()) {
            downstream.push(List.copyOf(state));
          }
        };
    //
    return Gatherer.ofSequential(initializer, integrator, finisher);
  }
}
