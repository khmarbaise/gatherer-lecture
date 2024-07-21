package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

class InitializerIntegratorTest {

  //TODO: Restructure using Gatherer.ofSequential(...)
  // from a static context.
  private static Gatherer.Integrator<AtomicInteger, Integer, Integer> limiter(Integer numberOfElements) {
    return (state, element, downstream) -> {
      var currentIndex = state.getAndIncrement();
      if (currentIndex < numberOfElements) {
        downstream.push(element);
      }
      return currentIndex + 1 < numberOfElements;
    };
  }

  private static final Supplier<AtomicInteger> initializer = AtomicInteger::new;

  @Test
  void limit_gatherer_with_parameter() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .gather(Gatherer.ofSequential(initializer, limiter(3)))
        .toList();
    System.out.println("resultList = " + resultList);

  }

}
