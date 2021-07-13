/**
 * 
 */
package rummykub.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author lucasbrambrink
 *
 */
public class PrintUtil
{
   public static final boolean PRINT = Solver.PRINT;
   public static final int STATE_WIDTH = 20;
   public static final int MAX_PER_ROW = 10;

   public static void printGameState(GameState state)
   {
      System.out.println(serialize(state));
   }

   public static void print(String stringToPrint)
   {
      if (PRINT)
      {
         System.out.println(stringToPrint);
      }
   }

   public static String serialize(GameState state)
   {
      return serialize(
            state,
            1
      );
   }

   public static String serialize(GameState state, int depth)
   {
      return serialize(
            state,
            depth,
            (ts) -> tileSetToString(ts),
            (t) -> tileToString(t)
      );
   }

   public static String serialize(
         GameState state, int depth, TileSetStringFunction tileSetStringFunction, TileStringFunction tileStringFunction
   )
   {
      String tileSetToAdd = state.tileSetToAdd == null ? "" : tileSetStringFunction.run(state.tileSetToAdd);
      String foundTile = state.foundTile == null ? "" : " TO " + tileStringFunction.run(state.foundTile);
      return String.join(
            " ",
            Arrays.asList(
                  "ADDING " + tileSetToAdd + foundTile,
                  "\n" + "*".repeat(10),
                  "\n" + "  ".repeat(depth - 1) + "POOL",
                  "\n" + "  ".repeat(depth - 1) + state.publicPool.stream().map(ts -> tileSetStringFunction.run(ts)).collect(Collectors.joining("\n" + "  ".repeat(depth - 1))),
                  "\n" + "  ".repeat(depth - 1) + "INTERMEDIATE 2x",
                  "\n" + "  ".repeat(depth - 1) + state.intermediatePoolDoubles.stream().map(ts -> tileSetStringFunction.run(ts)).collect(Collectors.joining("\n" + "  ".repeat(depth - 1))),
                  "\n" + "  ".repeat(depth - 1) + "INTERMEDIATE 1x",
                  "\n" + "  ".repeat(depth - 1) + state.intermediatePoolSingles.stream().map(ts -> tileSetStringFunction.run(ts)).collect(Collectors.joining("\n" + "  ".repeat(depth - 1))),
                  "\n" + "  ".repeat(depth - 1) + "HOLDING",
                  "\n" + "  ".repeat(depth - 1) + state.holdingTiles.stream().map(ts -> tileSetStringFunction.run(ts)).collect(Collectors.joining("\n" + "  ".repeat(depth - 1))),
                  "\n" + "*".repeat(10) + "\n"
            )
      );
   }

   public static void printSideBySide(GameState initialState, GameState result)
   {
      String[] initialStateSerializedLines = serialize(initialState).split("\n");
      String[] resultSerializedLines = serialize(result).split("\n");
      String[] initialStateSerializedLinesWithoutColors = serialize(
            initialState,
            1,
            (ts) -> tileSetToStringNoColor(ts),
            (t) -> tileToStringNoColor(t)
      ).split("\n");
      int minLength = Math.min(
            initialStateSerializedLines.length,
            resultSerializedLines.length
      );
      ArrayList<String> rows = new ArrayList<String>();
      int i = 0;
      for (i = 0; i < minLength; i++)
      {
         String leftLine = initialStateSerializedLines[i];
         String left = leftLine + " ".repeat(STATE_WIDTH - initialStateSerializedLinesWithoutColors[i].length());
         rows.add(left + resultSerializedLines[i] + "\n");
      }
      boolean leftIsLonger = initialStateSerializedLines.length > resultSerializedLines.length;
      String[] tail = leftIsLonger ? initialStateSerializedLines : resultSerializedLines;
      for (i = minLength; i < tail.length; i++)
      {
         String padLeft = leftIsLonger ? "" : " ".repeat(STATE_WIDTH);
         rows.add(padLeft + tail[i] + "\n");
      }
      System.out.println(
            String.join(
                  "",
                  rows
            )
      );
   }

   public static void printChainSideBySide(GameState result)
   {
      // iterate through all to get to start
      // keep track of the "tallest" one -> that'll determine the row size
      ArrayList<String[][]> serializedStates = new ArrayList<String[][]>();
      GameState state = result;
      int maxHeight = 0;
      int numberOfStates = 0;
      while (state != null)
      {
         String[] serializedLines = serialize(state).split("\n");
         if (serializedLines.length > maxHeight)
         {
            maxHeight = serializedLines.length;
         }
         String[] serializedLinesWithoutColors = serialize(
               state,
               1,
               (ts) -> tileSetToStringNoColor(ts),
               (t) -> tileToStringNoColor(t)
         ).split("\n");
         serializedStates.add(new String[][]
         {
               serializedLines,
               serializedLinesWithoutColors,
         });
         state = state.previousState;
         numberOfStates++;
      }
      Collections.reverse(serializedStates);

      int numberOfStateRows = (int) Math.ceil((double) numberOfStates / MAX_PER_ROW);
      String[][] stateRows = new String[numberOfStateRows][];
      for (int j = 0; j < numberOfStateRows; j++)
      {
         String[] rows = new String[maxHeight];
         // seed with an empty string
         for (int i = 0; i < maxHeight; i++)
         {
            rows[i] = "";
         }
         stateRows[j] = rows;
      }
      int counter = 0;
      int stateRowIndex = 0;
      String[] stateRow = stateRows[0];
      for (String[][] serializedStateArray : serializedStates)
      {
         if (counter == MAX_PER_ROW)
         {
            stateRowIndex++;
            counter = 0;
            for (int i = 0; i < maxHeight; i++)
            {
               stateRow[i] += "\n";
            }
            stateRow = stateRows[stateRowIndex];
         }
         for (int i = 0; i < maxHeight; i++)
         {
            if (i >= serializedStateArray[0].length)
            {
               stateRow[i] += " ".repeat(STATE_WIDTH);
            }
            else
            {
               String leftLine = serializedStateArray[0][i];
               String left = leftLine + " ".repeat(STATE_WIDTH - serializedStateArray[1][i].length());
               stateRow[i] += left;
            }
         }
         counter++;
      }
      for (int i = 0; i < maxHeight; i++)
      {
         stateRow[i] += "\n";
      }
      for (String[] rows : stateRows)
      {
         System.out.println(
               String.join(
                     "",
                     rows
               )
         );
      }
   }

   public static void printTileSet(TileSet tileSet)
   {
      System.out.println(tileSetToString(tileSet));
   }

   public static String tileSetToString(TileSet tileSet)
   {
      if (tileSet.size() == 0)
      {
         return "[]";
      }
      return tileSet.tiles.stream().map(tile -> tileToString(tile)).collect(Collectors.joining(" "));
   }

   public static String tileSetToStringNoColor(TileSet tileSet)
   {
      return tileSet.tiles.stream().map(tile -> tileToStringNoColor(tile)).collect(Collectors.joining(" "));
   }

   public static String tileToString(Tile tile)
   {
      if (tile.color == Color.JOKER)
      {
         return getTileColorCode(tile) + "J" + ANSI_RESET;
      }
      return getTileColorCode(tile) + tile.value + ANSI_RESET;
   }

   public static String tileToStringNoColor(Tile tile)
   {
      if (tile.color == Color.JOKER)
      {
         return "J";
      }
      return String.valueOf(tile.value);
   }

   private static String getTileColorCode(Tile tile)
   {
      switch (tile.color)
      {
      case RED:
         return ANSI_RED;
      case BLUE:
         return ANSI_BLUE;
      case BLACK:
         return ANSI_WHITE_BACKGROUND + ANSI_BLACK;
      case YELLOW:
         return ANSI_YELLOW;
      case JOKER:
         return ANSI_GREEN;
      default:
         return "";
      }

   }

   private interface TileSetStringFunction
   {
      String run(TileSet tileSet);
   }

   private interface TileStringFunction
   {
      String run(Tile tile);
   }

   private static final String ANSI_RESET = "\u001B[0m";
   private static final String ANSI_BLACK = "\u001B[30m";
   private static final String ANSI_RED = "\u001B[31m";
   private static final String ANSI_GREEN = "\u001B[32m";
   private static final String ANSI_YELLOW = "\u001B[33m";
   private static final String ANSI_BLUE = "\u001B[34m";
   private static final String ANSI_PURPLE = "\u001B[35m";
   private static final String ANSI_CYAN = "\u001B[36m";
   private static final String ANSI_WHITE = "\u001B[37m";
   private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
}
