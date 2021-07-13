package rummykub.v2.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import rummykub.v2.dataStructures.Tile;
import rummykub.v2.dataStructures.TileSet;
import rummykub.v2.enums.Color;
import rummykub.v2.enums.SetType;

/**
 * @author lucasbrambrink
 *
 */
public class TileSetUtil
{

   public static boolean isValid(TileSet tileSet)
   {
      return isValidRun(tileSet) || isValidGroup(tileSet);
   }

   public static boolean isValidRun(TileSet tileSet)
   {
      boolean isValidRun = true;
      Tile previousTile = tileSet.tiles.get(0);
      Tile tile;
      Color runColor = previousTile.color;
      for (int i = 1; i < tileSet.tiles.size(); i++)
      {
         tile = tileSet.tiles.get(i);
         // check that the numbers are incrementing in sequence

         if (tile.color != Color.JOKER)
         {
            if (runColor == Color.JOKER && tile.color != Color.JOKER)
            {
               runColor = tile.color;
            }
            isValidRun &= previousTile.value + 1 == tile.value || tile.color == Color.JOKER
                  || previousTile.color == Color.JOKER;
            // and all the colors are the same
            isValidRun &= runColor == tile.color || tile.color == Color.JOKER || previousTile.color == Color.JOKER;
         }
         if (!isValidRun)
         {
            return false;
         }
         previousTile = tile;
      }
      return true;
   }

   public static boolean isValidGroup(TileSet tileSet)
   {
      boolean isValidGroup = true;
      Tile previousTile = tileSet.tiles.get(0);
      HashSet<Color> seenColors = new HashSet<Color>();
      seenColors.add(previousTile.color);
      Tile tile;
      for (int i = 1; i < tileSet.tiles.size(); i++)
      {
         tile = tileSet.tiles.get(i);
         if (tile.color != Color.JOKER)
         {
            // check that the number is the same
            isValidGroup &= previousTile.value == tile.value || tile.color == Color.JOKER
                  || previousTile.color == Color.JOKER;
            // but all the colors are different
            isValidGroup &= seenColors.add(tile.color);
         }
         if (!isValidGroup)
         {
            return false;
         }
         previousTile = tileSet.tiles.get(i);
      }
      return true;
   }

   public static boolean tileIsFreelyAvailable(TileSet tileSet, Tile tile)
   {
      if (tileSet.size() == 1)
      {
         return true;
      }
      if (tileSet.size() == 2 || tileSet.size() == 3)
      {
         return false;
      }
      if (tileSet.setType == SetType.GROUP)
      {
         return tileSet.size() == 4;
      }
      else if (tileSet.setType == SetType.RUN)
      {
         int index = tileSet.tiles.indexOf(tile);
         return index == 0 || index == tileSet.size() - 1;
      }
      else
      {
         return false;
      }
   }

   public static TileSet removeTile(TileSet tileSet, Tile tile)
   {
      int initialIndex = tileSet.tiles.indexOf(tile);
      if (initialIndex == -1)
      {
         throw new IllegalArgumentException("Unable to find tile");
      }
      tileSet.tiles.remove(tile);

      // if length 0 or 1, or it was the first or last index, did not create a new set
      if (tileSet.size() < 2 || initialIndex == 0 || initialIndex == tileSet.size())
      {
         tileSet.sort();
         return null;
      }
      // create a new list
      TileSet newTileSet = new TileSet(
            new ArrayList<Tile>(
                  tileSet.tiles.subList(
                        0,
                        initialIndex
                  )
            )
      );
      // update existing tileSet by removing all left ones
      tileSet.tiles = new ArrayList<Tile>(
            tileSet.tiles.subList(
                  initialIndex,
                  tileSet.tiles.size()
            )
      );
      tileSet.sort();
      return newTileSet;
   }

   public static TileSet[] splitAndMerge(TileSet set, TileSet tileSetToMerge)
   {
      // 12345 + 3 => 123 345
      // 12345 + 34 => 1234 345
      if (tileSetToMerge.setType == SetType.GROUP)
      {
         return null;
      }
      int splitIndex = -1;
      for (int i = 0; i < set.size(); i++)
      {
         if (TileUtil.tilesEqual(
               set.tiles.get(i),
               tileSetToMerge.tiles.get(0)
         ))
         {
            splitIndex = i;
            break;
         }
      }
      // can only do this for sets of 5 or more where the split point is between the
      // 3rd and 3rd last index
      if (set.size() < 5 || splitIndex < 2 || splitIndex + 3 > set.size())
      {
         return null;
      }
      ArrayList<Tile> leftTiles = new ArrayList<Tile>(
            set.tiles.subList(
                  0,
                  splitIndex
            )
      );
      leftTiles.addAll(tileSetToMerge.tiles);
      ArrayList<Tile> rightTiles = new ArrayList<Tile>(
            set.tiles.subList(
                  splitIndex,
                  set.tiles.size()
            )
      );
      return new TileSet[]
      {
            new TileSet(leftTiles),
            new TileSet(rightTiles)
      };
   }

   public static TileSet[] splitDoublet(TileSet set)
   {
      if (set.size() != 2)
      {
         throw new IllegalArgumentException("Splitting doublet can only split sets of length 2");
      }
      return new TileSet[]
      {
            new TileSet(
                  new ArrayList<Tile>(
                        set.tiles.subList(
                              0,
                              1
                        )
                  )
            ),
            new TileSet(
                  new ArrayList<Tile>(
                        set.tiles.subList(
                              1,
                              2
                        )
                  )
            )
      };
   }

   public static TileSet mergeTileSets(TileSet set1, TileSet set2)
   {
      if (set1 == null || set2 == null)
      {
         System.out.println("WTF IS GOING ON");
      }
      ArrayList<Tile> mergedTiles = new ArrayList<Tile>(set1.tiles);
      mergedTiles.addAll(set2.tiles);
      try
      {
         return new TileSet(mergedTiles);
      }
      catch (IllegalArgumentException e)
      {
         return null;
      }
   }

   public static SetType getSetType(TileSet tileSet)
   {
      if (tileSet.tiles.size() == 1)
      {
         return SetType.UNDEFINED;
      }
      else if (isValidRun(tileSet))
      {
         return SetType.RUN;
      }
      else if (isValidGroup(tileSet))
      {
         return SetType.GROUP;
      }
      else
      {
         throw new IllegalArgumentException("Invalid Set");
      }
   }

   public static int getTotalValue(TileSet tileSet)
   {
      return (int) tileSet.tiles.stream().collect(Collectors.summarizingInt((Tile t) -> t.value)).getSum();
   }

   public static int getSetColor(TileSet tileSet)
   {
      if (tileSet.setType == SetType.RUN || tileSet.tiles.size() == 1)
      {
         return tileSet.tiles.get(0).color.ordinal();
      }
      return -1;
   }

   public static TileSet clone(TileSet tileSet)
   {
      return new TileSet(
            new ArrayList<Tile>(tileSet.tiles),
            tileSet.setType
      );
   }

   public static String serialize(TileSet tileSet)
   {
      return tileSet.tiles.stream().map(tile -> TileUtil.serialize(tile)).collect(Collectors.joining(""));
   }

   public static ArrayList<Tile> getComplements(TileSet tileSet)
   {
      if (tileSet.setType == SetType.RUN)
      {
         return getRunComplement(tileSet);
      }
      else if (tileSet.setType == SetType.GROUP)
      {
         return getGroupComplement(tileSet);
      }
      else
      {
         ArrayList<Tile> complements = getRunComplement(tileSet);
         complements.addAll(getGroupComplement(tileSet));
         // don't need to add joker twice
         complements.remove(complements.size() - 1);
         return complements;
      }
   }

   private static ArrayList<Tile> getRunComplement(TileSet tileSet)
   {
      Tile lowerBound = tileSet.tiles.get(0);
      Tile upperBound = tileSet.tiles.get(tileSet.tiles.size() - 1);
      ArrayList<Tile> complements = new ArrayList<Tile>();
      if (lowerBound.value != 1)
      {
         complements.add(
               new Tile(
                     lowerBound.color,
                     lowerBound.value - 1
               )
         );
      }
      if (upperBound.value != 13)
      {
         complements.add(
               new Tile(
                     upperBound.color,
                     upperBound.value + 1
               )
         );
      }
      complements.add(
            new Tile(
                  Color.JOKER,
                  0
            )
      );
      return complements;
   }

   private static ArrayList<Tile> getGroupComplement(TileSet tileSet)
   {
      Tile lowerBound = tileSet.tiles.get(0);
      HashSet<Color> seenColors = new HashSet<Color>(
            tileSet.tiles.stream().map((Tile t) -> t.color).collect(Collectors.toList())
      );
      ArrayList<Tile> complements = new ArrayList<Tile>();
      for (Color color : Color.values())
      {
         if (!seenColors.contains(color))
         {
            complements.add(
                  new Tile(
                        color,
                        lowerBound.value
                  )
            );
         }
      }
      complements.add(
            new Tile(
                  Color.JOKER,
                  0
            )
      );
      return complements;
   }
}
