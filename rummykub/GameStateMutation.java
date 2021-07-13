package rummykub.v2;

/**
 * @author lucasbrambrink
 *
 */
public class GameStateMutation
{
   public GameState newState;
   public StateOperation operation;

   public GameStateMutation(
         GameState newState,
         StateOperation operation
   )
   {
      this.newState = newState;
      this.operation = operation;
   }
}
