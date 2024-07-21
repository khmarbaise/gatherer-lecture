package com.soebes.gatherer.lecture.collector;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class TestingCollectorTest {

  static <T> TestingCollector<T> partitioningBySize(int size) {
    return new TestingCollector<>(size);
  }

  @Test
  void usingNonParallel() {
    var result = IntStream.range(0, 10).boxed().collect(partitioningBySize(3));
    Assertions.assertThat(result).contains(List.of(0, 1, 2), List.of(3, 4, 5), List.of(6, 7, 8), List.of(9));
    System.out.println("result = " + result);
  }

  @Test
  @Disabled("Does currently not work!")
  void parallel() {
    var result = IntStream.range(0, 10).boxed().parallel().collect(partitioningBySize(3));
    Assertions.assertThat(result).contains(List.of(0, 1, 2), List.of(3, 4, 5), List.of(6, 7, 8), List.of(9));
    System.out.println("result = " + result);
  }

}
