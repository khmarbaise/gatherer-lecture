package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Gatherer;

class IntegratorTest {

  private static <T> Gatherer<T, ?, T> mapNoOp() {
    Gatherer.Integrator<Void, T, T> integrator = (_, element, downstream) -> downstream.push(element);
    return Gatherer.ofSequential(integrator);
  }

  // <A, T, R>
  // A state, T element, Downstream<? super R> downstream
  private static final Gatherer.Integrator<Void, Integer, ? super Integer> noOp =
      //We could use "_" instead of "state"!
      (_, element, downstream) -> {
        downstream.push(element);
        return true;
      };


  @Test
  void noOperation_withGathererOf() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .gather(Gatherer.of(noOp))
        .toList();
    System.out.println("resultList = " + resultList);
  }
  @Test
  void noOperationMapping() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .map(Function.identity())
        .toList();
    System.out.println("resultList = " + resultList);
  }

  @Test
  void noOperation_Integration() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .gather(mapNoOp())
        .toList();
    System.out.println("resultList = " + resultList);
  }
  @Test
  void noOperation_IntegrationDifferentType() {
    var integerList = List.of("1", "2", "3", "4", "5", "6", "7", "8");

    var resultList = integerList.stream()
        .gather(mapNoOp())
        .toList();
    System.out.println("resultList = " + resultList);
  }

  // Typical mapper from T -> R function. see Stream.map(..)
  private static final Function<Integer, Integer> duplicateMapper = in -> in * 2;
  private static final Gatherer.Integrator<Void, Integer, Integer> replace_map_with_integrator =
      (_, element, downstream) -> {
        var newElement = duplicateMapper.apply(element);
        downstream.push(newElement);
        return true;
      };

  @Test
  void map_Integrator() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .gather(Gatherer.of(replace_map_with_integrator))
        .toList();
    System.out.println("resultList = " + resultList);
  }


  // More convenient way to do that:
  // PECS Producer extends, Consumer super
  // Ref: https://stackoverflow.com/questions/2723397/what-is-pecs-producer-extends-consumer-super
  // The given type "Void" defines the type for the "state" which is replaced by "_" because it's not used as all!
  private static <T, R> Gatherer<T, Void, R> mapGatherer(Function<? super T, ? extends R> mapper) {
    Gatherer.Integrator<Void, T,R> integrator = (_, element, downstream) -> {
      downstream.push(mapper.apply(element));
      return true;
    };
    return Gatherer.of(integrator);
  }

  @Test
  void another_map_Integrator() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .gather(mapGatherer(s -> s * 2))
        .toList();
    System.out.println("resultList = " + resultList);
  }


  private static <T> Gatherer<T, Void, T> no_operation() {
    Gatherer.Integrator<Void, T, T> integrator = (_, element, downstream) -> {
      downstream.push(element);
      return true;
    };
    return Gatherer.of(integrator);
  }

  @Test
  void another_no_operation() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .gather(no_operation())
        .toList();
    System.out.println("resultList = " + resultList);
  }



  // Typical Predicate<T> ..
  private static final Predicate<Integer> filter_odd = in -> in % 2 != 0;
  private static final Gatherer.Integrator<Void, Integer, Integer> replace_filter_with_integrator =
      //The "_" is the place of the "Void" (aka state!)
      (_, element, downstream) -> {
        var passOn = filter_odd.test(element);
        if (passOn) {
          downstream.push(element);
        }
        return true;
      };


  @Test
  void filter_Integrator() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .gather(Gatherer.of( replace_filter_with_integrator))
        .toList();
    System.out.println("resultList = " + resultList);
  }


  /**
   * Type parameters:
   * <T> – the type of input elements to the gatherer operation
   * <A> – the potentially mutable state type of the gatherer operation (often hidden as an implementation detail)
   * <R> – the type of output elements from the gatherer operation
   * ..public interface Gatherer<T, A, R>...
   **/
  // PECS Producer extends, Consumer super
  // Ref: https://stackoverflow.com/questions/2723397/what-is-pecs-producer-extends-consumer-super
  // The given type "Void" defines the type (A) for the "state" which is replaced by "_" because it's not used as all!
  private static <T> Gatherer<T, Void, T> filterGatherer(Predicate<? super T> predicate) {
    Gatherer.Integrator<Void, T,T> integrator = (_, element, downstream) -> {
      var passDownstream = predicate.test(element);
      if (passDownstream) {
        downstream.push(element);
      }
      return true;
    };
    return Gatherer.of(integrator);
  }

  @Test
  void another_filter_gatherer() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .gather(filterGatherer(s -> s % 2 == 0))
        .toList();
    System.out.println("resultList = " + resultList);
  }

}
