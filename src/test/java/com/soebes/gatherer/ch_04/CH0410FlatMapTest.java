package com.soebes.gatherer.ch_04;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import static com.soebes.gatherer.DataFactory.LIST_OF_LIST;

/**
 * FlatMap.
 */
class CH0410FlatMapTest {


  @Test
  void flatMapWithGatherer() {
    var result = LIST_OF_LIST.stream()
        .map(s -> {
          System.out.println("s = " + s);
          return s;
        })
        .gather(gathererFlatMap(Collection::stream))
        .limit(3)
        .toList();

    System.out.println("result = " + result);
  }

  @Test
  void mapMulti() {
    var result = LIST_OF_LIST.stream()
        .map(s -> {
          System.out.println("s = " + s);
          return s;
        })
        .<Integer>mapMulti((elements, downstream) ->
            elements.forEach(k -> downstream.accept(k))
        )
        .limit(5)
        .toList();

    System.out.println("result = " + result);
  }

  static <T, R> Gatherer<T, ?, R> gathererFlatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
    //
    Gatherer.Integrator<Void, T, R> integrator =
        (_, element, downstream) -> {
      if (downstream.isRejecting()) {
        return false;
      }
      mapper.apply(element)
          .sequential()
          .takeWhile(_ -> !downstream.isRejecting())
          .forEach(downstream::push);
      return true;
    };
    //
    return Gatherer.ofSequential(integrator);
  }

}
