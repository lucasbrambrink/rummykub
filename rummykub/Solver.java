/**
 * Main algorithm for solving a rummykub state 
 */
package rummykub.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import rummykub.v2.dataStructures.GameState;
import rummykub.v2.dataStructures.GameStateMutation;
import rummykub.v2.dataStructures.Tile;
import rummykub.v2.dataStructures.TileMembership;
import rummykub.v2.dataStructures.TileSet;
import rummykub.v2.utils.GameStateUtil;
import rummykub.v2.utils.PrintUtil;
import rummykub.v2.utils.TileSetUtil;
import rummykub.v2.utils.TileUtil;

/**
 * @author lucasbrambrink
 *
 */
public class Solver
{
   public boolean verbose;
   public int maxCost;
   public int maxNumStates;
   public static int DEFAULT_MAX_COST = 20;
   public static int DEFAULT_MAX_NUM_STATES = 100000;

   public Solver(
         boolean verbose,
         int maxCost,
         int maxNumStates
   )
   {
      this.verbose = verbose;
      this.maxCost = maxCost == 0 ? DEFAULT_MAX_COST : maxCost;
      this.maxNumStates = maxNumStates == 0 ? DEFAULT_MAX_NUM_STATES : maxCost;
   }

   /*
    * Sequester the tileSetToAdd into the initialGameState if possible
    */
   public GameState findSolution(GameState initialGameState)
   {
      boolean keepIterating = true;
      HashSet<String> seenGameStates = new HashSet<String>();
      GameStateUtil.cleanUpPublicGameState(initialGameState);
      seenGameStates.add(GameStateUtil.serialize(initialGameState));
      ArrayList<GameState> statesToExplore = new ArrayList<GameState>();
      int totalNumberOfStatesExplored = 0;
      statesToExplore.add(initialGameState);
      while (keepIterating && totalNumberOfStatesExplored < maxNumStates)
      {
         ArrayList<GameState> newStatesToExplore = new ArrayList<GameState>();
         for (GameState state : statesToExplore)
         {
            GameState stateClone = GameStateUtil.cleanClone(state);
            TileSet tileSetToAdd = GameStateUtil.nextTileSetToAdd(state);
            if (tileSetToAdd == null)
            {
               return state;
            }
            ArrayList<Tile> complements = TileSetUtil.getComplements(tileSetToAdd);
            HashMap<String, ArrayList<TileMembership>> tileMap = GameStateUtil.getTileMap(state);
            for (Tile complement : complements)
            {
               ArrayList<TileMembership> foundList = tileMap.get(TileUtil.serialize(complement));
               if (foundList == null)
               {
                  continue;
               }
               for (TileMembership found : foundList)
               {
                  // STEP 1: clone the game state
                  GameState newGameState = GameStateUtil.clone(state);
                  newGameState.tileSetToAdd = tileSetToAdd;
                  newGameState.foundTile = found.tile;
                  newGameState.previousState = stateClone;

                  // STEP 2: get best mutation
                  GameStateMutation mutation = GameStateUtil.getBestMutation(
                        newGameState,
                        tileSetToAdd,
                        found
                  );
                  // Step 3: sort the game state into the right pools
                  GameStateUtil.cleanUpPublicGameState(newGameState);

                  // STEP 4: print Side by Side
                  if (verbose)
                  {
                     PrintUtil.printSideBySide(
                           state,
                           newGameState
                     );
                  }

                  // Step 5: calculate the cost of this mutation
                  // if too high, don't
                  int cost = GameStateUtil.calculateMutationCost(
                        mutation,
                        state
                  );
                  newGameState.runningCost += cost;
                  if (verbose)
                  {
                     System.out.println("COST OF OPERATION: " + cost);
                     System.out.println("RUNNING TOTAL COST: " + newGameState.runningCost);
                  }

                  if (newGameState.runningCost > maxCost)
                  {
                     if (verbose)
                     {
                        System.out.println("SKIPPING STATE BECAUSE COST IS TOO HIGH!");
                     }
                     continue;
                  }
                  // make sure we avoid iterating over the same game states
                  String serializedState = GameStateUtil.serialize(newGameState);
                  if (!seenGameStates.contains(serializedState))
                  {
                     seenGameStates.add(serializedState);
                     newStatesToExplore.add(newGameState);
                     totalNumberOfStatesExplored++;
                  }
                  if (GameStateUtil.isSolved(newGameState))
                  {
                     System.out.println("FOUND SOLUTION");
                     newGameState.totalNumberOfStatesExplored = totalNumberOfStatesExplored;
                     return newGameState;
                  }
               }
            }
         }
         statesToExplore = newStatesToExplore;
         totalNumberOfStatesExplored++;
      }
      return initialGameState;
   }

}
