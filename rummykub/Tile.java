/**
 * 
 */
package rummykub.v2;

/**
 * @author lucasbrambrink
 *
 *         Note these tiles are meant to be duplicated, i.e. many instances of a
 *         Yellow 2 might exist, as they fit into each set. This allows
 *         exploring multiple game states hermetically, as each game state is
 *         self contained.
 *
 */
public class Tile
{
   public int value;
   public Color color;
   public TileSet memberOf;

   public Tile(
         Color color,
         int value
   )
   {
      this.color = color;
      this.value = value;
   }
}
