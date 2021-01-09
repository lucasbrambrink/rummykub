package rummykub;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author lucasbrambrink
 *
 */
public class SolverUtil
// Scheme:
// -> for each board state:
//     -> explores all possible pairings between two {@code TileSet}
//          -> if they produce a "productive set" i.e. its a valid combination
//               -> recurse into a new board state, i.e. add to stack
// -> if found winning solution, reached maximum recursive depth or max amount of comparisons, stop
//
{
   public static final int MAX_RECURSIVE_DEPTH = 10;
   public static final int MAX_NUM_STATES = 100000;
   public static final boolean PRINT_INTERMEDIATES = false;

   public static ArrayList<IntermediateGameState> findOptimalSolutions(
         ArrayList<TileSet> pool, ArrayList<TileSet> intermediatePool, ArrayList<Tile> holdingTiles
   )
   {
      int depth = 1;
      ArrayList<IntermediateGameState> allValidGameStates = new ArrayList<IntermediateGameState>();
      IntermediateGameState initialGameState = new IntermediateGameState(
            pool,
            intermediatePool,
            holdingTiles
      );
      HashSet<IntermediateGameState> allStates = new HashSet<IntermediateGameState>();

      HashSet<String> seenStates = new HashSet<String>();
      HashSet<IntermediateGameState> newAllStates;
      allStates.add(initialGameState);

      int numberOfStatesScanned = 0;

      boolean keepTrying = true;
      while (keepTrying)
      {
         newAllStates = new HashSet<IntermediateGameState>();
         for (IntermediateGameState state : allStates)
         {
            ArrayList<TileSet> tileSetsToTry = new ArrayList<TileSet>();

            Source incorporateSource = !state.intermediatePool.isEmpty() ? Source.INTERMEDIATE : Source.HOLDING;
            if (incorporateSource == Source.INTERMEDIATE)
            {
               tileSetsToTry.addAll(state.intermediatePool);
            }
            else if (state.holdingTiles.isEmpty())
            {
               keepTrying = false;
               break;
            }
            else
            {
               tileSetsToTry.addAll(
                     state.holdingTiles.stream().map(tile -> TileSet.getSinglet(tile)).collect(Collectors.toList())
               );
            }
            for (TileSet incorporate : tileSetsToTry)
            {
               ArrayList<TestTileResult> results = new ArrayList<TestTileResult>();
               if (PRINT_INTERMEDIATES)
               {
                  System.out.println("INCORPORATING " + incorporate.print());
               }
               // try to find a match against the pool
               results.addAll(
                     tryTileSetAgainst(
                           incorporate,
                           state.pool,
                           Source.POOL
                     )
               );
               // try to find a match against the intermediate pool
               results.addAll(
                     tryTileSetAgainst(
                           incorporate,
                           state.intermediatePool,
                           Source.INTERMEDIATE
                     )
               );
               // try to find a match against any of the holding tiles
               results.addAll(
                     tryTileSetAgainst(
                           incorporate,
                           new ArrayList<TileSet>(state.holdingTiles.stream().map(tile -> TileSet.getSinglet(tile)).collect(Collectors.toList())),
                           Source.HOLDING
                     )
               );
               if (PRINT_INTERMEDIATES)
               {
                  System.out.println("RESULTS NEW " + results.size());

               }

               // check each "productive pair" — if it's a state we have not seen before,
               // recurse and explore that state further
               // if we have seen it before, dont add to avoid infinite looping over cyclical
               // states
               for (TestTileResult result : results)
               {
                  ArrayList<IntermediateGameState> possibleStates = handleTestTileResult(
                        result,
                        incorporate,
                        state.pool,
                        state.intermediatePool,
                        state.holdingTiles,
                        incorporateSource,
                        depth
                  );
                  for (IntermediateGameState possibleState : possibleStates)
                  {
                     if (seenStates.add(possibleState.serialize()))
                     {
                        newAllStates.add(possibleState);
                        if (PRINT_INTERMEDIATES)
                        {
                           System.out.println("BEFORE");
                           IntermediateGameState t = new IntermediateGameState(
                                 state.pool,
                                 state.intermediatePool,
                                 state.holdingTiles
                           );
                           t.depth = depth;
                           t.showResult();
                           System.out.println("AFTER");
                           possibleState.depth = depth;
                           possibleState.showResult();
                        }
                        numberOfStatesScanned++;
                     }
                     else
                     {
                        numberOfStatesScanned++;
                        if (PRINT_INTERMEDIATES)
                        {
                           System.out.println("ALREADY SEEN!!!");
                        }
                     }
                  }
               }
            }
         }
         for (IntermediateGameState intermediateState : newAllStates)
         {
            if (intermediateState.isValid())
            {
               // continue if not empty
               keepTrying = !intermediateState.holdingTiles.isEmpty();
               allValidGameStates.add(intermediateState);
               if (PRINT_INTERMEDIATES)
               {
                  System.out.println("INITIAL STATE");
                  initialGameState.depth = 1;
                  initialGameState.showResult();
                  System.out.println("FOUND SOLUTION");
                  intermediateState.depth = 1;
                  intermediateState.showResult();

               }
               if (intermediateState.holdingTiles.isEmpty())
               {
                  System.out.println("FOUND WINNING SOLUTION");
                  keepTrying = false;
                  break;
               }
            }
         }
         if (depth >= MAX_RECURSIVE_DEPTH || numberOfStatesScanned >= MAX_NUM_STATES)
         {
            System.out.println("GIVING UP AFTER " + numberOfStatesScanned + " STATES TRIED");
            break;
         }
         depth++;
         allStates = newAllStates;
      }
      return allValidGameStates;
   }

   public static ArrayList<IntermediateGameState> handleTestTileResult(
         TestTileResult result, TileSet incorporate, ArrayList<TileSet> pool, ArrayList<TileSet> intermediatePool,
         ArrayList<Tile> holdingTiles, Source incorporatedSource, int depth
   )
   {
      ArrayList<TileSet> newPoolState = new ArrayList<TileSet>(pool);
      ArrayList<TileSet> newIntermediatePool = new ArrayList<TileSet>(intermediatePool);
      ArrayList<Tile> newHoldingTiles = new ArrayList<Tile>(holdingTiles);

      switch (incorporatedSource)
      {
      case POOL:
         newPoolState.remove(incorporate);
         break;
      case INTERMEDIATE:
         newIntermediatePool.remove(incorporate);
         break;
      case HOLDING:
         newHoldingTiles.removeAll(incorporate.getTiles());
         break;
      }
      switch (result.sourceTileSet)
      {
      case POOL:
         newPoolState.remove(result.splitTileSet);
         break;
      case INTERMEDIATE:
         newIntermediatePool.remove(result.splitTileSet);
         break;
      case HOLDING:
         newHoldingTiles.removeAll(result.splitTileSet.getTiles());
         break;
      }

      ArrayList<IntermediateGameState> states = new ArrayList<IntermediateGameState>();
      if (result.splitBeforeTile != null)
      {
         TileSet[] split = result.splitTileSet.splitBefore(result.splitBeforeTile);
         for (TileSet splitSet : split)
         {
            if (splitSet != null)
            {
               if (splitSet.isValid(true))
               {
                  newPoolState.add(splitSet);
               }
               else
               {
                  newIntermediatePool.add(splitSet);
               }
            }
         }
      }
      if (result.newTileSet.isValid(true))
      {
         newPoolState.add(result.newTileSet);
      }
      else
      {
         newIntermediatePool.add(result.newTileSet);
      }
      ArrayList<IntermediateGameState> finalState = new ArrayList<IntermediateGameState>();
      finalState.add(
            new IntermediateGameState(
                  newPoolState,
                  newIntermediatePool,
                  newHoldingTiles
            )
      );
      for (TileSet ts : newIntermediatePool)
      {
         ArrayList<TileSet> clone = new ArrayList<TileSet>(newIntermediatePool);
         if (ts.getTiles().size() < 2)
         {
            continue;
         }

         // if its an incomplete set of length 2, we want to explore
         // both states: the complete set and the set split into 2 singlets
         clone = new ArrayList<TileSet>(newIntermediatePool);
         Tile tileToSplit = ts.getTiles().get(1);
         TileSet[] splits = ts.splitBefore(tileToSplit);
         clone.remove(ts);
         clone.add(splits[0]);
         clone.add(TileSet.getSinglet(tileToSplit));
         finalState.add(
               new IntermediateGameState(
                     newPoolState,
                     clone,
                     newHoldingTiles
               )
         );
      }
      return finalState;
   }

   public static ArrayList<TestTileResult> tryTileSetAgainst(
         TileSet incorporate, ArrayList<TileSet> runAgainst, Source source
   )
   {
      ArrayList<TestTileResult> successfulRuns = new ArrayList<TestTileResult>();
      for (TileSet set : runAgainst)
      {
         if (set == incorporate)
         {
            continue;
         }
         // try combining the entire tile set
         TileSet newTileSet = TileSet.testTileSet(
               incorporate,
               set,
               false
         );
         if (newTileSet != null)
         {
            successfulRuns.add(
                  new TestTileResult(
                        newTileSet,
                        set,
                        null,
                        source
                  )
            );
         }
         // try each tile in the set individually
         for (Tile setTile : set.getTiles())
         {
            newTileSet = TileSet.testTileSet(
                  incorporate,
                  TileSet.getSinglet(setTile),
                  false
            );
            if (newTileSet != null)
            {
               successfulRuns.add(
                     new TestTileResult(
                           newTileSet,
                           set,
                           setTile,
                           source
                     )
               );
            }
         }
      }
      return successfulRuns;
   }
}
