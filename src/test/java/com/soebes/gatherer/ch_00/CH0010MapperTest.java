package com.soebes.gatherer.ch_00;

import com.soebes.gatherer.Person;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.PERSONS;

/**
 * Gatherer - stateless - Mapping with mapper-function.
 */
class CH0010MapperTest {

  @Test
  void mapperWithGatherer() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererMapper(n -> n * 2))
        .toList();
    System.out.println("result = " + result);
  }

  @Test
  void mapWithPerson() {
    var nameList = PERSONS.stream()
        .gather(gathererMapper(Person::name))
        .toList();
    System.out.println("nameList = " + nameList);
  }

  static <T,R> Gatherer<T, ?, R> gathererMapper(Function<? super T, ? extends R> mapper) {
    //
    Gatherer.Integrator<Void, T, R> integrator =
        (_, element, downstream) ->
        downstream.push(mapper.apply(element));
    //
    return Gatherer.ofSequential(integrator);
  }

}
