package rummykub.v2.utils;

/**
 * Parses a set of command line arguments for input files & verboseness
 * 
 * @author lucasbrambrink
 *
 */
public class CommandLineSettings
{
   public String inputFileName;
   public boolean verbose;
   public int maxCost;
   public int maxNumStates;

   public CommandLineSettings(String[] commandLineArgs)
   {
      String commandLineArg;
      for (int i = 0; i < commandLineArgs.length; i++)
      {
         commandLineArg = commandLineArgs[i];
         // parse for input file command line argument
         if (commandLineArg.equals(INPUT_FILE_ARGUMENT_LONG) || commandLineArg.equals(INPUT_FILE_ARGUMENT_SHORT))
         {
            inputFileName = commandLineArgs[i + 1];
            i++;
         }
         // if in append mode, keep adding file names until we reach another command line
         // argument
         else if (commandLineArg.endsWith(MAX_COST))
         {
            this.maxCost = Integer.parseInt(commandLineArgs[i + 1]);
            i++;
         }
         else if (commandLineArg.endsWith(MAX_NUM_STATES))
         {
            this.maxNumStates = Integer.parseInt(commandLineArgs[i + 1]);
            i++;
         }
         // determine if we want to also print all output to the console
         else if (commandLineArg.equals(VERBOSE))
         {
            this.verbose = true;
         }
      }
      if (inputFileName == null)
      {
         inputFileName = EXAMPLE_3;
      }
      GameStateUtil.verbose = verbose;
      PrintUtil.verbose = verbose;
   }

   private static final String INPUT_FILE_ARGUMENT_LONG = "--input";
   private static final String INPUT_FILE_ARGUMENT_SHORT = "-i";
   private static final String VERBOSE = "--verbose";
   private static final String MAX_COST = "--max_cost";
   private static final String MAX_NUM_STATES = "--max_num_states";

   public static final String EXAMPLE_3_2 = "rummykub/game_states/example_3_2.txt";
   public static final String EXAMPLE_3 = "rummykub/game_states/example_3.txt";
}
