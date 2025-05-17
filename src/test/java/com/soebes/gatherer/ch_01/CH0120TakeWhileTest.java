package com.soebes.gatherer.ch_01;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.ALICE;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS_SORTED;
import static com.soebes.gatherer.DataFactory.PERSONS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - Stateful - TakeWhile
 */
class CH0120TakeWhileTest {

  @Test
  void takeWhileWithNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererTakeWhile(n -> n > 74))
        .toList();
    assertThat(result).containsExactly(100);
    System.out.println("result = " + result);
  }

  @Test
  void takeWhileWithSortedNumbers() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .gather(gathererTakeWhile(n -> n < 77))
        .toList();
    assertThat(result).containsExactly(1, 5, 10, 11, 11, 75);
    System.out.println("result = " + result);
  }

  @Test
  void takeWhileWithPersons() {
    var result = PERSONS.stream()
        .gather(gathererTakeWhile(p -> p.age() < 29))
        .toList();
    assertThat(result).containsExactly(ALICE);
    System.out.println("result = " + result);
  }

  static <T> Gatherer<T, ?, T> gathererTakeWhile(Predicate<? super T> predicate) {
    //
    Supplier<boolean[]> initializer = () -> new boolean[]{true};
    //
    Gatherer.Integrator<boolean[], T, T> integrator =
        (state, element, downstream) -> {
      if (state[0] && !predicate.test(element)) {
        state[0] = false;
        return false;
      } else {
        downstream.push(element);
        return true;
      }
    };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }

}
