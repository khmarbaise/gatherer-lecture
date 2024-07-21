package com.soebes.gatherer.lecture.sealed;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Gatherer;

import static org.assertj.core.api.Assertions.assertThat;

class KeepOnlyTest {

  sealed interface Element permits CustomElement, KnownElement {}
  sealed interface KnownElement extends Element {}
  non-sealed interface CustomElement extends Element {}
  sealed interface HtmlElement extends KnownElement {}
  sealed interface InternalElement extends KnownElement {}
  record Text(String value) implements InternalElement {}
  record HtmlLiteral(String value) implements InternalElement {}
  record Div(String value) implements HtmlElement {}
  record Paragraph(String value) implements HtmlElement {}
  record Span(String value) implements HtmlElement {}
  record Image(String value) implements HtmlElement {}
  record Anchor(String value) implements HtmlElement {}
  record FirstCustom(String value) implements CustomElement {}


  private static final List<Element> ELEMENT_LIST = List.of(
      new FirstCustom("FirstCustom"),
      new Div("Div1"),
      new Paragraph("P1"),
      new Span("Span1"),
      new Image("Image1"),
      new Anchor("https://google.de"),
      new Div("Div2"),
      new HtmlLiteral("Literal1"),
      new Text("Text1"));

  static <T> Gatherer<T, ?, T> keepOnly(Class<? extends Element> clazz) {
    Gatherer.Integrator<Void, T, T> integrator = (_, element, downstream) -> {
      if (clazz.isInstance(element)) {
        downstream.push(element);
      }
      return true;
    };
    return Gatherer.ofSequential(integrator);
  }

  static <T> Gatherer<T, ?, T> keepOnlyFirst(Class<? extends Element> clazz) {
    Gatherer.Integrator<Void, T, T> integrator = (_, element, downstream) -> {
      if (clazz.isInstance(element)) {
        downstream.push(element);
        return false;
      }
      return true;
    };
    return Gatherer.ofSequential(integrator);
  }

  @Test
  void filterWithGathererKeepOnlyDiv() {
    var result = ELEMENT_LIST.stream().gather(keepOnly(Div.class)).toList();
    System.out.printf("result = %s%n", result);
    assertThat(result).containsExactly(new Div("Div1"), new Div("Div2"));
  }

  @Test
  void filterWithGathererKeepOnlyImage() {
    var result = ELEMENT_LIST.stream().gather(keepOnly(Image.class)).toList();
    System.out.printf("result = %s%n", result);
    assertThat(result).containsExactly(new Image("Image1"));
  }

  @Test
  void filterWithGathererKeepOnlyFirst() {
    var result = ELEMENT_LIST.stream().gather(keepOnlyFirst(Div.class)).toList();
    System.out.printf("result = %s%n", result);
    assertThat(result).containsExactly(new Div("Div1"));
  }

  @Test
  void filterWithStaticHelper() {
    var result = ELEMENT_LIST.stream().flatMap(HelpClass.keepOnly(Div.class)).toList();
    System.out.printf("result = %s%n", result);
    assertThat(result).containsExactly(new Div("Div1"), new Div("Div2"));
  }

  @Test
  void filterWithMapMulti() {
    var result = ELEMENT_LIST.stream().mapMulti((Element v1, Consumer<Element> v2) -> {
      System.out.println("-".repeat(20));
      System.out.printf("v1 = %s%n", v1);
      System.out.printf("v2 = %s%n", v2);
      if (v1 instanceof Div) {
        v2.accept(v1);
      }
    }).toList();
    System.out.printf("result = %s%n", result);
    assertThat(result).containsExactly(new Div("Div1"), new Div("Div2"));
  }

}
