package com.soebes.gatherer.ch_00;

import com.soebes.gatherer.Person;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.PERSONS;

/**
 * stateless operations
 */
class ACH00Test {

  @Test
  void mapIdentity() {
    var result = INTEGER_NUMBERS.stream()
        .map(Function.identity())
        .toList();
    System.out.println("result = " + result);
  }

  @Test
  void mapWithNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .map(n -> n * 2)
        .toList();
    System.out.println("result = " + result);
  }

  @Test
  void mapWithPerson() {
    var nameList = PERSONS.stream()
        .map(Person::name)
        .toList();
    System.out.println("nameList = " + nameList);
  }

  @Test
  void filtering() {
    var result = INTEGER_NUMBERS.stream()
        .filter(p -> p > 20)
        .toList();
    System.out.println("result = " + result);
  }
}
