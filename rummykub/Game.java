/**
 * 
 */
package rummykub;

import java.util.ArrayList;

/**
 * @author lucasbrambrink
 *
 */
public class Game
{
   private ArrayList<TileSet> publicPool;
   private ArrayList<Tile> pouch;
   private Player[] players;
   private int NUM_TURNS = 50;

   public Game(Player[] players)
   {
      this.players = players;
      this.publicPool = new ArrayList<TileSet>();
      this.pouch = initNewPouch();
   }

   public void run()
   {
      // each player gets 13 tiles
      for (Player player : this.players)
      {
         for (int i = 0; i < 13; i++)
         {
            player.addTile(drawFromPouch());
         }
         player.sortUnorderedTiles();
      }
      boolean playerHasWon = false;
      int turn = 1;
      while (!playerHasWon)
      {
         System.out.println("ROUND " + (turn + 1));
         for (Player player : this.players)
         {
            player.takeNewTurn(
                  drawFromPouch(),
                  this.publicPool
            );
            playerHasWon |= player.hasWon();
         }
         printGameState();
         turn += 1;
      }
   }

   public void printGameState()
   {
      printPublicPool();
      System.out.println("");
      for (Player player : this.players)
      {
         System.out.println(player.print());
      }
      System.out.println("");
   }

   public void printPublicPool()
   {
      System.out.println("PUBLIC POOL");
      for (TileSet tileSet : this.publicPool)
      {
         System.out.println(tileSet.print());
      }
   }

   public Tile drawFromPouch()
   {
      if (this.pouch.isEmpty())
      {
         return null;
      }
      int randomIndex = (int) (Math.random() * this.pouch.size());
      return this.pouch.remove(randomIndex);
   }

   private ArrayList<Tile> initNewPouch()
   {
      ArrayList<Tile> pouch = new ArrayList<Tile>();
      for (int i = 0; i < 2; i++)
      {
         pouch.addAll(initSet(Color.RED));
         pouch.addAll(initSet(Color.BLACK));
         pouch.addAll(initSet(Color.YELLOW));
         pouch.addAll(initSet(Color.BLUE));
      }
      return pouch;
   }

   private ArrayList<Tile> initSet(Color color)
   {
      ArrayList<Tile> tileSet = new ArrayList<Tile>();
      for (int i = 1; i < 13; i++)
      {
         tileSet.add(
               new Tile(
                     color,
                     i
               )
         );
      }
      return tileSet;
   }

}
