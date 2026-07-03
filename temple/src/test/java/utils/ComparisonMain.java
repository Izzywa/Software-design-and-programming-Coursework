package utils;

public class ComparisonMain {
    public static void main(String[] args) {
        ExploreStrategyComparisonTest test = new ExploreStrategyComparisonTest();
        test.testExploreStrategyAndSaveMultiplier();

        EscapeStrategyComparisonTest escapeTest = new EscapeStrategyComparisonTest();
        escapeTest.testEscapeStrategyAndSaveResults();
    }
}
