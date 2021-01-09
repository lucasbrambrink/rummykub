/**
 * 
 */
package rummykub;

/**
 * @author lucasbrambrink
 *
 */
public class Tile
{
   private int value;
   private Color color;

   public Tile(
         Color color,
         int value
   )
   {
      this.color = color;
      this.value = value;
   }

   public void setValue(int value)
   {
      if (value < 1 || value > 13)
      {
         throw new IllegalArgumentException("Tile must be between 1-13");
      }
      this.value = value;
   }

   public int getValue()
   {
      return this.value;
   }

   public void setColor(Color color)
   {
      this.color = color;
   }

   public Color getColor()
   {
      return this.color;
   }

   public String print()
   {
      return this.getColorCode() + this.value + ANSI_RESET;
   }

   private String getColorCode()
   {
      switch (this.color)
      {
      case RED:
         return ANSI_RED;
      case BLUE:
         return ANSI_BLUE;
      case BLACK:
         return ANSI_WHITE_BACKGROUND + ANSI_BLACK;
      case YELLOW:
         return ANSI_YELLOW;
      default:
         return "";
      }
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
