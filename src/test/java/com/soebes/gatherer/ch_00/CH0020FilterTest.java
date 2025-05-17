package com.soebes.gatherer.ch_00;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.PERSONS;

/**
 * Gatherer - stateless - Filter
 */
class CH0020FilterTest {

  @Test
  void filteringGatherer() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererFilter(p -> p > 20))
        .toList();
    System.out.println("result = " + result);
  }

  @Test
  void filteringPersonGatherer() {
    var result = PERSONS.stream()
        .gather(gathererFilter(p -> p.age() > 26))
        .toList();
    System.out.println("result = " + result);
  }

  static <T> Gatherer<T, ?, T> gathererFilter(Predicate<? super T> predicate) {
    //
    Gatherer.Integrator<Void, T, T> integrator =
        (_, element, downstream) -> {

      if (predicate.test(element)) {
        downstream.push(element);
      }

      return true;
    };
    return Gatherer.ofSequential(integrator);
  }
}
