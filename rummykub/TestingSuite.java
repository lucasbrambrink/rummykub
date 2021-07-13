package rummykub.v2;

/**
 * @author lucasbrambrink
 *
 */
public class TestingSuite
{

   public static void runTestSuite()
   {
      System.out.println("*".repeat(300));
      GameState loadedState = GameStateFileParser.load("rummykub/game_results/example_3.txt");
      PrintUtil.printGameState(loadedState);

      GameState solution = Solver.findSolution(loadedState);
      PrintUtil.printChainSideBySide(solution);
      System.out.println("DEPTH: " + solution.depth);
      System.out.println("TOTAL COST: " + solution.runningCost);
      System.out.println("TOTAL NUMBER OF STATES: " + solution.totalNumberOfStatesExplored);
   }
}
