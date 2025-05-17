package com.soebes.gatherer.ch_02;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.ALICE;
import static com.soebes.gatherer.DataFactory.ALICE_1_2;
import static com.soebes.gatherer.DataFactory.BOB;
import static com.soebes.gatherer.DataFactory.BOB_1_2;
import static com.soebes.gatherer.DataFactory.BOB_A;
import static com.soebes.gatherer.DataFactory.CHARLIE;
import static com.soebes.gatherer.DataFactory.CHARLIE_1_1;
import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS;
import static com.soebes.gatherer.DataFactory.PERSONS;
import static com.soebes.gatherer.DataFactory.PERSONS_WITH_MORE_FIELDS;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Gatherer - Stateful Distinct - Select the last one
 */
class CH0220DistinctLastTest {

  @Test
  void distinctWithNumber() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererDistinctLast())
        .toList();
    assertThat(result).containsExactly(100, 1, 10, 11, 5, 75, 78, 90);
    System.out.println("result = " + result);
  }

  @Test
  void distinctWithPersons() {
    var result = PERSONS.stream()
        .gather(gathererDistinctLast())
        .toList();
    assertThat(result).containsExactly(ALICE, BOB, CHARLIE, BOB_A);
    System.out.println("result = " + result);
  }

  @Test
  void distinctWithPersonsWithMoreFields() {
    var result = PERSONS_WITH_MORE_FIELDS.stream()
        .gather(gathererDistinctLast())
        .toList();
    System.out.println("result = " + result);
    assertThat(result).containsExactly(ALICE_1_2, BOB_1_2, CHARLIE_1_1);
    result.forEach(System.out::println);
  }

  static <ITEM> Gatherer<ITEM, ?, ITEM> gathererDistinctLast() {
    //
    Supplier<Map<ITEM, List<ITEM>>> initializer =  LinkedHashMap::new;
    //
    Gatherer.Integrator<Map<ITEM, List<ITEM>>, ITEM, ITEM> integrator =
        (state, element, _) -> {
          var add = state
              .computeIfAbsent(element, _ -> new ArrayList<>())
              .add(element);
          System.out.println("add = " + add + " Element:" + element);
          return true;
        };
    //
    BiConsumer<Map<ITEM, List<ITEM>>, Gatherer.Downstream<? super ITEM>> finisher =
        (state, downstream) ->
            state.entrySet()
                .stream()
                .forEach(entry ->
                    downstream.push(entry.getValue().getLast()));
    //
    return Gatherer.ofSequential(initializer, integrator, finisher);
  }

}
