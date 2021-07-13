/**
 * 
 */
package rummykub.v2;

/**
 * @author lucasbrambrink
 *
 */
public class TileUtil
{
   public static Tile parse(String tileString, Color previousColor)
   {
      Color color = null;
      int offset = 1;
      if (tileString.contains("R"))
      {
         color = Color.RED;
      }
      else if (tileString.contains("L"))
      {
         color = Color.BLUE;
      }
      else if (tileString.contains("Y"))
      {
         color = Color.YELLOW;
      }
      else if (tileString.contains("B"))
      {
         color = Color.BLACK;
      }
      else if (tileString.contains("J"))
      {
         return new Tile(
               Color.JOKER,
               0
         );
      }
      else if (previousColor != null)
      {
         color = previousColor;
         offset = 0;
      }
      if (color == null)
      {
         throw new IllegalArgumentException("Must specify color");
      }
      return new Tile(
            color,
            Integer.parseInt(tileString.substring(offset))
      );
   }

   public static boolean tilesEqual(Tile tile1, Tile tile2)
   {
      return serialize(tile1).equals(serialize(tile2));
   }

   public static String serialize(Tile tile)
   {
      char colorString;
      switch (tile.color)
      {
      default:
      case RED:
         colorString = 'R';
         break;
      case BLUE:
         colorString = 'L';
         break;
      case BLACK:
         colorString = 'B';
         break;
      case YELLOW:
         colorString = 'Y';
         break;
      case JOKER:
         return "J";
//         colorString = 'J';
//         System.out.println("JOKER VALUE IS " + tile.value);
//         break;
      }
      return colorString + String.valueOf(tile.value);
   }

   public static Tile clone(Tile tile)
   {
      return new Tile(
            tile.color,
            tile.value
      );
   }
}
