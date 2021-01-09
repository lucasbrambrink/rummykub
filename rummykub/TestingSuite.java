/**
 * 
 */
package rummykub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author lucasbrambrink
 *
 */
public class TestingSuite
{

   public static void runTestSuite()
   {
      TileSet newSet = TileSet.getSinglet(HOLDING_TILES.get(0));
      ArrayList<IntermediateGameState> gameState = SolverUtil.findOptimalSolutions(
            POOL,
            new ArrayList<TileSet>(),
            HOLDING_TILES
      );
      gameState = SolverUtil.findOptimalSolutions(
            POOL_2,
            new ArrayList<TileSet>(),
            new ArrayList<Tile>(Arrays.asList(RED_4))
      );
      gameState = SolverUtil.findOptimalSolutions(
            POOL_2,
            new ArrayList<TileSet>(),
            new ArrayList<Tile>(
                  Arrays.asList(
                        RED_4,
                        new Tile(
                              Color.RED,
                              5
                        )
                  )
            )
      );
      // unable to find solution for last holding tile
      gameState = SolverUtil.findOptimalSolutions(
            POOL_2,
            new ArrayList<TileSet>(),
            new ArrayList<Tile>(
                  Arrays.asList(
                        RED_4,
                        new Tile(
                              Color.RED,
                              5
                        ),
                        new Tile(
                              Color.RED,
                              10
                        )
                  )
            )
      );
      gameState = SolverUtil.findOptimalSolutions(
            new ArrayList<TileSet>(),
            new ArrayList<TileSet>(),
            new ArrayList<Tile>(
                  Arrays.asList(
                        RED_4,
                        new Tile(
                              Color.RED,
                              5
                        ),
                        new Tile(
                              Color.RED,
                              6
                        ),
                        new Tile(
                              Color.BLUE,
                              4
                        ),
                        new Tile(
                              Color.BLUE,
                              5
                        ),
                        new Tile(
                              Color.BLUE,
                              6
                        )
                  )
            )
      );
   }

   private static void showResult(IntermediateGameState gameState)
   {
      System.out.println("\nPOOL");
      for (TileSet tileSet : gameState.pool)
      {
         System.out.println(tileSet.print());
      }
      System.out.println("\nINTERMEDIATE");
      for (TileSet tileSet : gameState.intermediatePool)
      {
         System.out.println(tileSet.print());
      }
      System.out.println("\nHOLDING");
      System.out.println(gameState.holdingTiles.stream().map(tile -> tile.print()).collect(Collectors.joining(" ")));
   }

   private static final Tile RED_1 = new Tile(
         Color.RED,
         1
   );
   private static final Tile RED_2 = new Tile(
         Color.RED,
         2
   );
   private static final Tile RED_2_1 = new Tile(
         Color.RED,
         2
   );
   private static final Tile RED_3 = new Tile(
         Color.RED,
         3
   );
   private static final Tile RED_3_1 = new Tile(
         Color.RED,
         3
   );
   private static final Tile RED_4 = new Tile(
         Color.RED,
         4
   );
   private static final Tile BLUE_1 = new Tile(
         Color.BLUE,
         1
   );
   private static final Tile BLUE_2 = new Tile(
         Color.BLUE,
         2
   );
   private static final Tile BLUE_3 = new Tile(
         Color.BLUE,
         3
   );
   private static final Tile BLUE_4 = new Tile(
         Color.BLUE,
         4
   );
   private static final Tile BLUE_5 = new Tile(
         Color.BLUE,
         5
   );
   private static final Tile YELLOW_2 = new Tile(
         Color.YELLOW,
         2
   );
   private static final Tile BLACK_2 = new Tile(
         Color.BLACK,
         2
   );
   private static final ArrayList<Tile> HOLDING_TILES = new ArrayList<Tile>(Arrays.asList(RED_3_1));
   private static final ArrayList<TileSet> UNFINISHED_POOL = new ArrayList<TileSet>();
   private static final ArrayList<TileSet> POOL = new ArrayList<TileSet>(
         Arrays.asList(
               TileSet.createTileSet(
                     new ArrayList<Tile>(
                           Arrays.asList(
                                 RED_1,
                                 RED_2,
                                 RED_3,
                                 RED_4
                           )
                     )
               ),
               TileSet.createTileSet(
                     new ArrayList<Tile>(
                           Arrays.asList(
                                 YELLOW_2,
                                 BLACK_2,
                                 RED_2_1
                           )
                     )
               ),
               TileSet.createTileSet(
                     new ArrayList<Tile>(
                           Arrays.asList(
                                 BLUE_2,
                                 BLUE_3,
                                 BLUE_4,
                                 BLUE_5
                           )
                     )
               )
         )
   );

   private static final ArrayList<TileSet> POOL_2 = new ArrayList<TileSet>(
         Arrays.asList(
               TileSet.createTileSet(
                     new ArrayList<Tile>(
                           Arrays.asList(
                                 new Tile(
                                       Color.RED,
                                       1
                                 ),
                                 new Tile(
                                       Color.BLUE,
                                       1
                                 ),
                                 new Tile(
                                       Color.YELLOW,
                                       1
                                 )
                           )
                     )
               ),
               TileSet.createTileSet(
                     new ArrayList<Tile>(
                           Arrays.asList(
                                 new Tile(
                                       Color.RED,
                                       2
                                 ),
                                 new Tile(
                                       Color.BLUE,
                                       2
                                 ),
                                 new Tile(
                                       Color.YELLOW,
                                       2
                                 )
                           )
                     )
               ),
               TileSet.createTileSet(
                     new ArrayList<Tile>(
                           Arrays.asList(
                                 new Tile(
                                       Color.RED,
                                       3
                                 ),
                                 new Tile(
                                       Color.BLUE,
                                       3
                                 ),
                                 new Tile(
                                       Color.YELLOW,
                                       3
                                 )
                           )
                     )
               )
         )
   );

}
