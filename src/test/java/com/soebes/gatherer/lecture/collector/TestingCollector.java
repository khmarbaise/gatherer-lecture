package com.soebes.gatherer.lecture.collector;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * https://stackoverflow.com/questions/30995763/java-8-partition-list
 */
// Collector<T, A, R> ..
public class TestingCollector<T> implements Collector<T, List<List<T>>, List<List<T>>> {
  private int blockSize;

  private int counter;

  private ReentrantLock lock;

  public TestingCollector(int blockSize) {
    this.counter = 0;
    this.blockSize = blockSize;
    System.out.println("TestingCollector.TestingCollector");
    this.lock = new ReentrantLock();
  }

  @Override
  public Supplier<List<List<T>>> supplier() {
    System.out.println("TestingCollector.supplier");
    return ArrayList::new;
  }

  @Override
  public BiConsumer<List<List<T>>, T> accumulator() {
    return (acc, element) -> {
      System.out.println("TestingCollector.accumulator");
      System.out.println(" -> acc = " + acc);
      System.out.println(" -> element = " + element);
      counter++;
      if (counter == 1) {
        acc.add(new ArrayList<>());
      }
      acc.getLast().add(element);

      if (counter >= blockSize) {
        counter = 0;
      }

      System.out.println(" <- acc = " + acc);
      System.out.println(" <- element = " + element);
    };
  }

  @Override
  public BinaryOperator<List<List<T>>> combiner() {
    return (lhs, rhs) -> {
      System.out.println("TestingCollector.combiner");
      System.out.println("lhs = " + lhs);
      System.out.println("rhs = " + rhs);
      return lhs;
    };
  }

  @Override
  public Function<List<List<T>>, List<List<T>>> finisher() {
    return result -> {
      System.out.println("TestingCollector.finisher");
      System.out.println("result = " + result);
      return (List<List<T>>) result;
    };
  }

  @Override
  public Set<Characteristics> characteristics() {
    return EnumSet.of(Collector.Characteristics.UNORDERED);
  }
}