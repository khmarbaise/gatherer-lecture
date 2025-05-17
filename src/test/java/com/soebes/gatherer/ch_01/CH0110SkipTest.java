package com.soebes.gatherer.ch_01;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.ALICE;
import static com.soebes.gatherer.DataFactory.BOB_A;
import static com.soebes.gatherer.DataFactory.CHARLIE;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.PERSONS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - Stateful - Skip
 */
class CH0110SkipTest {

  static <T> Gatherer<T, ?, T> gathererSkip(int numberToSkip) {
    //
    Supplier<int[]> initializer = () -> new int[1];
    //
    Gatherer.Integrator<int[], T, T> integrator =
        (state, element, downstream) -> {
      if (numberToSkip > state[0]++ ) {
        return true;
      }
      return downstream.push(element);
    };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }

  @Test
  void skipWithNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererSkip(5))
        .toList();
    assertThat(result).containsExactly(10, 11, 5, 100, 75, 78, 90);
    System.out.println("result = " + result);
  }

  @Test
  void limitWithPersons() {
    var result = PERSONS.stream()
        .gather(gathererSkip(2))
        .toList();
    assertThat(result).containsExactly(CHARLIE, ALICE, BOB_A);
    System.out.println("result = " + result);
  }

}
