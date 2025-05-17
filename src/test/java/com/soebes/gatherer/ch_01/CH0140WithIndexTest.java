package com.soebes.gatherer.ch_01;

import org.junit.jupiter.api.Test;

import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/// Example for implementing a none existing Gatherer - `withIndex`:
///
/// This gatherer is stateful.
///
/// * {@link CH0140WithIndexTest#gathererWithIndex()}
/// * {@link CH0140WithIndexTest#gathererWithIndex(long)}
/// * {@link CH0140WithIndexTest#gathererWithIndex(long, LongUnaryOperator)}
///
class CH0140WithIndexTest {

  @Test
  void withIndex() {
    var stream = Stream.of("A", "B", "C", "D", "E");
    var resultList = stream.gather(gathererWithIndex()).toList();
    resultList.forEach(System.out::println);
  }

  @Test
  void withIndexStartValue() {
    var stream = Stream.of("A", "B", "C", "D", "E");
    var indexedList = stream.gather(gathererWithIndex(5))
//        .peek(k -> System.out.println("k = " + k))
        .toList();
    indexedList.forEach(System.out::println);
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

  record IndexedElement<ITEM>(ITEM value, long index) {
    @Override
    public String toString() {
      return "IndexedElement{idx:%s v:'%s'}".formatted(index, value);
    }
  }

  /**
   * Starting with index {@code 0}.
   */
  static <T> Gatherer<T, ?, IndexedElement<T>> gathererWithIndex() {
    return gathererWithIndex(0, n -> n + 1);
  }

  /**
   * Starting with the given index.
   */
  static <T> Gatherer<T, ?, IndexedElement<T>> gathererWithIndex(long startValue) {
    return gathererWithIndex(startValue, n -> n + 1);
  }

  static <T> Gatherer<T, ?, IndexedElement<T>> gathererWithIndex(long startValue, LongUnaryOperator increment) {
    //
    Supplier<long[]> initializer = () -> new long[]{startValue};
    //
    Gatherer.Integrator.Greedy<long[], T, IndexedElement<T>> integrator =
        (counter, element, downstream) -> {
          var more = downstream
              .push(new IndexedElement<>(element, counter[0]));
          counter[0] = increment.applyAsLong(counter[0]);
          return more;
        };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }

}
