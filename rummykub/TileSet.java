/**
 * 
 */
package rummykub.v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author lucasbrambrink
 *
 */
public class TileSet
{
   public ArrayList<Tile> tiles;
   public SetType setType;

   public TileSet(
         ArrayList<Tile> tiles,
         SetType setType
   )
   {
      this.tiles = tiles;
      this.setType = setType;
      sort();
   }

   public TileSet(ArrayList<Tile> tiles)
   {
      this.tiles = tiles;
      sort();
      this.setType = TileSetUtil.getSetType(this);
      sort();
   }

   public void addTile(Tile tile)
   {
      this.tiles.add(tile);
      sort();
      if (this.setType == SetType.UNDEFINED)
      {
         this.setType = TileSetUtil.getSetType(this);
      }
      sort();
   }

   public void sort()
   {
      Collections.sort(
            this.tiles,
            Comparator.comparing((Tile t) -> t.value).thenComparing(t -> t.color)
      );
      boolean hasJoker = this.tiles.stream().anyMatch((Tile t) -> t.color == Color.JOKER);
      if (hasJoker)
      {
         fixJoker();
         Collections.sort(
               this.tiles,
               Comparator.comparing((Tile t) -> t.value).thenComparing(t -> t.color)
         );
         Tile joker = this.tiles.stream().filter((Tile t) -> t.color == Color.JOKER).collect(Collectors.toList())
               .get(0);
         joker.value = 0;
      }
      if (this.size() > 1)
      {
         if (this.tiles.get(0).color == Color.JOKER && this.tiles.get(1).value == 1)
         {
            Tile joker = this.tiles.remove(0);
            this.tiles.add(joker);
         }
         else if (this.tiles.get(this.size() - 1).color == Color.JOKER && this.tiles.get(this.size() - 2).value == 13
               && this.setType == SetType.RUN)
         {
            Tile joker = this.tiles.remove(this.size() - 1);
//            System.out
//                  .println("CHANGING JOKER FROM " + joker.value + " TO " + String.valueOf(this.tiles.get(0).value - 1));
//            joker.value = this.tiles.get(0).value - 1;

            this.tiles.add(
                  0,
                  joker
            );
         }
      }
   }

   public int size()
   {
      return this.tiles.size();
   }

   private void fixJoker()
   {
      Tile joker = this.tiles.stream().filter((Tile t) -> t.color == Color.JOKER).collect(Collectors.toList()).get(0);
      if (this.size() == 1 || this.setType == SetType.UNDEFINED)
      {
         joker.value = 0;
         return;
      }
      if (this.setType == SetType.GROUP)
      {
         joker.value = this.tiles.stream().filter((Tile t) -> t.color != Color.JOKER).collect(Collectors.toList())
               .get(0).value;
         return;
      }
      joker.value = 0;
      Tile previousTile = this.tiles.get(1);
      for (int i = 2; i < this.size(); i++)
      {
         Tile tile = this.tiles.get(i);
         if (tile.value == previousTile.value + 2)
         {
            joker.value = tile.value - 1;
            return;
         }
         previousTile = tile;
      }
//      joker.value = this.tiles.get(1).value;
   }

}
