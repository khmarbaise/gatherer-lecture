package com.soebes.gatherer.ch_03;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS_SORTED;

/**
 * Limits in a Stream.
 */
class ACH03Test {

  @Test
  void limitsWithUsualStream() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .map(element -> {
          System.out.println("(1) = " + element);
          return element.toString();
        })
        .limit(3)
        .toList();

    System.out.println("result = " + result);
  }













  @Test
  void limitsWithUsualStreamMoreMapper() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .map(element -> {
          System.out.println("(1) = " + element);
          return element.toString();
        })
        .limit(3)
        .map(element -> {
          System.out.println("(2) = " + element);
          return Integer.parseInt(element);
        })
        .toList();
    System.out.println("result = " + result);
  }

















  @Test
  void limitsWithUsualStreamMoreMapperMoreLimits() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .map(element -> {
          System.out.println("(1) = " + element);
          return element.toString();
        })
        .limit(3)
        .map(element -> {
          System.out.println("(2) = " + element);
          return Integer.parseInt(element);
        })
        .limit(2)
        .toList();
    System.out.println("result = " + result);
  }

  @Test
  void sideEffectsPeek() {
    var ints = List.of(1, 2, 3, 4, 5, 6);
    var list = new ArrayList<>();

    var count = ints.stream()
        .peek(list::add)
        .count();

    System.out.println("count = " + count);
    System.out.println("list = " + list);
  }

  @Test
  void sideEffectsPeekAndMore() {
    var ints = List.of(1, 2, 3, 4, 5, 6);
    var list = new ArrayList<>();

    var count = ints.stream()
        .peek(list::add)
        .filter(i -> i < 10)
        .count();

    System.out.println("count = " + count);
    System.out.println("list = " + list);
  }

}
