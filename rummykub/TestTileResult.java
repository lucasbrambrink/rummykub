package rummykub;

public class TestTileResult
{
   public TileSet newTileSet;
   public TileSet splitTileSet;
   public Tile splitBeforeTile;
   public Source sourceTileSet;
   public TileSet splitIntermediateTileSet;

   public TestTileResult(
         TileSet newTileSet,
         TileSet splitTileSet,
         Tile splitBeforeTile,
         Source sourceTileSet
   )
   {
      this.newTileSet = newTileSet;
      this.splitTileSet = splitTileSet;
      this.splitBeforeTile = splitBeforeTile;
      this.sourceTileSet = sourceTileSet;
   }
}
