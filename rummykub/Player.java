package rummykub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author lucasbrambrink
 *
 */
public class Player
{
   private String name;
   private boolean isOnTheBoard;
   private ArrayList<Tile> holdingTiles;

   public Player(String name)
   {
      this.name = name;
      this.isOnTheBoard = false;
      this.holdingTiles = new ArrayList<Tile>();
   }

   public void addTile(Tile tile)
   {
      this.holdingTiles.add(tile);
   }

   public void sortUnorderedTiles()
   {
      Collections.sort(
            this.holdingTiles,
            Comparator.comparing(Tile::getColor).thenComparing(Tile::getValue)
      );
   }

   public boolean hasWon()
   {
      return this.holdingTiles.isEmpty();
   }

   /*
    * DEPRECATED
    */
   public void takeTurn(Tile newTile, ArrayList<TileSet> pool)
   {
      this.addTile(newTile);
      this.sortUnorderedTiles();

      ArrayList<TileSet> holdingSets = produceTileSetUnordered();
      boolean canPlay = this.isOnTheBoard;
      if (!this.isOnTheBoard)
      {
         int value = (int) holdingSets.stream().collect(Collectors.summarizingInt(TileSet::getTotalValue)).getSum();
         if (value >= 30)
         {
            canPlay = true;
            this.isOnTheBoard = true;
         }
      }
      if (canPlay && !holdingSets.isEmpty())
      {
         pool.addAll(holdingSets);
         System.out.println(
               this.name + " PLAYED " + holdingSets.stream().map(tile -> tile.print()).collect(Collectors.joining(" "))
         );
         for (TileSet tileSet : holdingSets)
         {
            this.holdingTiles.removeAll(tileSet.getTiles());
         }
      }
      if (this.isOnTheBoard)
      {
         manipulateTheBoard(pool);
      }
   }

   public void takeNewTurn(Tile newTile, ArrayList<TileSet> pool)
   {
      if (newTile != null)
      {
         System.out.println(this.name + " DREW " + newTile.print());
         this.addTile(newTile);
      }
      this.sortUnorderedTiles();

      ArrayList<IntermediateGameState> solutions;
      if (!this.isOnTheBoard)
      {
         solutions = SolverUtil.findOptimalSolutions(
               new ArrayList<TileSet>(),
               new ArrayList<TileSet>(),
               this.holdingTiles
         );
      }
      else
      {
         solutions = SolverUtil.findOptimalSolutions(
               pool,
               new ArrayList<TileSet>(),
               this.holdingTiles
         );
      }
      IntermediateGameState bestSolution = solutions.isEmpty() ? null : solutions.get(solutions.size() - 1);
      if (bestSolution != null)
      {
         boolean canPlay = this.isOnTheBoard;
         if (!this.isOnTheBoard)
         {
            int value = (int) bestSolution.pool.stream().collect(Collectors.summarizingInt(TileSet::getTotalValue))
                  .getSum();
            if (value >= 30)
            {
               canPlay = true;
               // add the current pool back into this one
               bestSolution.pool.addAll(pool);
               this.isOnTheBoard = true;
            }
         }
         if (canPlay)
         {
            pool.clear();
            pool.addAll(bestSolution.pool);
            HashSet<Tile> holdingSet = new HashSet<Tile>(this.holdingTiles);
            holdingSet.removeAll(bestSolution.holdingTiles);
            System.out.println(
                  this.name + " PLAYED " + holdingSet.stream().map(tile -> tile.print()).collect(Collectors.joining(" "))
            );
            this.holdingTiles.clear();
            this.holdingTiles.addAll(bestSolution.holdingTiles);
         }
      }
   }

   public boolean getIsOnTheBoard()
   {
      return this.isOnTheBoard;
   }

   public String print()
   {
      String onTheBoard = this.isOnTheBoard ? "yes" : "no";
      return this.name + " (" + onTheBoard + ") \n"
            + this.holdingTiles.stream().map(tile -> tile.print()).collect(Collectors.joining(" "));
   }

   /*
    * DEPRECATED
    */
   public ArrayList<TileSet> produceTileSetUnordered()
   {
      // go through unodered tile set and
      // see if any set can be created
      // for each tile, see if a tileset can be created
      ArrayList<TileSet> pairs = this.findAllStartingPairs(
            this.holdingTiles,
            this.holdingTiles
      );

      ArrayList<TileSet> validSets = new ArrayList<TileSet>();
      for (TileSet testingPair : pairs)
      {
         ArrayList<Tile> testingTiles = testingPair.getTiles();
         for (Tile thirdTile : this.holdingTiles)
         {
            TileSet testSet = TileSet.testTileSet(
                  new ArrayList<Tile>(Arrays.asList(testingPair.getTiles().get(0),
                        testingPair.getTiles().get(1),
                        thirdTile
                  )),
                  true
            );
            if (testSet != null)
            {
               boolean notSeen = true;
               for (TileSet validSet : validSets)
               {
                  notSeen &= !validSet.print().equals(testSet.print());
               }
               if (notSeen)
               {
                  validSets.add(testSet);
               }
            }
         }
      }
      ArrayList<TileSet> reducedSets = new ArrayList<TileSet>();
      for (TileSet valid : validSets)
      {
         boolean add = true;
         for (int i = 0; i < reducedSets.size(); i++)
         {
            TileSet existing = reducedSets.get(i);
            if (valid.equals(existing))
            {
               add = false;
               continue;
            }
            if (valid.overlapsWith(existing) && valid.getTotalValue() > existing.getTotalValue())
            {
               add = false;
               reducedSets.set(
                     i,
                     valid
               );
            }
         }
         if (add)
         {
            reducedSets.add(valid);
         }
      }
      return reducedSets;
   }

   /*
    * DEPRECATED
    */
   public void manipulateTheBoard(ArrayList<TileSet> pool)
   {
      boolean continueMakingMoves = true;
      while (continueMakingMoves)
      {
         boolean changesHaveBeenMade = false;
         for (TileSet tileSet : pool)
         {
            // simple adding to the end
            Tile foundTile = null;
            for (Tile tile : this.holdingTiles)
            {
               TileSet newSet = tileSet.testNewTile(tile);
               if (newSet != null)
               {
                  foundTile = tile;
                  break;
               }
            }
            if (foundTile != null)
            {
               pool.remove(tileSet);
               pool.add(tileSet.testNewTile(foundTile));
               this.holdingTiles.remove(foundTile);
               System.out.println(this.name + " ADDED TILE " + foundTile.print() + " to " + tileSet.print());
               changesHaveBeenMade = true;
               break;
            }

            // test for splits
            Tile foundSplitTile = null;
            ArrayList<TileSet> newSet = null;
//            for (Tile tile : this.holdingTiles)
//            {
//               newSet = tileSet.testNewTileSplit(TileSet.getSinglet(tile));
//               if (newSet != null)
//               {
//                  foundSplitTile = tile;
//                  break;
//               }
//            }
//            if (foundSplitTile != null)
//            {
//               pool.remove(tileSet);
//               pool.addAll(newSet);
//               this.holdingTiles.remove(foundTile);
//               System.out
//                     .println(this.name + " SPLIT " + tileSet.print() + " TO " + newSet.get(0).print() + " & " + newSet.get(1).print());
//               changesHaveBeenMade = true;
//               break;
//            }

            // take tiles from end
            ArrayList<Tile> allAvailableTiles = new ArrayList<Tile>();
            TileSet testSet;
            boolean continueOn = true;
            // test against all possible splits
            for (TileSet publicTileSet : pool)
            {
               for (TileSet[] splitPair : publicTileSet.possibleSplitPairs())
               {
                  // just 1 level
                  TileSet leftSet = splitPair[0];
                  TileSet rightSet = splitPair[1];
                  for (TileSet bestGroup : this.organizeIntoBestSets())
                  {
                     testSet = leftSet.testNewTileSet(bestGroup);
                     if (testSet != null && rightSet.isValid(true))
                     {
                        // apply left
                        System.out.println(
                              this.name + " JOINED " + leftSet.print() + " WITH " + bestGroup.print() + " LEAVING "
                                    + rightSet.print()
                        );
                        pool.remove(publicTileSet);
                        pool.add(rightSet);
                        pool.add(testSet);
                        this.holdingTiles.removeAll(bestGroup.getTiles());
//                        pool.addAll(newSet);
//                        this.holdingTiles.remove(foundTile);
//                        System.out
//                              .println(this.name + " SPLIT " + tileSet.print() + " TO " + newSet.get(0).print() + " & " + newSet.get(1).print());
                        changesHaveBeenMade = true;
                        continueOn = false;
                        break;
                     }
                     testSet = leftSet.testNewTileSet(bestGroup);
                     if (testSet != null && leftSet.isValid(true))
                     {
                        // apply right
                        // apply left
                        System.out.println(
                              this.name + " JOINED " + rightSet.print() + " WITH " + bestGroup.print() + " LEAVING "
                                    + leftSet.print()
                        );
                        pool.remove(publicTileSet);
                        pool.add(leftSet);
                        pool.add(testSet);
                        this.holdingTiles.removeAll(bestGroup.getTiles());
//                        pool.addAll(newSet);
//                        this.holdingTiles.remove(foundTile);
//                        System.out
//                              .println(this.name + " SPLIT " + tileSet.print() + " TO " + newSet.get(0).print() + " & " + newSet.get(1).print());
                        changesHaveBeenMade = true;
                        continueOn = false;
                        break;
                     }
                  }
                  if (!continueOn)
                  {
                     break;
                  }
               }
               if (!continueOn)
               {
                  break;
               }
            }
            if (!continueOn)
            {
               break;
            }
         }
         if (!changesHaveBeenMade)
         {
            continueMakingMoves = false;
         }
      }
      // check all sets against own hand
      // if can make a move, do it?
      // repeat until no more moves have been found
   }

   public ArrayList<TileSet> findAllStartingPairs(ArrayList<Tile> baseTiles, ArrayList<Tile> testTiles)
   {
      ArrayList<TileSet> pairs = new ArrayList<TileSet>();

      Tile tile;
      TileSet testPair;
      for (int i = 0; i < baseTiles.size(); i++)
      {
         tile = baseTiles.get(i);
         for (int j = 0; j < testTiles.size(); j++)
         {
            if (j == i)
            {
               continue;
            }
            testPair = TileSet.testPair(
                  baseTiles.get(i),
                  testTiles.get(j)
            );
            if (testPair != null)
            {
               pairs.add(testPair);
            }
         }
      }
      return pairs;
   }

   public ArrayList<TileSet> organizeIntoBestSets()
   {
      ArrayList<TileSet> groups = new ArrayList<TileSet>();

      Tile tile;
      TileSet testPair;
      for (int i = 0; i < this.holdingTiles.size(); i++)
      {
         tile = this.holdingTiles.get(i);
         for (int j = 0; j < this.holdingTiles.size(); j++)
         {
            if (j == i)
            {
               continue;
            }
            testPair = TileSet.testPair(
                  this.holdingTiles.get(i),
                  this.holdingTiles.get(j)
            );
            if (testPair != null)
            {
               groups.add(testPair);
            }
         }
      }
      for (Tile holdingTile : this.holdingTiles)
      {
         groups.add(TileSet.getSinglet(holdingTile));
      }
      return groups;
   }
}
