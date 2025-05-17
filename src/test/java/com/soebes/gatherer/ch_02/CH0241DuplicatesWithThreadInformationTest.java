package com.soebes.gatherer.ch_02;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gatherer - Duplicates
 */
class CH0241DuplicatesWithThreadInformationTest {

  private static final String SEP_START = "(START) " + "-".repeat(10);
  private static final String SEP_END = "(END) " + "-".repeat(10);

  @Test
  void exampleFindDuplicatesParallel() {
    var duplicates = List.of(10, 20, 30, 10, 11, 30, 12, 39)
        .parallelStream()
        .gather(gathererDuplicates())
        .toList();
    assertThat(duplicates).containsExactlyInAnyOrder(10, 30);
    System.out.println("duplicates = " + duplicates);
  }

  static <T> Gatherer<? super T, ?, T> gathererDuplicates() {
    record Dup<T> (int cnt, Map<T, Integer> currentState) {}
    var counter = new AtomicInteger(1);
    Supplier<Dup<T>> initializer = () -> {
      // The output "(Cnt:xx)" will show how many threads will be started in total.
      var cnt = counter.getAndIncrement();
      System.out.printf("(INIT) %s (Cnt:%s)%n", threadInformation(), cnt);
      return new Dup<>(cnt, new HashMap<>());
    };
    //
    Gatherer.Integrator<Dup<T>, T, T> integrator =
        (state, element, _) -> {
          System.out.printf("(I) e:=%5s %s%n", element, threadInformation());
          state.currentState().put(element, state.currentState().getOrDefault(element, 0) + 1);
          return true;
        };
    //
    BiConsumer<Dup<T>, Gatherer.Downstream<? super T>> finisher =
        (state, downstream) -> {
          System.out.printf("(F) s=%s %s%n",state, threadInformation());
          state.currentState().forEach((k, v) -> {
            if (v >= 2) {
              downstream.push(k);
            }
          });
        };
    //
    ReentrantLock lock = new ReentrantLock();
    BinaryOperator<Dup<T>> combiner = (s1, s2) -> {
      try {
        lock.lock();
        var saveS1 = Map.copyOf(s1.currentState());
        var saveS2 = Map.copyOf(s2.currentState());
        s1.currentState().forEach((k, v) -> {
          var s1def = s2.currentState().getOrDefault(k, 0);
          s2.currentState().put(k, v + s1def);
        });
        System.out.printf("(C) %s%n(C) %s%n" +
                          "(C) before: s1=%s s2=%s%n" +
                          "(C)  after: s1=%s s2=%s%n" +
                          "(C) %s%n",
            SEP_START,
            threadInformation(),
            saveS1, saveS2,
            s1, s2, SEP_END);
        return s2;
      } finally {
        lock.unlock();
      }
    };
    //
    return Gatherer.of(initializer, integrator, combiner, finisher);
  }

  private static String threadInformation() {
    return " Thread: Id: %s Name: %s".formatted(Thread.currentThread().threadId(),
        Thread.currentThread().getName()
    );
  }
}
