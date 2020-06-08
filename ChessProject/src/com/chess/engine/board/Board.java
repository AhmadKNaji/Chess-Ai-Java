package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class Board {

    //we use a list since we cannot have an immutable array but we can have an immutable list.
    private final List<Tiles> gameBoard;

    //keep track of active white and black pieces
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;



    private Board(final Builder builder){
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        //pass in the board and white, and black, standard legal moves for each player we construct
        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    public Collection<Piece> getBlackPieces(){
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces(){
        return this.whitePieces;
    }

    public Player whitePlayer(){
        return this.whitePlayer;
    }

    public Player blackPlayer(){
        return this.blackPlayer;
    }

    public Player currentPlayer(){
        return this.currentPlayer;
    }
    @Override
    public String toString(){
        final StringBuilder builder = new StringBuilder();
        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            //print out the board in an ASCII code.
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if((i+1) % BoardUtils.NUM_TILES_PER_ROW == 0){
                //insert new Line after each ROW, Rows = 8.
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /*we implemented the calculateLegalMoves to each piece, now we are passing in a collection of pieces
    * now we want calculateLegalMoves for a given alliance. */
    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final Piece piece: pieces) {
            //add all of the elements back into this container.
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static Collection<Piece> calculateActivePieces(final List<Tiles> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();
        //loop through the tiles' list.
        for (final Tiles tile : gameBoard){
            //if the tile is occupied by a piece.
            if(tile.isTileOccupied()){
                //assign a placeholder to save the value of the piece.
                final Piece piece = tile.getPiece();
                //if piece belongs to the alliance required.
                if(piece.getPieceAlliance()== alliance){
                    //add it to my activePieces's list.
                    activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    public Tiles getTile(final int tileCoordinate){
        return gameBoard.get(tileCoordinate);
    }

    //we create a method to populate a list of tiles numbered 0 to 63 to represent chess board.
    private static List<Tiles> createGameBoard(final Builder builder){
        final Tiles[] tiles = new Tiles[BoardUtils.NUM_TILES];

        //we get from the config, we are going to map a piece onto a tile id.
        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            //get the piece associated with the tile id.
            tiles[i] = Tiles.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }



    public static Board createStandardBoard(){
        final Builder builder = new Builder();
        //Black Layout.
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(4, Alliance.BLACK));
        builder.setPiece(new King(3, Alliance.BLACK));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));
        //White Layout
        builder.setPiece(new Rook(63, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Queen(60, Alliance.WHITE));
        builder.setPiece(new King(59, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(48, Alliance.WHITE));
        //White To Move
        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();

    }

    public Iterable<Move> getAllLegalMoves() {
        //concatenation of white player and black player legal moves.
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(),
                                                                this.blackPlayer.getLegalMoves()));
    }


    public static class Builder{

        //map tile id of chess board to a given piece on that tile id.
        Map<Integer, Piece> boardConfig;

        //person to move.
        Alliance nextMoveMaker;

        Pawn enPassantPawn;

        public Builder(){
            this.boardConfig = new HashMap<>();
        }

        public Builder setPiece(final Piece piece){
            //associate a piece with the Tile position.
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public void setMoveMaker(final Alliance nextMoveMaker){
            this.nextMoveMaker = nextMoveMaker;
        }

        public Board build(){
            return new Board(this);
        }


        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }


    }

    public Pawn getEnPassantPawn() {
        return enPassantPawn;
    }


}
