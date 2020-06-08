
package com.chess.engine.board;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public abstract class Tiles {


    protected final int tileCoordinate;

    //initialize a Map and assign a key to each empty tile.
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    //method to create all possible empty tiles.
    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles(){

        //make an uneditable map using a hash map
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        //loop through all empty tiles
        for(int i = 0; i<BoardUtils.NUM_TILES ; i++)
            //assign an integer value for each tile
            emptyTileMap.put(i, new EmptyTile(i));

        //After returning it is possible to change original empty tile map
        //Hence we use a copy of the map
        return ImmutableMap.copyOf(emptyTileMap);
    }

    //create a tile with params as coordinate and piece.
    public static Tiles createTile(final int tileCoordinate, final Piece piece){
        //if piece on tile then assign the piece to the tile, otherwise return the coordinate of said tile
        return (piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate));
    }

    private Tiles(final int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    //an abstract method that returns whether the tile is occupied or not.
    public abstract boolean isTileOccupied();

    //an abstract method that returns the piece desired.
    public abstract Piece getPiece();

    //a new class for empty tiles.
    public static final class EmptyTile extends Tiles{

        @Override
        public String toString(){
            return "-";
        }

        private EmptyTile(final int Coordinate){
            super(Coordinate);
        }

        //return the information that tile is not occupied
        public boolean isTileOccupied(){
            return false;
        }

        //since tile is empty hence no piece on it
        public Piece getPiece(){
            return null;
        }
    }

    //a new class for occupied tiles.
    public static final class OccupiedTile extends Tiles{
        private final Piece pieceOnTile;

        //Black pieces as Lowercase, White as uppercase.
        @Override
        public String toString(){
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                    getPiece().toString();
        }

        //input the tileCoordinate and piece on the tile.
        private OccupiedTile(int tileCoordinate, final Piece pieceOnTile){
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;

        }

        //return the information that tile is occupied.
        public boolean isTileOccupied(){
            return true;
        }

        @Override
        //since tile is occupied hence return piece on it.
        public Piece getPiece() {
            return this.pieceOnTile;
        }
    }

    public int getTileCoordinate(){
        return this.tileCoordinate;
    }
}
