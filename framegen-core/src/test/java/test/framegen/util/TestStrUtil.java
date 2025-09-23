package test.framegen.util;

import org.framegen.util.StrUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TestStrUtil {

    @ParameterizedTest
    @MethodSource("prefixData")
    public void testGetCommonPrefix(List<String> list, String prefix) {
        String result = StrUtil.getCommonPrefix(list);
        System.out.println("Input: " + list + ", Expected: " + prefix + ", Result: " + result);
        assert prefix.equals(result);
    }

    public static Stream<Arguments> prefixData() {
        return Stream.of(
                Arguments.of(Arrays.asList("head_table1", "head_table2", "head_table3"), "head"),
                Arguments.of(Arrays.asList("headTable1", "headTable2", "headTable3"), "head"),
                Arguments.of(Arrays.asList("head_table_one", "head_table_two", "head_table_three"), "head_table"),
                Arguments.of(Arrays.asList("headTableOne", "headTableTwo", "headTableThree"), "headTable"),
                Arguments.of(Arrays.asList("APIKey", "APIGateway", "APIEndpoint"), "API")
        );
    }

    @ParameterizedTest
    @MethodSource("pascalCaseData")
    public void testToPascalCase(String input, String expected) {
        String result = StrUtil.toPascalCase(input);
        System.out.println("Input: " + input + ", Expected: " + expected + ", Result: " + result);
        assert expected.equals(result);
    }

    public static Stream<Arguments> pascalCaseData() {
        return Stream.of(
                Arguments.of("head_table", "HeadTable"),
                Arguments.of("head_table_one", "HeadTableOne"),
                Arguments.of("_head_table", "HeadTable"),
                Arguments.of("head_table_", "HeadTable"),
                Arguments.of("headTable", "HeadTable")
        );
    }
}
