package rummykub.v2;

import rummykub.v2.utils.CommandLineSettings;

/**
 * @author lucasbrambrink
 *
 */
public class GameRunner
{
   public static void main(String[] args)
   {
      CommandLineSettings settings = new CommandLineSettings(args);
      Solver solver = new Solver(
            settings.verbose,
            settings.maxCost,
            settings.maxNumStates
      );
      TestingSuite.runTestSuite(
            settings.inputFileName,
            solver
      );
   }
}
