package rummykub.v2;

import rummykub.v2.dataStructures.GameState;
import rummykub.v2.utils.GameStateFileParser;
import rummykub.v2.utils.PrintUtil;

/**
 * @author lucasbrambrink
 *
 */
public class TestingSuite
{
   public static void runTestSuite(String fileName, Solver solver)
   {
      System.out.println("*".repeat(300));
      GameState loadedState = GameStateFileParser.load(fileName);
      PrintUtil.printGameState(loadedState);

      GameState solution = solver.findSolution(loadedState);
      PrintUtil.printChainSideBySide(solution);
      System.out.println("DEPTH: " + solution.depth);
      System.out.println("TOTAL COST: " + solution.runningCost);
      System.out.println("TOTAL NUMBER OF STATES: " + solution.totalNumberOfStatesExplored);
   }
}
