package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

class DistinctByTest {

  @Test
  void first() {
//    var result = Stream.of("foo", "bar", "baz", "quux")
//        .distinctBy(String::length)      // Hypothetical
//        .toList();
  }

  @Test
  void exampleDistinctWithNumbers() {
    var integers = List.of(1, 10, 11, 10, 11);
    var result = integers.stream().distinct().toList();
    System.out.println("result = " + result);
  }

  @Test
  void usingGroupingBy() {
    var result = Stream.of("123456", "foo", "bar", "baz", "quux", "anton", "egon", "banton")
        .collect(Collectors.groupingBy(String::length));
    System.out.println("result = " + result);
  }

  @Test
  void usingDistinctBy() {
    var stringStream = List.of("123456", "foo", "bar", "baz", "quux", "anton", "egon", "banton");
//    var stringStream = List.<String>of("A", "B", "C", "DD", "EE", "FFF");
    var groupingBy = stringStream.stream().collect(Collectors.groupingBy(String::length));
    var result = stringStream
        .parallelStream()
        .gather(distinctBy(String::length))
        .toList();

    System.out.println("stringStream = " + stringStream);
    System.out.println("result = " + result);
    System.out.println("groupingBy = " + groupingBy);
  }

  @Test
  void usingDistinctByExample() {
    var stringStream = List.of("123456", "foo", "bar", "baz", "quux", "anton", "egon", "banton");
    var result = stringStream
        .stream()
        .gather(distinctByWithoutFinisher(String::length))
        .toList();

    System.out.println("result = " + result);
  }

  @Test
  void usingDistinctByStrange() {
    var stringStream = List.of(500, 200, 1, 2, 3, 4, 10, 20, 1, 50, 100, 50);
    var result = stringStream
        .stream()
        .gather(distinctByWithoutFinisher(Object::hashCode))
        .toList();

    System.out.println("result = " + result);
  }

  static <T, A> Gatherer<T, ?, T> distinctByWithoutFinisher(Function<? super T, ? extends A> classifier) {
    Supplier<HashMap<A, List<T>>> initializer = HashMap::new;
    //
    Gatherer.Integrator<HashMap<A, List<T>>, T, T> integrator = (state, element, downstream) -> {
      A apply = classifier.apply(element);
      state.computeIfAbsent(apply, _ -> new ArrayList<>()).add(element);
      if (state.get(apply).size() == 1) {
        downstream.push(element);
      }
      return true;
    };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }


  /**
   * Type parameters:
   * <T> – the type of input elements to the gatherer operation
   * <A> – the potentially mutable state type of the gatherer operation (often hidden as an implementation detail)
   * <R> – the type of output elements from the gatherer operation
   * ..public interface Gatherer<T, A, R>...
   * ^  ^  ^
   * !  !  !
   * !  !  +--- Result Type
   * !  +------ The State Type
   * +--------- Input Type
   **/
  // PECS Producer Extends, Consumer Super
  // The type <T> describes the consumed elements (input) or consumer
  // The type <A> describes the state (accumulator?)
  // The type <R super T> describes the consumer or the type of the output (the emitted values).
//// PECS Producer extends, Consumer super
//  // Ref: https://stackoverflow.com/questions/2723397/what-is-pecs-producer-extends-consumer-super
//  // The given type "Void" defines the type (A) for the "state" which is replaced by "_" because it's not used as all!
  static <T, A> Gatherer<T, ?, T> distinctBy(Function<? super T, ? extends A> classifier) {
    Supplier<HashMap<A, List<T>>> initializer = HashMap::new;
    //
    Gatherer.Integrator<HashMap<A, List<T>>, T, T> integrator = (state, element, _) -> {
      A apply = classifier.apply(element);
      state.computeIfAbsent(apply, _ -> new ArrayList<>()).add(element);
      return true;
    };
    //
    BiConsumer<HashMap<A, List<T>>, Gatherer.Downstream<? super T>> finisher = (state, downstream) -> {
      state.forEach((_, value) -> downstream.push(value.getLast()));
    };
    //
    return Gatherer.ofSequential(initializer, integrator, finisher);
  }
}
