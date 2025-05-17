package com.soebes.gatherer.ch_03;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static com.soebes.gatherer.DataFactory.INTEGER_NUMBERS_SORTED;

/**
 * Handling following limits.
 */
class CH0310LimitTest {

  @Test
  void limitsWithStream() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .map(element -> {
          System.out.println("(1): " + element);
          return element.toString();
        })
        .gather(gathererLimit(3))
        .toList();

    System.out.println("result = " + result);
  }

  static <T> Gatherer<T, ?, T> gathererLimit(int limit) {
    //
    Supplier<int[]> initializer = () -> new int[]{0};
    //
    Gatherer.Integrator<int[], T, T> integrator = (counter, element, downstream) -> {
      if (counter[0]++ < limit) {
        downstream.push(element);
      }
      return true;
    };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }

  @Test
  void limitsWithStreamChecking() {
    var result = INTEGER_NUMBERS_SORTED.stream()
        .map(element -> {
          System.out.println("map element = " + element);
          return element.toString();
        })
        .gather(gathererLimitWithChecking(3))
        .map(s -> {
          System.out.println("s = " + s);
          return s;
        })
        .limit(2)
        .toList();

    System.out.println("result = " + result);
  }




  static <T> Gatherer<T, ?, T> gathererLimitWithChecking(int limit) {
    class Cnt {
      int count;
    }
    //
    Supplier<Cnt> initializer = Cnt::new;
    //
    Gatherer.Integrator<Cnt, T, T> integrator = (state, element, downstream) -> {
      System.out.println("downstream.isRejecting(1) = " + downstream.isRejecting());
      if (downstream.isRejecting()) {
        return false;
      }
      System.out.println("element=" + element + " state.count = " + state.count + " limit = " + limit);
      if (state.count++ < limit) {
        var d = downstream.push(element);
        System.out.println("downstream.push = " + d);
      }
      System.out.println("downstream.isRejecting(2) = " + downstream.isRejecting());
      return state.count < limit;
    };
    //
    return Gatherer.ofSequential(initializer, integrator);
  }



}
