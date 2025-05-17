package com.soebes.gatherer.ch_02;

import com.soebes.gatherer.Person;
import com.soebes.gatherer.PersonWithMoreFields;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import static com.soebes.gatherer.DataFactory.ALICE;
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
 * Gatherer - Stateful DistinctBy - classifier.
 */
class CH0221DistinctByTest {

  @Test
  void distinctWithNumber() {
    var result = INTEGER_NUMBERS.stream()
        .gather(gathererDistinctBy(s -> Integer.toString(s).length()))
        .toList();
    assertThat(result).containsExactly(5,90,100);
    System.out.println("result = " + result);
  }

  @Test
  void distinctWithPersons() {
    var result = PERSONS.stream()
        .gather(gathererDistinctBy(Person::name))
        .toList();
    assertThat(result).containsExactly(BOB_A, BOB, ALICE, CHARLIE);
    System.out.println("result = " + result);
  }

  @Test
  void distinctWithPersonsWithMoreFields() {
    var result = PERSONS_WITH_MORE_FIELDS.stream()
        .gather(gathererDistinctBy(PersonWithMoreFields::marker))
        .toList();
    assertThat(result).containsExactly(BOB_1_2, CHARLIE_1_1);
    System.out.println("result = " + result);
  }

  @Test
  void anotherTestWithDistrinctBy() {
    record Person(String firstName, String lastName) { }
    var result = Stream.of(
            new Person("Todd", "Ginsberg"),
            new Person("Emma", "Ginsberg"),
            new Person("Todd", "Smith")
        )
        .gather(gathererDistinctBy(Person::firstName))
        .toList();
    assertThat(result)
        .containsExactly(new Person("Todd", "Ginsberg"), new Person("Emma", "Ginsberg"));
  }

  static <T, A> Gatherer<T, ?, T> gathererDistinctBy(Function<? super T, ? extends A> classifier) {
    Supplier<Map<A, List<T>>> initializer = LinkedHashMap::new;
    /**
     * {@link Gatherer.Integrator}
     */
    Gatherer.Integrator<Map<A, List<T>>, T, T> integrator =
        (state, element, _) -> {
          A apply = classifier.apply(element);
          state.computeIfAbsent(apply, _ -> new ArrayList<>()).add(element);
          return true;
        };
    /**
     * {@link BiConsumer}
     * {@link Gatherer.Downstream}
     */
    BiConsumer<Map<A, List<T>>, Gatherer.Downstream<? super T>> finisher =
        (state, downstream) ->
            state.forEach((_, value) -> downstream.push(value.getLast()));
    //
    return Gatherer.ofSequential(initializer, integrator, finisher);
  }
}
