package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tiles;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;
import static com.chess.engine.pieces.Piece.PieceType.KING;

public class King extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePosition, final Alliance pieceAlliance) {
        super(KING ,piecePosition, pieceAlliance, true);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE){
           final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

           //check if the king is in exclusion zones.
           if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset)
                   || isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
               continue;
           }




           if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
               final Tiles candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

               //if designated tile is not Occupied.
               if(!candidateDestinationTile.isTileOccupied()){
                   //add the move to ArrayList.
                   legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
               } else {
                   //else if tile is occupied.

                   //return the piece on that tile.
                   final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                   //return whether the piece is for White or Black.
                   final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                   //if Occupying piece is the opposite of the king's alliance, add the move to capture later.
                   if(this.pieceAlliance != pieceAlliance)
                       legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
               }
           }
        }
        return ImmutableList.copyOf(legalMoves);
    }


    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate() ,move.getMovedPiece().getPieceAlliance());
    }

    //check if king is on First column.
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
    }

    //check if king is on Eighth column.
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9 || candidateOffset == 1);
    }
    @Override
    public String toString(){
        return KING.toString();
    }
    
}
