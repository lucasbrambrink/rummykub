/**
 * Main algorithm for solving a rummykub state 
 */
package rummykub.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author lucasbrambrink
 *
 */
public class Solver
{
   public static final int MAX_COST = 20;
   public static boolean PRINT = false;
   public static int MAX_NUM_STATES = 100000;

   /*
    * Sequester the tileSetToAdd into the initialGameState if possible
    */
   public static GameState findSolution(GameState initialGameState)
   {
      boolean keepIterating = true;
      HashSet<String> seenGameStates = new HashSet<String>();
      GameStateUtil.cleanUpPublicGameState(initialGameState);
      seenGameStates.add(GameStateUtil.serialize(initialGameState));
      ArrayList<GameState> statesToExplore = new ArrayList<GameState>();
      int totalNumberOfStatesExplored = 0;
      statesToExplore.add(initialGameState);
      while (keepIterating && totalNumberOfStatesExplored < MAX_NUM_STATES)
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
                  if (PRINT)
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
                  if (PRINT)
                  {
                     System.out.println("COST OF OPERATION: " + cost);
                     System.out.println("RUNNING TOTAL COST: " + newGameState.runningCost);
                  }

                  if (newGameState.runningCost > MAX_COST)
                  {
                     if (PRINT)
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
