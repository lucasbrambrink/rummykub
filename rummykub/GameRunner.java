package rummykub;

/**
 * @author lucasbrambrink
 *
 */
public class GameRunner
{
   public static void main(String[] args)
   {
//      TestingSuite.runTestSuite();
      Player[] players = new Player[]
      {
            new Player("Player 1"),
            new Player("Player 2")
      };
      Game game = new Game(players);
      game.run();
   }
}
