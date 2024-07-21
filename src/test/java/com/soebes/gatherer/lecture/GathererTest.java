package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Gatherer;

import static java.util.Objects.isNull;

class GathererTest {

  static <T> Gatherer<T, ?, T> duplicates() {
    Gatherer.Integrator<Map<T, Integer>, T, T> integrator =
        (state, element, downstream) -> {
          Integer frequency =
              state.compute(element, (_, v) -> isNull(v) ? 1 : v + 1);
          if (frequency == 2) {
            return downstream.push(element);
          } else {
            return true;
          }
        };
    return Gatherer.ofSequential(
        HashMap::new,
        Gatherer.Integrator.of(integrator)
    );
  }

  @Test
  void name() {
    List<Integer> numbers = List.of(7, 1, 2, 7, 1, 3, 4, 4, 1);
    List<Integer> unique = numbers.stream().distinct().toList();
    List<Integer> duplicates = numbers.stream().gather(duplicates()).toList();
    System.out.println("duplicates = " + duplicates);
    System.out.println("unique = " + unique);
    System.out.println("numbers = " + numbers);
  }
}
