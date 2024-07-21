package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

class FixedGroupTest {

  /**
   * Type parameters:
   * <T> – the type of input elements to the gatherer operation
   * <A> – the potentially mutable state type of the gatherer operation (often hidden as an implementation detail)
   * <R> – the type of output elements from the gatherer operation
   * ..public interface Gatherer<T, A, R>...
   **/
  // PECS Producer extends, Consumer super
  // Ref: https://stackoverflow.com/questions/2723397/what-is-pecs-producer-extends-consumer-super
  //                          +----------------------- The input elements
  //                          !      +---------------- The state
  //                          !      !           +---- Output elements
  //                          !      !           !
  //                          v      v           v
  static private <T> Gatherer<T, List<T>, List<T>> fixedGroup(int windowSize) {
    //
    Supplier<List<T>> initializer = ArrayList::new;
    //
    Gatherer.Integrator<List<T>, T, List<T>> integrator = (state, element, downstream) -> {
      state.add(element);
      if (state.size() == windowSize) {
        downstream.push(List.copyOf(state));
        state.clear();
      }
      return true;
    };
    //
    BiConsumer<List<T>, Gatherer.Downstream<? super List<T>>> finisher = (state, downstream) -> {
      if (!state.isEmpty()) {
        downstream.push(List.copyOf(state));
      }
    };

    return Gatherer.ofSequential(initializer, integrator, finisher);
  }

  @Test
  void fixedGroupWindowsWithGatherer() {
    List<Integer> numbers = List.of(7, 1, 2, 7, 1, 3, 4, 4, 1);
    System.out.println("numbers = " + numbers);
    var groups = numbers.stream().gather(fixedGroup(3)).toList();
    System.out.println("groups = " + groups);
  }
  @Test
  void fixedGroupWindowsWithGathererParallelCombiner() {
    List<Integer> numbers = List.of(7, 1, 2, 7, 1, 3, 4, 4, 1);
    System.out.println("numbers = " + numbers);
    var groups = numbers.stream().parallel().gather(fixedGroup(3)).toList();
    System.out.println("groups = " + groups);
  }


  static <T> Collector<T, ?, List<List<T>>> fixedWindow(int size) {
    class InternalState {
      List<T> current;
      List<List<T>> state;

      InternalState() {
        this.current = new ArrayList<>(size);
        this.state = new ArrayList<>();
      }
    }
    //
    Supplier<InternalState> supplier = InternalState::new;
    //
    BiConsumer<InternalState, T> accumulator = (state, element) -> {
      state.current.add(element);
      if (state.current.size() == size) {
        state.state.add(state.current);
        state.current = new ArrayList<>(size);
      }
    };
    //
    BinaryOperator<InternalState> combiner = (lhs, rhs) -> {
//      lhs.state.addAll(rhs.state);
      return lhs;
    };
    //
    Function<InternalState, List<List<T>>> finisher = (acc) -> {
      if (!acc.current.isEmpty()) {
        acc.state.add(acc.current);
      }
      return acc.state;
    };

    return Collector.of(
        supplier, // A (supplier)
        accumulator,// A,T (accumulator)
        combiner, // A (combiner),
        finisher// A,R (finisher)
    );
  }


  @Test
  void fixedGroupWithCollectorOf() {
    var resultList = IntStream.range(0, 11).boxed().collect(fixedWindow(3));
    System.out.println("resultList = " + resultList);
  }
  @Test
  void fixedGroupWithCollectorOfWithParallel() {
    var resultList = IntStream.range(0, 11).boxed().parallel().collect(fixedWindow(3));
    System.out.println("resultList = " + resultList);
  }

}
