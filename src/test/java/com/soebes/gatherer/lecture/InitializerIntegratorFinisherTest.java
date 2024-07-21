package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

class InitializerIntegratorFinisherTest {

  record FixedWindow<TR>(int windowSize) implements Gatherer<TR, ArrayList<TR>, List<TR>> {
    @Override
    public Supplier<ArrayList<TR>> initializer() {
      return () -> new ArrayList<>(windowSize);
    }

    @Override
    public Integrator<ArrayList<TR>, TR, List<TR>> integrator() {
      return Gatherer.Integrator.ofGreedy((window, element, downstream) -> {
        // Add the element to the current open window
        window.add(element);
        // Until we reach our desired window size,
        // return true to signal that more elements are desired
        if (window.size() < windowSize) {
          return true;
        }
        // When the window is full, close it by creating a copy
        var result = List.copyOf(window);
        // Clear the window so the next can be started
        window.clear();
        // Send the closed window downstream
        return downstream.push(result);
      });
    }

    @Override
    public BiConsumer<ArrayList<TR>, Downstream<? super List<TR>>> finisher() {
      // The finisher runs when there are no more elements to pass from
      // the upstream
      return (window, downstream) -> {
        // If the downstream still accepts more elements and the current
        // open window is non-empty, then send a copy of it downstream
        if (!downstream.isRejecting() && !window.isEmpty()) {
          downstream.push(new ArrayList<>(window));
          window.clear();
        }
      };
    }
  }


//  public static Gatherer<ArrayList<Integer>, Integer, List<Integer>> windowFixed(int windowSize) {
//    if (windowSize < 1)
//      throw new IllegalArgumentException("'windowSize' must be greater than zero");
//
//    class FixedWindow {
//      List<Integer> window;
//      int at;
//
//      FixedWindow() {
//        at = 0;
//        window = new ArrayList<>();
//      }
//
//      boolean integrate(Integer element, Gatherer.Downstream<List<Integer>> downstream) {
//        window.add(at++, element);
//        if (at < windowSize) {
//          return true;
//        } else {
//          downstream.push(element);
//          window.clear();
//          at = 0;
//          return true;
//        }
//      }
//
//      void finish(Gatherer.Downstream<List<Integer>> downstream) {
//        downstream.push(List.copyOf(window));
//      }
//    }
//    return Gatherer.<Integer, FixedWindow, List<Integer>>ofSequential(
//        // Initializer
//        FixedWindow::new,
//
//        // Integrator
//        Gatherer.Integrator.<FixedWindow, Integer, List<Integer>>ofGreedy(FixedWindow::integrate),
//
//        // Finisher
//        FixedWindow::finish
//    );
//  }


  @Test
  void windowFixedSizeGatherer() {
    var integerList = List.of(1, 2, 3, 4, 5, 6, 7, 8);

    var resultList = integerList.stream()
        .gather(new FixedWindow<>(3))
        .toList();
    System.out.println("resultList = " + resultList);

  }

}
