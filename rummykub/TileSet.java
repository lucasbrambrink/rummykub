/**
 * 
 */
package rummykub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lucasbrambrink
 *
 */
public class TileSet
{
   private ArrayList<Tile> tiles;
   private SetType setType;

   private TileSet(ArrayList<Tile> tiles)
   {
      this.tiles = tiles;
      Collections.sort(
            this.tiles,
            Comparator.comparing(Tile::getValue).thenComparing(Tile::getColor)
      );
      this.setSetType();
   }

   public int getSetType()
   {
      return this.setType == SetType.RUN ? 1 : 0;
   }

   public int getSetColor()
   {
      if (this.setType == SetType.RUN || this.tiles.size() == 1)
      {
         return this.tiles.get(0).getColor().ordinal();
      }
      return -1;
   }

   public int getLength()
   {
      return this.tiles.size();
   }

   public static TileSet createTileSet(ArrayList<Tile> tiles)
   {
      TileSet tileSet = new TileSet(tiles);
      if (!tileSet.isValid(true))
      {
         throw new IllegalArgumentException("Tile set not valid");
      }
      return tileSet;
   }

   public static TileSet testTileSet(TileSet tiles, TileSet otherTiles, boolean enforceLength)
   {
      if (tiles == null || tiles.getTiles().isEmpty())
         return null;
      ArrayList<Tile> newTileSet = new ArrayList<Tile>();
      newTileSet.addAll(tiles.getTiles());
      newTileSet.addAll(otherTiles.getTiles());
      TileSet tileSet = new TileSet(newTileSet);
      if (tileSet.isValid(enforceLength))
      {
         return tileSet;
      }
      return null;
   }

   public static TileSet testTileSet(ArrayList<Tile> tiles, boolean enforceLength)
   {
      if (tiles == null || tiles.isEmpty())
         return null;
      TileSet tileSet = new TileSet(tiles);
      if (tileSet.isValid(enforceLength))
      {
         return tileSet;
      }
      return null;
   }

   public static TileSet testPair(Tile tileOne, Tile tileTwo)
   {
      TileSet tileSet = new TileSet(
            new ArrayList<Tile>(
                  Arrays.asList(

                        tileOne,
                        tileTwo
                  )
            )
      );
      if (tileSet.isValid(false))
      {
         return tileSet;
      }
      return null;
   }

   public TileSet[] splitBefore(Tile splitBefore)
   {
      // split at that index
      int index = this.tiles.indexOf(splitBefore);
      ArrayList<Tile> leftSet = new ArrayList<Tile>(
            this.tiles.subList(
                  0,
                  index
            )
      );
      ArrayList<Tile> rightSet;
      if (index + 1 == this.tiles.size())
      {
         rightSet = null;
      }
      else
      {
         rightSet = new ArrayList<Tile>(
               this.tiles.subList(
                     index + 1,
                     this.tiles.size()
               )
         );
      }
      return new TileSet[]
      {
            TileSet.testTileSet(
                  leftSet,
                  false
            ),
            TileSet.testTileSet(
                  rightSet,
                  false
            ),
      };
   }

   public static TileSet getSinglet(Tile startingTile)
   {
      return new TileSet(new ArrayList<Tile>(Arrays.asList(startingTile)));
   }

   public ArrayList<Tile> neededTiles()
   {
      ArrayList<Tile> neededTiles = new ArrayList<Tile>();
      // only return tiles if this is incomplete
      if (this.isValidLength())
      {
         return neededTiles;
      }

      Tile lastTile = this.tiles.get(this.tiles.size() - 1);
      Tile firstTile = this.tiles.get(0);
      switch (this.setType)
      {
      case RUN:
         if (lastTile.getValue() != 13)
         {
            neededTiles.add(
                  new Tile(
                        lastTile.getColor(),
                        lastTile.getValue() + 1
                  )
            );
         }
         if (firstTile.getValue() != 1)
         {
            neededTiles.add(
                  new Tile(
                        firstTile.getColor(),
                        firstTile.getValue() - 1
                  )
            );
         }
         break;
      case GROUP:
         Set<Color> seenColors = this.tiles.stream().map(tile -> tile.getColor()).collect(Collectors.toSet());
         for (Color color : Color.values())
         {
            if (!seenColors.contains(color))
            {
               neededTiles.add(
                     new Tile(
                           color,
                           firstTile.getValue()
                     )
               );
            }
         }
         break;
      }
      return neededTiles;
   }

   public TileSet testNewTile(Tile newTile)
   {
      ArrayList<Tile> testSet = new ArrayList<Tile>(this.tiles);
      testSet.add(newTile);
      TileSet tileSet = new TileSet(testSet);
      if (tileSet.isValid(true))
      {
         tileSet.setSetType();
         return tileSet;
      }
      return null;
   }

   public ArrayList<TileSet[]> possibleSplitPairs()
   {
      // return all the pairs that this can be broken into
      // 12345 -> 1,2345; 12,235; 123,45; 1234,5;
      ArrayList<TileSet[]> possibleSplits = new ArrayList<TileSet[]>();
      for (int i = 1; i < this.tiles.size() - 1; i++)
      {
         // split at that index
         ArrayList<Tile> leftSet = new ArrayList<Tile>(
               this.tiles.subList(
                     0,
                     i
               )
         );
         ArrayList<Tile> rightSet = new ArrayList<Tile>(
               this.tiles.subList(
                     i,
                     this.tiles.size()
               )
         );
         possibleSplits.add(new TileSet[]
         {
               new TileSet(leftSet),
               new TileSet(rightSet)
         });
      }
      return possibleSplits;
   }

   public ArrayList<TileSet> testNewTileSplit(TileSet mergingTileSet)
   {
      for (int index = 0; index < this.tiles.size(); index++)
      {
         // split at that index
         ArrayList<Tile> leftSet = new ArrayList<Tile>(
               this.tiles.subList(
                     0,
                     index
               )
         );
         leftSet.addAll(mergingTileSet.getTiles());
         ArrayList<Tile> rightSet = new ArrayList<Tile>(
               this.tiles.subList(
                     index,
                     this.tiles.size()
               )
         );
         TileSet leftTileSet = new TileSet(leftSet);
         TileSet rightTileSet = new TileSet(rightSet);
         if (leftTileSet.isValid(true) && rightTileSet.isValid(true))
         {
            return new ArrayList<TileSet>(
                  Arrays.asList(
                        leftTileSet,
                        rightTileSet
                  )
            );
         }
         // split at that index
         leftSet = new ArrayList<Tile>(
               this.tiles.subList(
                     0,
                     index
               )
         );
         rightSet = new ArrayList<Tile>(
               this.tiles.subList(
                     index,
                     this.tiles.size()
               )
         );
         rightSet.addAll(mergingTileSet.getTiles());
         leftTileSet = new TileSet(leftSet);
         rightTileSet = new TileSet(rightSet);
         if (leftTileSet.isValid(true) && rightTileSet.isValid(true))
         {
            return new ArrayList<TileSet>(
                  Arrays.asList(
                        leftTileSet,
                        rightTileSet
                  )
            );
         }
      }
      return null;
   }

   public TileSet testNewTileSet(TileSet mergingTileSet)
   {
      // split at that index
      ArrayList<Tile> leftSet = new ArrayList<Tile>(this.tiles);
      leftSet.addAll(mergingTileSet.getTiles());
      TileSet leftTileSet = new TileSet(leftSet);
      if (leftTileSet.isValid(true))
      {
         return leftTileSet;
      }
      return null;
   }

   public ArrayList<Tile> getTiles()
   {
      return this.tiles;
   }

   public boolean isValid(boolean enforceLength)
   {
      return (isValidLength() || !enforceLength) && (isValidRun() || isValidGroup());
   }

   public void setSetType()
   {
      if (this.tiles.size() == 1)
      {
         this.setType = null;
      }
      else if (this.isValidRun())
      {
         this.setType = SetType.RUN;
      }
      else if (this.isValidGroup())
      {
         this.setType = SetType.GROUP;
      }
      else
      {
         this.setType = null;
      }
   }

   public int getTotalValue()
   {
      return (int) this.tiles.stream().collect(Collectors.summarizingInt(Tile::getValue)).getSum();
   }

   public String print()
   {
      return this.tiles.stream().map(tile -> tile.print()).collect(Collectors.joining(" "));
   }

   private boolean isValidLength()
   {
      return this.tiles.size() > 2;
   }

   public boolean overlapsWith(TileSet otherTileSet)
   {
      for (Tile tile : this.getTiles())
      {
         for (Tile otherTile : otherTileSet.getTiles())
         {
            if (tile.equals(otherTile))
            {
               return true;
            }
         }
      }
      return false;
   }

   private boolean isValidRun()
   {
//      return false;
      boolean isValidRun = true;
      Tile previousTile = this.tiles.get(0);
      Tile tile;
      Color runColor = previousTile.getColor();
      for (int i = 1; i < this.tiles.size(); i++)
      {
         tile = this.tiles.get(i);
         // check that the numbers are incrementing in sequence
         isValidRun &= previousTile.getValue() + 1 == tile.getValue();
         // and all the colors are the same
         isValidRun &= runColor == tile.getColor();
         previousTile = tile;
      }
      return isValidRun;
   }

   private boolean isValidGroup()
   {
      boolean isValidGroup = true;
      Tile previousTile = this.tiles.get(0);
      HashSet<Color> seenColors = new HashSet<Color>();
      seenColors.add(previousTile.getColor());
      for (int i = 1; i < this.tiles.size(); i++)
      {
         // check that the number is the same
         isValidGroup &= previousTile.getValue() == this.tiles.get(i).getValue();
         // but all the colors are different
         isValidGroup &= seenColors.add(this.tiles.get(i).getColor());
         previousTile = this.tiles.get(i);
      }
      return isValidGroup;
   }

   private enum SetType
   {
      RUN,
      GROUP,
   }
}
