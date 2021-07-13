package rummykub.v2;

import java.util.ArrayList;

/**
 * @author lucasbrambrink
 *
 */
public class TileMembership
{
   public Tile tile;
   public TileSet memberOf;
   public ArrayList<TileSet> pool;
   public boolean isFreelyAvailable;

   public TileMembership(
         Tile tile,
         TileSet memberOf,
         ArrayList<TileSet> pool
   )
   {
      this.tile = tile;
      this.memberOf = memberOf;
      this.pool = pool;
   }
}
