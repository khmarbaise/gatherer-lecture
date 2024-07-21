package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicatesTest {

  @Test
  void exampleFindDuplicates() {
    var integers = List.of(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);
    var duplicates = findDuplicates(integers);
    System.out.println("duplicates = " + duplicates);
  }
  @Test
  void exampleFindDuplicatesViaStream() {
    var integers = List.of(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);
    var duplicates = findDuplicatesViaStreamAndEntrySet(integers);
    System.out.println("duplicates = " + duplicates);
  }

  List<Integer> findDuplicates(List<Integer> givenList) {
    long count = givenList.stream().distinct().count();
    if (count < givenList.size()) {
      return givenList.stream().filter(i -> Collections.frequency(givenList, i) > 1)
          .distinct().toList();
    } else {
      return List.of();
    }
  }

  public static <T, U> Predicate<T> distinctByFieldGeneral(Function<? super T, ? extends U> fieldExtractor) {
    Set<U> seen = new HashSet<>();

    return element -> !seen.add(fieldExtractor.apply(element));
  }

  @Test
  void exampleFindDuplicatesDistinctByFieldGeneral() {
    var integers = List.of(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);
    var duplicates = integers.stream().filter(distinctByFieldGeneral(s -> s)).toList();
    System.out.println("duplicates = " + duplicates);
  }


  List<Integer> findDuplicatesViaStreamAndEntrySet(List<Integer> givenlist) {
    return givenlist.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .entrySet().stream()
        .filter(s -> s.getValue() > 1)
        .map(Map.Entry::getKey)
        .toList();

  }
  @Test
  void exampleFindDuplicatesWithGatherer() {
    var integers = List.of(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);
    var resultList = integers.stream().gather(duplicatesWithoutCombiner()).toList();

    assertThat(resultList).containsExactlyInAnyOrder(100, 10, 11, 5);
    System.out.println("resultList = " + resultList);
  }

  Supplier<HashMap<Integer, Integer>> supplier = HashMap::new;
  BiConsumer<HashMap<Integer, Integer>, Integer> accumulator = (state, element) -> {
    var orDefault = state.getOrDefault(element, 0);
    state.put(element, orDefault + 1);
  };
  BinaryOperator<HashMap<Integer, Integer>> combiner = (s1, s2) -> {
    s1.forEach((k, v) -> s2.put(k, v + s2.getOrDefault(k, 0)));
    return s2;
  };
  Function<HashMap<Integer, Integer>, List<Integer>> finisher = (acc) -> {
    var duplicateList = acc.entrySet().stream().filter(e -> e.getValue() >= 2).map(Map.Entry::getKey).toList();
    return List.copyOf(duplicateList);
  };

  @Test
  void exampleFindDuplicatedWithCollectorOf() {
    var integers = List.of(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);

    // <R, A> R collect(Collector<? super T, A, R> collector);
    List<Integer> resultList = integers.stream()
        .collect(
            Collector.of(
                supplier, // A (supplier)
                accumulator,// A,T (accumulator)
                combiner, // A (combiner),
                finisher// A,R (finisher)
            )
        );

    assertThat(resultList).containsExactlyInAnyOrder(100, 10, 11, 5);
    System.out.println("resultList = " + resultList);

  }


  // Collector<? super T, A, R> collector
  // The "?" can be replaced by the type of the Supplier which represents the "memory".
  // but that is not required.
  static <RESULT> Collector<RESULT, ?, List<RESULT>> duplicatesWithCollectorOf() {
    // This creates the internal memory
    Supplier<HashMap<RESULT, Integer>> supplier = HashMap::new;
    // This will see each element from the stream.
    BiConsumer<HashMap<RESULT, Integer>, RESULT> accumulator = (state, element) -> {
      state.put(element, state.getOrDefault(element, 0) + 1);
    };
    // This the part which combines in cases of parallelStream() the results.
    BinaryOperator<HashMap<RESULT, Integer>> combiner = (s1, s2) -> {
      s1.forEach((k, v) -> s2.put(k, v + s2.getOrDefault(k, 0)));
      return s2;
    };
    // The finisher is the only part which emits the resulting type. That means this
    // controls the resulting type.
    Function<HashMap<RESULT, Integer>, List<RESULT>> finisher = (acc) -> {
      List<RESULT> duplicateList = acc.entrySet().stream().filter(e -> e.getValue() >= 2).map(Map.Entry::getKey).toList();
      return List.copyOf(duplicateList);
    };
    return Collector.of(
        supplier, // A (supplier)
        accumulator,// A,T (accumulator)
        combiner, // A (combiner),
        finisher// A,R (finisher)
    );
  }

  @Test
  void exampleFindDuplicatedWithConvenience() {
    var integers = List.of(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);

    // <R, A> R collect(Collector<? super T, A, R> collector);
    var resultList = integers.stream()
        .collect(duplicatesWithCollectorOf());

    assertThat(resultList).containsExactlyInAnyOrder(100, 10, 11, 5);
    System.out.println("resultList = " + resultList);

  }



  @Test
  void exampleFindDuplicatesWithGathererCombiner() {
    var integers = List.of(100, 1, 10, 11, 5, 10, 11, 5, 100, 75, 78, 90);
    var resultList = integers.parallelStream().gather(duplicates()).toList();

    assertThat(resultList).containsExactlyInAnyOrder(100, 10, 11, 5);
  }

  @Test
  void exampleFindDuplicatesWithGathererCombinerForStrings() {
    var integers = List.of("A", "BB", "A", "C", "BB", "DD", "EE", "F");
    var resultList = integers.parallelStream().gather(duplicates()).toList();

    assertThat(resultList).containsExactlyInAnyOrder("A", "BB");
  }

  // Gatherer<? super T, ?, R> gatherer
  static <ELEMENT> Gatherer<? super ELEMENT, ?, ELEMENT> duplicates() {
    Supplier<HashMap<ELEMENT, Integer>> initializer = HashMap::new;
    //
    Gatherer.Integrator<HashMap<ELEMENT, Integer>, ELEMENT, ELEMENT> integrator = (state, element, _) -> {
      state.put(element, state.getOrDefault(element, 0) + 1);
      return true;
    };
    //
    BinaryOperator<HashMap<ELEMENT, Integer>> combiner = (s1, s2) -> {
      s1.forEach((k, v) -> s2.put(k, v + s2.getOrDefault(k, 0)));
      return s2;
    };
    //
    BiConsumer<HashMap<ELEMENT, Integer>, Gatherer.Downstream<? super ELEMENT>> finisher = (state, downstream) -> {
      state.forEach((k, v) -> {
        if (v >= 2) {
          downstream.push(k);
        }
      });
    };
    //
    return Gatherer.of(initializer, integrator, combiner, finisher);
  }

  static <T> Gatherer<? super T, ?, T> duplicatesWithoutCombiner() {
    Supplier<HashMap<T, Integer>> initializer = HashMap::new;
    //
    Gatherer.Integrator<HashMap<T, Integer>, T, T> integrator = (state, element, _) -> {
      var orDefault = state.getOrDefault(element, 0);
      state.put(element, orDefault + 1);
      return true;
    };
    //
    BiConsumer<HashMap<T, Integer>, Gatherer.Downstream<? super T>> finisher = (state, downstream) -> {
      state.forEach((k, v) -> {
        if (v >= 2) {
          downstream.push(k);
        }
      });
    };
    //
    return Gatherer.ofSequential(initializer, integrator, finisher);
  }
}
