package com.soebes.gatherer.ch_01;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.ALICE;
import static com.soebes.gatherer.DataFactory.BOB;
import static com.soebes.gatherer.DataFactory.BOB_A;
import static com.soebes.gatherer.DataFactory.CHARLIE;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS_SORTED;
import static com.soebes.gatherer.DataFactory.PERSONS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - Stateful - DropWhile
 */
class CH0130DropWhileTest {

  @Test
  void dropWhileWithNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererDropWhile(n -> n < 77))
        .toList();
    assertThat(result).containsExactly(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);
    System.out.println("result = " + result);
  }

  @Test
  void dropWhileWithSortedNumbers() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .gather(gathererDropWhile(n -> n < 74))
        .toList();
    assertThat(result).containsExactly(75, 78, 90, 100, 100);
    System.out.println("result = " + result);
  }

  @Test
  void dropWhileWithPersons() {
    var result = PERSONS.stream()
        .gather(gathererDropWhile(p -> p.age() < 29))
        .toList();
    assertThat(result).containsExactly(BOB, CHARLIE, ALICE, BOB_A);
    System.out.println("result = " + result);
  }

  static <T> Gatherer<T, ?, T> gathererDropWhile(Predicate<? super T> predicate) {
    //
    Supplier<boolean[]> initializer = () -> new boolean[]{true};
    //
    Gatherer.Integrator<boolean[], T, T> integrator =
        (state, element, downstream) -> {
      if (state[0] && predicate.test(element)) {
        return true;
      } else {
        downstream.push(element);
        state[0] = false;
        return true;
      }
    };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }

}
