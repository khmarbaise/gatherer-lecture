package com.soebes.gatherer.lecture.collector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Collections.emptySet;

/**
 * https://stackoverflow.com/questions/30995763/java-8-partition-list
 */
// Collector<T, A, R> ..
public class PartitioningCollector<T> implements Collector<T, List<List<T>>, List<List<T>>> {

  private final int batchSize;
  private List<T> batch;

  public PartitioningCollector(int batchSize) {
    this.batchSize = batchSize;
    this.batch = new ArrayList<>(batchSize);
  }

  @Override
  public Supplier<List<List<T>>> supplier() {
    return LinkedList::new;
  }

  @Override
  public BiConsumer<List<List<T>>, T> accumulator() {
    return (total, element) -> {
      batch.add(element);
      if (batch.size() >= batchSize) {
        total.add(batch);
        batch = new ArrayList<>(batchSize);
      }
    };
  }

  @Override
  public BinaryOperator<List<List<T>>> combiner() {
    return (lhs, rhs) -> {
      List<List<T>> result = new ArrayList<>();
      lhs.addAll(rhs);
      return result;
    };
  }

  @Override
  public Function<List<List<T>>, List<List<T>>> finisher() {
    return result -> {
      if (!batch.isEmpty()) {
//        total.add(batch);
        batch = new ArrayList<>(batchSize);
      }
      return result;
    };
  }

  @Override
  public Set<Characteristics> characteristics() {
    return emptySet();
  }
}