package rummykub.v2.dataStructures;

import java.util.ArrayList;
import java.util.HashMap;

import rummykub.v2.utils.GameStateUtil;

/**
 * @author lucasbrambrink
 *
 */
public class GameState
{
   public ArrayList<TileSet> publicPool;
   public ArrayList<TileSet> intermediatePoolDoubles;
   public ArrayList<TileSet> intermediatePoolSingles;
   public ArrayList<TileSet> holdingTiles;
   public HashMap<String, TileSet> tileSetMap;
   public ArrayList<TileSet> allPublicSets;
   public int depth;
   public int runningCost;
   public GameState previousState;
   public TileSet tileSetToAdd;
   public Tile foundTile;
   public int totalNumberOfStatesExplored;

   public GameState(
         ArrayList<TileSet> publicPool,
         ArrayList<TileSet> intermediatePoolDoubles,
         ArrayList<TileSet> intermediatePoolSingles,
         ArrayList<TileSet> holdingTiles
   )
   {
      this.publicPool = publicPool;
      this.intermediatePoolDoubles = intermediatePoolDoubles;
      this.intermediatePoolSingles = intermediatePoolSingles;
      this.holdingTiles = holdingTiles;
      this.allPublicSets = new ArrayList<TileSet>();
      GameStateUtil.sort(this);
      this.depth = 1;
      this.runningCost = 0;
   }
}
