package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {
    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cachedHashCode;



    //input the position of the piece and whether its a white or black piece.
    Piece(final PieceType pieceType,
          final int piecePosition,
          final Alliance pieceAlliance,
          final boolean isFirstMove){
        this.pieceType = pieceType;
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }


    //source: https://www.sitepoint.com/how-to-implement-javas-hashcode-correctly/
    private int computeHashCode(){
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    public int getPiecePosition(){
        return this.piecePosition;
    }

    public PieceType getPieceType(){
        return this.pieceType;
    }

    //an abstract method to calculate the legal moves on the desired board.
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    //return the information on whether the piece is for white or black.
    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public boolean isFirstMove(){
        return this.isFirstMove;
    }

    //Take in a move and apply it to the exiting piece we are on,
    // and return a new piece that is the same as the old piece with an updated piece position.
    public abstract Piece movePiece(Move move);

    //the regular equals returns the reference equality between 2 variables.
    @Override
    public boolean equals(final Object other){
        //if they are referentially equal.
        if(this == other)
            return true;
        //if the thing we are comparing against is not a piece, don't bother.
        if(!(other instanceof Piece))
            return false;
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
                pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
    }

    //the regular returns the address of the object in memory as integer.
    @Override
    public int hashCode(){
        return this.cachedHashCode;
    }

    public int getPieceValue(){
        return this.pieceType.getPieceValue();
    }



    public enum PieceType{
        PAWN("P", 100){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R", 500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q", 900) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K", 99999) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private final String pieceName;
        private final int pieceValue;

        PieceType(final String pieceName, final int pieceValue){
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }
        @Override
        public String toString(){
            return this.pieceName;
        }


        //to prevent casting
        public abstract boolean isKing();


        public abstract boolean isRook();

        public int getPieceValue(){
            return this.pieceValue;
        }
    }

}
