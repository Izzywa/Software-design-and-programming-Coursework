package student.explore;

public class ExploreStrategyFactory {
    public enum Strategy {
        HeuristicDFS("HeuristicDFS"),
        NaiveDFS("NaiveDFS"),
        AStar("AStar");
        
        private final String name;

        Strategy(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    public static ExploreStrategy getExploreStrategy(Strategy strategyName) {
        switch (strategyName) {
            case HeuristicDFS -> {
                return new HeuristicDFSExploreStrategy();
            }
            case NaiveDFS -> {
                return new NaiveDFSExploreStrategy();
            }
            case AStar -> {
                return new AStarExploreStrategy();
            }
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        }
    }
}
