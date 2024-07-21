package com.soebes.gatherer.lecture.sealed;

import java.util.function.Function;
import java.util.stream.Stream;

final class HelpClass {

  static <E, T> Function<E, Stream<T>> keepOnly(Class<T> type) {
    return e -> type.isInstance(e) ? Stream.of(type.cast(e)) : Stream.empty();
  }

}
