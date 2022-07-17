package org.lili.rxjava;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author lili
 * @date 2021/7/6 1:30
 */
public class Java8StreamTest {
    @Test
    void test() {
        String crazy =
                "supercalifragilisticexpialidocious";
        IntStream letters = crazy.chars();
        Map<Integer, Long> wordCounts = letters.boxed().collect(
                Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                )
        );
        System.out.println(wordCounts);
    }

    @Test
    void testUnique() {
        String crazy = "supercalifragilisticexpialidocious";
        IntStream letters = crazy.chars();
        long count = letters.distinct().count();
        System.out.println(count);
    }

    @Test
    void test11() {

    }
}
