package rummykub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author lucasbrambrink
 *
 */
public class IntermediateGameState
{
   public ArrayList<TileSet> pool;
   public ArrayList<TileSet> intermediatePool;
   public ArrayList<Tile> holdingTiles;
   public int depth;
   public int differenceHoldingTiles;

   public IntermediateGameState(
         ArrayList<TileSet> pool,
         ArrayList<TileSet> intermediatePool,
         ArrayList<Tile> holdingTiles
   )
   {
      this.pool = pool;
      this.intermediatePool = intermediatePool;
      this.holdingTiles = holdingTiles;
      Collections.sort(
            this.pool,
            Comparator.comparing(TileSet::getLength).thenComparing(TileSet::getTotalValue).thenComparing(TileSet::getSetColor).reversed()
      );
      Collections.sort(
            this.intermediatePool,
            Comparator.comparing(TileSet::getLength).thenComparing(TileSet::getTotalValue).thenComparing(TileSet::getSetColor).reversed()
      );
      Collections.sort(
            this.holdingTiles,
            Comparator.comparing(Tile::getColor).thenComparing(Tile::getValue).reversed()
      );
//      showResult();
   }

   public boolean isValid()
   {
      boolean isValid = intermediatePool.isEmpty();
      for (TileSet set : pool)
      {
         isValid &= set.isValid(true);
      }
      return isValid;
   }

   public void showResult()
   {

      System.out.println(this.serialize());
   }

   public String serialize()
   {
      return String.join(
            " ",
            Arrays.asList("\t".repeat(depth) + "POOL",
                  "\n" + "\t".repeat(depth) + pool.stream().map(ts -> ts.print()).collect(Collectors.joining("\n" + "\t".repeat(depth))),
                  "\n" + "\t".repeat(depth) + "INTERMEDIATE",
                  "\n" + "\t".repeat(depth) + intermediatePool.stream().map(ts -> ts.print()).collect(Collectors.joining("\n" + "\t".repeat(depth))),
                  "\n" + "\t".repeat(depth) + "HOLDING",
                  "\n" + "\t".repeat(depth) + holdingTiles.stream().map(tile -> tile.print()).collect(Collectors.joining(" ".repeat(depth)))
            )
      );
   }
}
