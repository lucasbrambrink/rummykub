/**
 * 
 */
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

   public int calculateCost(GameState initialState)
   {
      if (this.operation == StateOperation.MERGE || this.operation == StateOperation.SPLIT_MERGE)
      {
         return 0;
      }
      // 1 points for each added in the singles
      int newlyAdded = newState.intermediatePoolSingles.size() - initialState.intermediatePoolSingles.size();
      return Math.max(
            newlyAdded,
            0
      );
   }
}
