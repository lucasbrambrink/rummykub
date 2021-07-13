/**
 * 
 */
package rummykub.v2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author lucasbrambrink
 *
 */
public class GameStateFileParser
{
   public static GameState load(String fileName)
   {
      return loadGameState(readFromFile(fileName));
   }

   public static GameState loadGameState(ArrayList<String> rows)
   {
      ArrayList<TileSet> newPoolState = new ArrayList<TileSet>();
      ArrayList<TileSet> newIntermediatePoolDoubles = new ArrayList<TileSet>();
      ArrayList<TileSet> newIntermediatePoolSingles = new ArrayList<TileSet>();
      ArrayList<TileSet> newHoldingTilesSets = new ArrayList<TileSet>();

      ArrayList<TileSet> tileSetToAdd = null;
      for (String row : rows)
      {
         if (row.contains(POOL))
         {
            tileSetToAdd = newPoolState;
            continue;
         }
         else if (row.contains(INTERMEDIATE_DOUBLE))
         {
            tileSetToAdd = newIntermediatePoolDoubles;
            continue;
         }
         else if (row.contains(INTERMEDIATE_SINGLE))
         {
            tileSetToAdd = newIntermediatePoolSingles;
            continue;
         }
         else if (row.contains(HOLDING))
         {
            tileSetToAdd = newHoldingTilesSets;
            continue;
         }
         ArrayList<Tile> set = new ArrayList<Tile>();
         Color previousColor = null;
         for (String tileString : row.split(" "))
         {
            Tile tile = TileUtil.parse(
                  tileString,
                  previousColor
            );
            set.add(tile);
            if (tile.color != Color.JOKER)
            {
               previousColor = tile.color;
            }
         }
         TileSet tileSet = new TileSet(set);
         tileSet.setType = TileSetUtil.getSetType(tileSet);
         tileSet.sort();
         tileSetToAdd.add(tileSet);
      }
      return new GameState(
            newPoolState,
            newIntermediatePoolDoubles,
            newIntermediatePoolSingles,
            newHoldingTilesSets
      );
   }

   public static ArrayList<String> readFromFile(String fileName)
   {
      ArrayList<String> sequences = new ArrayList<String>();
      try
      {
         File inputFile = new File(fileName);
         Scanner fileReader = new Scanner(inputFile);
         while (fileReader.hasNextLine())
         {
            String lineValue = fileReader.nextLine().trim();
            if (lineValue.equals(""))
            {
               continue;
            }
            sequences.add(lineValue);
         }
         fileReader.close();
      }
      catch (FileNotFoundException e)
      {
         System.out.println("Unable to locate file: " + fileName);
      }
      return sequences;
   }

   public static final String POOL = "POOL";
   public static final String INTERMEDIATE_DOUBLE = "INTERMEDIATE_DOUBLE";
   public static final String INTERMEDIATE_SINGLE = "INTERMEDIATE_SINGLE";
   public static final String HOLDING = "HOLDING";
}
