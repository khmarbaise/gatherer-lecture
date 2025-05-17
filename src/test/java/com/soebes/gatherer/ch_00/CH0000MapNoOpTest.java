package com.soebes.gatherer.ch_00;

import com.soebes.gatherer.Person;
import org.junit.jupiter.api.Test;

import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.PERSONS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - stateless - mapper - no-operation
 */
class CH0000MapNoOpTest {

  @Test
  void mapNoOperationWithIntegerNumbers() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererMapNoOp())
        .toList();
    assertThat(result).containsExactly(INTEGER_NUMBERS.toArray(new Integer[0]));
    System.out.println("result = " + result);
  }

  @Test
  void mapNoOperationWithPersonsList() {
    var result = PERSONS.stream()
        .gather(gathererMapNoOp())
        .toList();
    assertThat(result).containsExactly(PERSONS.toArray(new Person[0]));
    System.out.println("result = " + result);
  }

  /**
   * @param <T> {@link Gatherer}
   * @return {@link Gatherer}
   */
  static <T> Gatherer<T, ?, T> gathererMapNoOp() {
    /**
     * {@link Gatherer.Integrator}
     */
    Gatherer.Integrator<Void, T, T> integrator =
        (_, element, downstream) -> {
      System.out.println("element = " + element);
      return downstream.push(element);
    };
    //
    return Gatherer.ofSequential(integrator);
  }

}
