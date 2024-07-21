package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Gatherer;

class MapTest {

  private static final List<Integer> INTEGER_LIST = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  private static final List<String> STRING_LIST = List.of("A", "B", "C", "D", "E", "F", "G", "H");

  @Test
  void mapWithoutGatherer() {
    var result = INTEGER_LIST.stream().map(Integer::toBinaryString).toList();

    System.out.println("result = " + result);
  }


  public static <T, R> Gatherer<T, ?, R> map(Function<? super T, ? extends R> mapper) {
    Gatherer.Integrator<Void, T, R> integrator =
        (_, element, downstream) -> downstream.push(mapper.apply(element));
    return Gatherer.of(integrator);
  }

  @Test
  void mapWithGathererIntegerList() {
    var result = INTEGER_LIST.stream().gather(map(Integer::toBinaryString)).toList();

    System.out.println("result = " + result);
  }
  @Test
  void mapWithGathererStringList() {
    var result = STRING_LIST.stream().gather(map(String::toLowerCase)).toList();

    System.out.println("result = " + result);
  }

}
