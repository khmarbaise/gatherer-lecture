package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Gatherer;

class FilterTest {

  private static final List<Integer> INTEGER_LIST = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  private static final List<String> STRING_LIST = List.of("A", "B", "C", "D", "E", "F", "G", "H");

  @Test
  void filterWithoutGatherer() {
    var result = INTEGER_LIST.stream().filter(i -> i < 5).toList();

    System.out.println("result = " + result);
  }

  static <T> Gatherer<T, ?, T> filter(Predicate<? super T> predicate) {
    Gatherer.Integrator<Void, T, T> integrator =
        (_, element, downstream) -> {
          if (predicate.test(element)) {
            downstream.push(element);
          }
          return true;
        };
    return Gatherer.ofSequential(integrator);
  }

  @Test
  void filterWithGathererInteger() {
    var result = INTEGER_LIST.stream().gather(filter(s -> s < 5)).toList();
    System.out.println("result = " + result);
  }
  @Test
  void filterWithGathererString() {
    var result = STRING_LIST.stream().gather(filter(s -> s.compareTo("D") < 0)).toList();
    System.out.println("result = " + result);
  }
}
