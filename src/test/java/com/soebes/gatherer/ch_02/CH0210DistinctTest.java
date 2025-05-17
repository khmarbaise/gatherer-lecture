package com.soebes.gatherer.ch_02;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.ALICE;
import static com.soebes.gatherer.DataFactory.ALICE_1_1;
import static com.soebes.gatherer.DataFactory.BOB;
import static com.soebes.gatherer.DataFactory.BOB_1_1;
import static com.soebes.gatherer.DataFactory.BOB_A;
import static com.soebes.gatherer.DataFactory.CHARLIE;
import static com.soebes.gatherer.DataFactory.CHARLIE_1_1;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.PERSONS;
import static com.soebes.gatherer.DataFactory.PERSONS_WITH_MORE_FIELDS;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Gatherer - Stateful Distinct
 */
class CH0210DistinctTest {

  @Test
  void distinctWithNumber() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererDistinct())
        .toList();
    assertThat(result).containsExactly(100, 1, 10, 11, 5, 75, 78, 90);
    System.out.println("result = " + result);
  }

  @Test
  void distinctWithPersons() {
    var result = PERSONS.stream()
        .gather(gathererDistinct())
        .toList();
    assertThat(result).containsExactly(ALICE, BOB, CHARLIE, BOB_A);
    System.out.println("result = " + result);
  }

  @Test
  void distinctWithPersonsWithMoreFields() {
    var result = PERSONS_WITH_MORE_FIELDS.stream()
        .gather(gathererDistinct())
        .toList();
    assertThat(result).containsExactly(ALICE_1_1, BOB_1_1, CHARLIE_1_1);
    System.out.println("result = " + result);
  }

  static <ITEM> Gatherer<ITEM, ?, ITEM> gathererDistinct() {
    //
    Supplier<Set<ITEM>> initializer = HashSet::new;
    //
    Gatherer.Integrator<Set<ITEM>, ITEM, ITEM> integrator =
        (state, element, downstream) -> {
          var added = state.add(element);
          if (added) {
            downstream.push(element);
          }
          return true;
        };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }

}
