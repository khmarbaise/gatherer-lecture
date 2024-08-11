package com.soebes.gatherer.lecture;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.soebes.gatherer.lecture.SlidingWindow.slidingWindow;

class SlidingWindowTest {
  @Test
  void noOperation_withGathererOfSequential() {
    var integerList = List.of(1, 2, 3, 4, 5, 6);

    List<List<Integer>> resultList = integerList.stream()
        .gather(slidingWindow(3))
        .toList();
    System.out.println("resultList = " + resultList);
  }

  @Test
  void noOperation_withGathererOfSequentialDifferentType() {
    var integerList = List.of(List.of(1,2), List.of(2,3), List.of(5,6));

    var resultList = integerList.stream()
        .gather(slidingWindow(2))
        .toList();
    System.out.println("resultList = " + resultList);
  }


}
