package com.soebes.gatherer.ch_01;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/// Example for implementing a none existing Gatherer - `withIndex`: via {@link Map#entry(Object, Object)}
///
/// This gatherer is stateful.
///
/// * {@link CH0141WithIndexTest#gathererWithIndex()}
/// * {@link CH0141WithIndexTest#gathererWithIndex(long)}
/// * {@link CH0141WithIndexTest#gathererWithIndex(long, LongUnaryOperator)}
///
class CH0141WithIndexTest {

  @Test
  void withIndex() {
    var stream = Stream.of("A", "B", "C", "D", "E");
    var result = stream.gather(gathererWithIndex()).toList();

    System.out.println(result);
    result.forEach(item -> System.out.println("K:" + item.getKey() + " V:" + item.getValue()));
  }

  @Test
  void withIndexStartValue() {
    var stream = Stream.of("A", "B", "C", "D", "E");
    var indexedList = stream.gather(gathererWithIndex(5))
//        .peek(k -> System.out.println("k = " + k))
        .toList();
    System.out.println(indexedList);
  }

  @Test
  void withIndexStartValueAndIncrement() {
    var resultList = Stream.of("A", "B", "C", "D", "E")
        .gather(gathererWithIndex(5, n -> n + 3))
//        .peek(k -> System.out.println("k = " + k))
        .limit(3)
        .toList();

    resultList.forEach(System.out::println);
  }

  /**
   * Starting with index {@code 0}.
   */
  static <T> Gatherer<T, ?, Map.Entry<T, Long>> gathererWithIndex() {
    return gathererWithIndex(0, n -> n + 1);
  }

  /**
   * Starting with the given index.
   */
  static <T> Gatherer<T, ?, Map.Entry<T, Long>> gathererWithIndex(long startValue) {
    return gathererWithIndex(startValue, n -> n + 1);
  }

  static <T> Gatherer<T, ?, Map.Entry<T, Long>> gathererWithIndex(long startValue, LongUnaryOperator increment) {
    //
    Supplier<long[]> initializer = () -> new long[]{startValue};
    //
    Gatherer.Integrator.Greedy<long[], T, Map.Entry<T, Long>> integrator =
        (counter, element, downstream) -> {
          var more = downstream.push(Map.entry(element, counter[0]));
          counter[0] = increment.applyAsLong(counter[0]);
          return more;
        };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }

}
