package rummykub.v2.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

import rummykub.v2.dataStructures.GameState;
import rummykub.v2.dataStructures.GameStateMutation;
import rummykub.v2.dataStructures.Tile;
import rummykub.v2.dataStructures.TileMembership;
import rummykub.v2.dataStructures.TileSet;
import rummykub.v2.enums.StateOperation;

/**
 * @author lucasbrambrink
 *
 */
public class GameStateUtil
{
   public static boolean verbose = false;

   public static HashMap<String, ArrayList<TileMembership>> getTileMap(GameState state)
   {
      HashMap<String, ArrayList<TileMembership>> tileMap = new HashMap<String, ArrayList<TileMembership>>();
      loadPoolIntoMap(
            tileMap,
            state.publicPool
      );
      loadPoolIntoMap(
            tileMap,
            state.intermediatePoolDoubles
      );
      loadPoolIntoMap(
            tileMap,
            state.intermediatePoolSingles
      );
      loadPoolIntoMap(
            tileMap,
            state.holdingTiles
      );
      return tileMap;
   }

   private static void loadPoolIntoMap(HashMap<String, ArrayList<TileMembership>> tileMap, ArrayList<TileSet> pool)
   {
      for (TileSet set : pool)
      {
         for (Tile tile : set.tiles)
         {
            if (tileMap.containsKey(TileUtil.serialize(tile)))
            {
               tileMap.get(TileUtil.serialize(tile)).add(
                     new TileMembership(
                           tile,
                           set,
                           pool
                     )
               );
            }
            else
            {
               tileMap.put(
                     TileUtil.serialize(tile),
                     new ArrayList<TileMembership>(
                           Arrays.asList(
                                 new TileMembership(
                                       tile,
                                       set,
                                       pool
                                 )
                           )
                     )
               );
            }
         }
      }
   }

   public static boolean isSolved(GameState state)
   {
      return state.intermediatePoolDoubles.size() == 0 && state.intermediatePoolSingles.size() == 0
            && state.holdingTiles.size() == 0;
   }

   public static HashMap<String, TileSet> getTileSetMap(GameState state)
   {
      HashMap<String, TileSet> tileMap = new HashMap<String, TileSet>();
      loadPoolIntoTileSetMap(
            tileMap,
            state.publicPool
      );
      loadPoolIntoTileSetMap(
            tileMap,
            state.intermediatePoolDoubles
      );
      loadPoolIntoTileSetMap(
            tileMap,
            state.intermediatePoolSingles
      );
      loadPoolIntoTileSetMap(
            tileMap,
            state.allPublicSets
      );
      loadPoolIntoTileSetMap(
            tileMap,
            state.holdingTiles
      );
      return tileMap;
   }

   private static void loadPoolIntoTileSetMap(HashMap<String, TileSet> tileSetMap, ArrayList<TileSet> pool)
   {
      for (TileSet set : pool)
      {
         set.sort();
         tileSetMap.put(
               TileSetUtil.serialize(set),
               set
         );
      }
   }

   private static ArrayList<TileSet> clonePool(ArrayList<TileSet> pool)
   {
      ArrayList<TileSet> clonedPool = new ArrayList<TileSet>();
      for (TileSet set : pool)
      {
         clonedPool.add(TileSetUtil.clone(set));
      }
      return clonedPool;
   }

   public static GameState clone(GameState state)
   {
      GameState newState = new GameState(
            new ArrayList<TileSet>(),
            new ArrayList<TileSet>(),
            new ArrayList<TileSet>(),
            new ArrayList<TileSet>(clonePool(state.holdingTiles))
      );
      newState.allPublicSets = new ArrayList<TileSet>(clonePool(state.publicPool));
      newState.allPublicSets.addAll(clonePool(state.intermediatePoolDoubles));
      newState.allPublicSets.addAll(clonePool(state.intermediatePoolSingles));
      newState.tileSetMap = getTileSetMap(newState);
      newState.depth = state.depth + 1;
      newState.runningCost = state.runningCost;
      return newState;
   }

   public static GameState cleanClone(GameState state)
   {
      GameState newState = new GameState(
            new ArrayList<TileSet>(clonePool(state.publicPool)),
            new ArrayList<TileSet>(clonePool(state.intermediatePoolDoubles)),
            new ArrayList<TileSet>(clonePool(state.intermediatePoolSingles)),
            new ArrayList<TileSet>(clonePool(state.holdingTiles))
      );
      newState.depth = state.depth;
      newState.runningCost = state.runningCost;
      newState.previousState = state.previousState;
      newState.foundTile = state.foundTile;
      newState.tileSetToAdd = state.tileSetToAdd;
      return newState;
   }

   public static void cleanUpPublicGameState(GameState state)
   {
      for (TileSet set : state.allPublicSets)
      {
         if (set.size() == 0)
         {
            continue;
         }
         else if (set.size() == 1)
         {
            state.intermediatePoolSingles.add(set);
         }
         else if (set.size() == 2)
         {
            state.intermediatePoolDoubles.add(set);
         }
         else if (TileSetUtil.isValid(set))
         {
            state.publicPool.add(set);
         }
         else
         {
            System.out.println("INVALID GAME STATE!");
            PrintUtil.printGameState(state);
            PrintUtil.printTileSet(set);
            throw new IllegalArgumentException("Have a set that's invalid");
         }
      }
      state.holdingTiles = new ArrayList<TileSet>(
            state.holdingTiles.stream().filter(ts -> ts.size() != 0).collect(Collectors.toList())
      );
      sort(state);
      state.allPublicSets = null;
   }

   public static void sort(GameState state)
   {
      sortPool(state.publicPool);
      sortPool(state.intermediatePoolDoubles);
      sortPool(state.intermediatePoolSingles);
      sortPool(state.holdingTiles);
   }

   public static int calculateMutationCost(GameStateMutation mutation, GameState initialState)
   {
      if (mutation.operation == StateOperation.MERGE || mutation.operation == StateOperation.SPLIT_MERGE)
      {
         return 0;
      }
      // 1 points for each added in the singles
      int newlyAdded = mutation.newState.intermediatePoolSingles.size() - initialState.intermediatePoolSingles.size();
      return Math.max(
            newlyAdded,
            0
      );
   }

   public static void handleMutationResult(GameState newGameState, TileSet newTileSet, TileSet mutatedTileSet)
   {
      if (newTileSet != null)
      {
         if (newTileSet.size() == 2)
         {
            newGameState.allPublicSets.addAll(Arrays.asList(TileSetUtil.splitDoublet(newTileSet)));
         }
         else
         {
            newGameState.allPublicSets.add(newTileSet);
         }
      }
      // split up the tile set into singles
      if (mutatedTileSet.size() == 2)
      {
         newGameState.allPublicSets.remove(mutatedTileSet);
         newGameState.allPublicSets.addAll(Arrays.asList(TileSetUtil.splitDoublet(mutatedTileSet)));
      }
   }

   public static void acceptTileSet(GameState state, TileSet tileSet)
   {
      switch (tileSet.size())
      {
      case 0:
         break;
      case 1:
         state.intermediatePoolSingles.add(tileSet);
         break;
      case 2:
         state.intermediatePoolDoubles.add(tileSet);
         break;
      // case 3 or more
      default:
         state.publicPool.add(tileSet);
         break;
      }
   }

   public static TileSet nextTileSetToAdd(GameState state)
   {
      if (!state.intermediatePoolDoubles.isEmpty())
      {
         return state.intermediatePoolDoubles.remove(0);
      }
      else if (!state.intermediatePoolSingles.isEmpty())
      {
         return state.intermediatePoolSingles.remove(0);
      }
      else if (!state.holdingTiles.isEmpty())
      {
         return state.holdingTiles.remove(0);
      }
      else
      {
         return null;
      }
   }

   public static String serialize(GameState state)
   {
      return String.join(
            "",
            state.publicPool.stream().map(tileSet -> TileSetUtil.serialize(tileSet)).collect(Collectors.joining(" ")),
            state.intermediatePoolDoubles.stream().map(tileSet -> TileSetUtil.serialize(tileSet)).collect(Collectors.joining(" ")),
            state.intermediatePoolSingles.stream().map(tileSet -> TileSetUtil.serialize(tileSet)).collect(Collectors.joining(" ")),
            state.holdingTiles.stream().map(tileSet -> TileSetUtil.serialize(tileSet)).collect(Collectors.joining(" "))
      );
   }

   private static void sortPool(ArrayList<TileSet> pool)
   {
      Collections.sort(
            pool,
            Comparator.comparing((TileSet ts) -> ts.size()).thenComparing(ts -> TileSetUtil.getTotalValue(ts)).thenComparing(ts -> TileSetUtil.getSetColor(ts)).reversed()
      );
   }

   public static GameStateMutation tryMutation(
         StateOperation operationType, GameState newGameState, TileMembership found
   )
   {
      return null;
   }

   public static GameStateMutation getBestMutation(GameState newGameState, TileSet tileSetToAdd, TileMembership found)
   {
      if (verbose)
      {
         System.out.println("INCORPORATING " + PrintUtil.tileSetToString(tileSetToAdd));
      }
      GameStateMutation mutation = tryMerge(
            newGameState,
            tileSetToAdd,
            found
      );
      // TRY SPLIT MERGE
      if (mutation == null)
      {
         mutation = trySplitMerge(
               newGameState,
               tileSetToAdd,
               found
         );
      }
      // APPLY SPLIT
      if (mutation == null)
      {
         mutation = performSplit(
               newGameState,
               tileSetToAdd,
               found
         );
      }
      return mutation;
   }

   private static GameStateMutation tryMerge(GameState newGameState, TileSet tileSetToAdd, TileMembership found)
   {
      TileSet tileSetToMutate = newGameState.tileSetMap.get(TileSetUtil.serialize(found.memberOf));

      TileSet mergedTileSet = TileSetUtil.mergeTileSets(
            tileSetToMutate,
            tileSetToAdd
      );
      if (mergedTileSet == null)
      {
         return null;
      }
      if (verbose)
      {
         System.out.println(
               "MERGING " + PrintUtil.tileSetToString(tileSetToMutate) + " WITH " + PrintUtil.tileSetToString(tileSetToAdd)
         );
      }
      tileSetToMutate.tiles = new ArrayList<Tile>();
      newGameState.allPublicSets.add(mergedTileSet);
      return new GameStateMutation(
            newGameState,
            StateOperation.MERGE
      );
   }

   private static GameStateMutation trySplitMerge(GameState newGameState, TileSet tileSetToAdd, TileMembership found)
   {
      String serializedTileSte = TileSetUtil.serialize(found.memberOf);
      TileSet tileSetToMutate = newGameState.tileSetMap.get(serializedTileSte);

      TileSet[] splitSets = TileSetUtil.splitAndMerge(
            tileSetToMutate,
            tileSetToAdd
      );
      if (splitSets == null)
      {
         return null;
      }

      newGameState.allPublicSets.add(splitSets[0]);
      newGameState.allPublicSets.add(splitSets[1]);
      if (verbose)
      {
         System.out
               .println("SPLITTING " + PrintUtil.tileSetToString(tileSetToMutate) + " WITH " + PrintUtil.tileSetToString(tileSetToAdd));

      }
      tileSetToMutate.tiles = new ArrayList<Tile>();
      return new GameStateMutation(
            newGameState,
            StateOperation.SPLIT_MERGE
      );
   }

   private static GameStateMutation performSplit(GameState newGameState, TileSet tileSetToAdd, TileMembership found)
   {
      TileSet tileSetToMutate = newGameState.tileSetMap.get(TileSetUtil.serialize(found.memberOf));
      Tile tileToRemove = found.tile;
      if (tileSetToAdd.tiles.indexOf(found.tile) == -1)
      {
         tileToRemove = tileSetToMutate.tiles.stream().filter(
               (Tile t) -> TileUtil.tilesEqual(
                     found.tile,
                     t
               )
         ).collect(Collectors.toList()).get(0);
      }
      TileSet newTileSet = TileSetUtil.removeTile(
            tileSetToMutate,
            tileToRemove
      );

      // add the mutation results to the new game state
      GameStateUtil.handleMutationResult(
            newGameState,
            newTileSet,
            tileSetToMutate
      );

      // mutate the new tile set
      TileSet clonedSet = TileSetUtil.clone(tileSetToAdd);
      clonedSet.addTile(found.tile);
      newGameState.allPublicSets.add(clonedSet);

      if (verbose)
      {
         System.out
               .println("TAKING " + PrintUtil.tileToString(found.tile) + " FROM " + PrintUtil.tileSetToString(found.memberOf) + " TO FORM " + PrintUtil.tileSetToString(clonedSet) + " " + clonedSet.setType);
      }
      return new GameStateMutation(
            newGameState,
            StateOperation.SPLIT
      );
   }
}
