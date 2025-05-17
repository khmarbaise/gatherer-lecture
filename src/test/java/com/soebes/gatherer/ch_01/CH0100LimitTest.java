package com.soebes.gatherer.ch_01;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.ALICE;
import static com.soebes.gatherer.DataFactory.BOB;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.PERSONS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - Stateful - Limit
 */
class CH0100LimitTest {

  @Test
  void limitWithNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererLimit(5))
        .toList();
    assertThat(result).containsExactly(100, 1, 10, 11, 5);
    System.out.println("result = " + result);
  }

  @Test
  void limitWithPersons() {
    var result = PERSONS.stream()
        .gather(gathererLimit(2))
        .toList();
    assertThat(result).containsExactly(ALICE, BOB);
    System.out.println("result = " + result);
  }

  static <T> Gatherer<T, ?, T> gathererLimit(int limit) {
    //
    Supplier<int[]> initializer = () -> new int[]{limit};
    //
    Gatherer.Integrator<int[], T, T> integrator =
        (counter, element, downstream) -> {
      if (counter[0] > 0) {
        counter[0]--;
        downstream.push(element);
      }
      return true;
    };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }

}
