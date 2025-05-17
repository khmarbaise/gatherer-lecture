package com.soebes.gatherer.ch_01;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.soebes.gatherer.DataFactory.ALICE;
import static com.soebes.gatherer.DataFactory.BOB;
import static com.soebes.gatherer.DataFactory.BOB_A;
import static com.soebes.gatherer.DataFactory.CHARLIE;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS_SORTED;
import static com.soebes.gatherer.DataFactory.PERSONS;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * stateful operations
 */
class ACH01Test {

  @Test
  void limitWithIntegerNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .limit(3)
        .toList();
    assertThat(result).containsExactly(100, 1, 10);
    System.out.println("result = " + result);
  }

  @Test
  void limitWithPersons() {
    var nameList = PERSONS.stream()
        .limit(2)
        .toList();
    assertThat(nameList).containsExactly(ALICE, BOB);
    System.out.println("nameList = " + nameList);
  }

  @Test
  void skipWithNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .skip(5)
        .toList();
    assertThat(result).containsExactly(10, 11, 5, 100, 75, 78, 90);
    System.out.println("result = " + result);
  }

  @Test
  void skipWithPersons() {
    var result = PERSONS.stream()
        .skip(2)
        .toList();
    assertThat(result).containsExactly(CHARLIE, ALICE, BOB_A);
    System.out.println("result = " + result);
  }


  @Test
  void quiz() {
    Map<Integer, String> map = Map.of(1, "One", 2, "Two", 3, "Three", 4, "Four", 5, "Five");
    var list = map.values().stream().filter(s -> !s.equals("Two")).toList();
    System.out.println(list);
  }

  /**
   * @implNote The {@link Stream#takeWhile(Predicate)} method processes a stream of elements and keeps
   * elements as long as a predicate condition evaluates to true. As soon as the predicate returns false, the processing
   * stops, and no further elements are considered. This makes it especially useful for scenarios where you need to stop
   * processing based on a specific condition.
   */
  @Test
  void takeWhileWithNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .takeWhile(n -> n > 74)
        .toList();
    assertThat(result).containsExactly(100);
    System.out.println("result = " + result);
  }

  @Test
  void takeWhileWithNumbersSorted() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .takeWhile(n -> n > 50)
        .toList();
    assertThat(result).isEmpty();
    System.out.println("result = " + result);
  }

  @Test
  void takeWhileWithNumbersSorted_Second() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .takeWhile(n -> n < 12)
        .toList();
    assertThat(result).containsExactly(1, 5, 10, 11, 11);
    System.out.println("result = " + result);
  }

  @Test
  void takeWhileWithPersons() {
    var result = PERSONS.stream()
        .takeWhile(p -> p.age() < 29)
        .toList();
    assertThat(result).containsExactly(ALICE);
    System.out.println("result = " + result);
  }

  /**
   * @implNote The {@link Stream#dropWhile(Predicate)} is a stateful intermediate operation,
   * which also expects a predicate and basically acts like a stateful filter. After the first element which doesn't
   * match the predicate has been encountered, {@code dropWhile()} stops discarding elements from the stream.
   */
  @Test
  void dropWhileWithNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .dropWhile(n -> n < 76)
        .toList();
    assertThat(result).containsExactly(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);
    System.out.println("result = " + result);
  }

  @Test
  void dropWhileWithSortedNumbers() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .dropWhile(n -> n < 74)
        .toList();
    assertThat(result).containsExactly(75, 78, 90, 100, 100);
    System.out.println("result = " + result);
  }

  @Test
  void dropWhileWithPersons() {
    var result = PERSONS.stream()
        .dropWhile(p -> p.age() < 29)
        .toList();
    assertThat(result).containsExactly(BOB, CHARLIE, ALICE, BOB_A);
    System.out.println("result = " + result);
  }

}
