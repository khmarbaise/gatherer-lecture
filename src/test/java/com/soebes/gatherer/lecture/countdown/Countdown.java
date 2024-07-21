package com.soebes.gatherer.lecture.countdown;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This is the code used for the episode 22 of the JEP Café series, on Data Oriented Programming.
 * You can watch this episode here: <a href="https://youtu.be/Y2pmZlP-cOU">https://youtu.be/Y2pmZlP-cOU</a>
 * The full JEP Café series is here: <a href="https://www.youtube.com/playlist?list=PLX8CzqL3ArzV4BpOzLanxd4bZr46x5e87">https://www.youtube.com/playlist?list=PLX8CzqL3ArzV4BpOzLanxd4bZr46x5e87</a>
 *
 * @author José Paumard
 * @see <a href="https://gist.github.com/JosePaumard/ee935c53f942a25c3cd38027ce4e31c3">https://gist.github.com/JosePaumard/ee935c53f942a25c3cd38027ce4e31c3</a>
 */
public record Countdown(List<Integer> ints, int target) {

  public Countdown {
    Elements.allElements = HashSet.newHashSet(600_000);
  }

  private static final
  Function<Element, Gatherer<Element, ?, Element>> insertPreservingOrder =
      element -> Gatherer.ofSequential(
          () -> new Object() {
            Element previous;

            {
              this.previous = element;
            }
          },
          (state, e, downstream) -> {
            if (e.compareTo(state.previous) < 0) {
              return downstream.push(e);
            } else {
              var previous = state.previous;
              state.previous = e;
              return downstream.push(previous);
            }
          },
          (state, downstream) -> {
            downstream.push(state.previous);
          });

  sealed interface Element
      extends Comparable<Element>
      permits Number, Operation {

    default int compareTo(Element other) {
      return compareElement(this, other);
    }
  }

  record Target(int value) {
    public boolean matches(Element element) {
      return switch (element) {
        case Number(int n) -> n == value;
        case Add(_, _, int result) -> result == value;
        case Sub(_, _, int result) -> result == value;
        case Mult(_, _, int result) -> result == value;
        case Div(_, _, int result) -> result == value;
      };
    }

    public boolean doesntMatch(Element element) {
      return !matches(element);
    }
  }

  enum Operator {
    ADD, SUB, MULT, DIV
  }

  sealed interface Operation extends Element permits Add, Sub, Mult, Div {
    static Operation of(Operator operator, Element left, Element right) {
      return switch (operator) {
        case ADD -> new Add(left, right);
        case SUB -> new Sub(left, right);
        case MULT -> new Mult(left, right);
        case DIV -> new Div(left, right);
      };
    }
  }

  record Add(Element lhs, Element rhs, int result) implements Operation {
    public Add(Element lhs, Element rhs) {
      this(lhs, rhs, resolve(lhs) + resolve(rhs));
    }
  }

  record Sub(Element e1, Element e2, int result) implements Operation {
    public Sub(Element e1, Element e2) {
      this(e1, e2, resolve(e1) - resolve(e2));
    }
  }

  record Mult(Element e1, Element e2, int result) implements Operation {
    public Mult(Element e1, Element e2) {
      this(e1, e2, resolve(e1) * resolve(e2));
    }
  }

  record Div(Element e1, Element e2, int result) implements Operation {
    public Div(Element e1, Element e2) {
      this(e1, e2, resolve(e1) / resolve(e2));
    }
  }

  record Number(int value) implements Element {
  }

  record Elements(List<Element> elements) implements Iterable<Element> {

    private static Set<Elements> allElements;

    public Iterator<Element> iterator() {
      return elements.iterator();
    }

    public int size() {
      return this.elements.size();
    }

    public Stream<Element> stream() {
      return elements.stream();
    }

    public List<Elements> mergeElements(int leftIndex, int rightIndex) {
      var left = this.get(leftIndex);
      var right = this.get(rightIndex);
      Elements elements = remove(rightIndex).remove(leftIndex);

      return Arrays.stream(Operator.values())
          .<Element>mapMulti((op, downstream) -> {
            switch (op) {
              case ADD -> downstream.accept(Operation.of(op, left, right));
              case SUB -> {
                if (resolve(left) > resolve(right)) {
                  downstream.accept(Operation.of(op, left, right));
                }
              }
              case MULT -> {
                if (resolve(left) > 1 && resolve(right) > 1) {
                  downstream.accept(Operation.of(op, left, right));
                }
              }
              case DIV -> {
                if (resolve(right) > 1 && resolve(left) % resolve(right) == 0) {
                  downstream.accept(Operation.of(op, left, right));
                }
              }
            }
          })
          .map(elements::add)
          .filter(allElements::add)
          .toList();
    }

    private Elements add(Element element) {
      return new Elements(
          this.elements.stream()
              .gather(insertPreservingOrder.apply(element))
              .toList());
    }

    private Elements remove(int removedIndex) {
      return new Elements(IntStream.range(0, elements.size())
          .filter(index -> index != removedIndex)
          .mapToObj(elements::get)
          .toList());
    }

    private Element get(int index) {
      return this.elements.get(index);
    }
  }

  record Solution(Stream<Element> elements) {
    public Solution(Stream<Element> solutions, Stream<Element> moreSolutions) {
      this(Stream.of(solutions, moreSolutions).flatMap(Function.identity()));
    }
  }

  record CountdownSolver(Elements elements, Target target) {

    public Solution solve() {

      var solutions =
          elements.stream().filter(target::matches).toList();
      var nextElements =
          elements.stream().filter(target::doesntMatch).toList();

      if (nextElements.isEmpty()) {
        return new Solution(solutions.stream());
      } else {
        var moreSolutions =
            reduce(nextElements).stream()
                .map(elements -> new CountdownSolver(elements, target))
                .map(CountdownSolver::solve)
                .flatMap(Solution::elements)
                .toList();
        return new Solution(solutions.stream(), moreSolutions.stream());
      }
    }
  }

  private static int compareElement(Element e1, Element e2) {
    return Integer.compare(resolve(e2), resolve(e1));
  }

  private static int resolve(Element element) {
    return switch (element) {
      case Number number -> number.value();
      case Add(_, _, int result) -> result;
      case Mult(_, _, int result) -> result;
      case Sub(_, _, int result) -> result;
      case Div(_, _, int result) -> result;
    };
  }

  private static List<Elements> reduce(List<Element> elements) {
    return reduce(new Elements(elements));
  }

  private static List<Elements> reduce(Elements elements) {

    return IntStream.range(0, elements.size())
        .boxed()
        .flatMap(leftIndex ->
            IntStream.range(leftIndex + 1, elements.size())
                .boxed()
                .flatMap(rightIndex -> elements.mergeElements(leftIndex, rightIndex).stream())

        )
        .toList();
  }

  private static String composeElement(Element element) {
    return switch (element) {
      case Number(int value) -> Integer.toString(value);
      case Add(Element left, Element right, _) -> "(" + composeElement(left) + "+" + composeElement(right) + ")";
      case Mult(Element left, Element right, _) -> "(" + composeElement(left) + "*" + composeElement(right) + ")";
      case Sub(Element left, Element right, _) -> "(" + composeElement(left) + "-" + composeElement(right) + ")";
      case Div(Element left, Element right, _) -> "(" + composeElement(left) + "/" + composeElement(right) + ")";
    };
  }

  private static Collection<String> composeResult(Solution solutions) {
    return solutions.elements()
        .map(Countdown::composeElement)
        .toList();
  }

  public Collection<String> solve() {

    var intsAsElements = ints.stream()
        .map(Number::new)
        .map(Element.class::cast)
        .sorted()
        .toList();
    var elements = new Elements(intsAsElements);
    var target = new Target(this.target);

    var solver = new CountdownSolver(elements, target);
    var solutions = solver.solve();
    return composeResult(solutions);
  }

  public static void main(String... args) {
    // https://youtu.be/_JQYYz92-Uk
//        var numbers = List.of(25, 50, 75, 100, 1, 10);
//        var target = 813;

    // https://youtu.be/mRLW_iZVmHU
//        var numbers = List.of(75, 25, 50, 100, 8, 2);
//        var target = 431;

    // https://youtu.be/sKdM82SELsU
//        var numbers = List.of(25, 100, 75, 50, 6, 4);
//        var target = 821;

//        var numbers = List.of(1, 3, 5, 10, 25, 50);
//        var target = 999;

    var numbers = List.of(3, 1, 7, 8, 1, 4);
    var target = 246;
    var start = Instant.now();
    var solutions = new Countdown(numbers, target).solve();
    var end = Instant.now();

    solutions.forEach(System.out::println);
    System.out.println("Time taken (ms): " + Duration.between(start, end));
  }
}