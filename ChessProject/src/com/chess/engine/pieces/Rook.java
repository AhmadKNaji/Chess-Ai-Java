package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Tiles;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;
import static com.chess.engine.pieces.Piece.PieceType.ROOK;

public class Rook extends Piece {

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-8, -1, 1, 8};
    
    public Rook(final int piecePosition, final Alliance pieceAlliance) {
        super(ROOK, piecePosition, pieceAlliance, true);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        
        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            //create a variable to take the current piece position
            int candidateDestinationCoordinate = this.piecePosition;

            //check if the candidate tile is valid.
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                //if the rook is in an exception zone skip the loop.
                if(isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)
                        || isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset))
                    break;


                //increment the candidate destination coordinate by the offset
                candidateDestinationCoordinate += candidateCoordinateOffset;

                //check if the candidate tile is valid.
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                    final Tiles candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                    //if designated tile is not Occupied.
                    if (!candidateDestinationTile.isTileOccupied()) {
                        //add the move to ArrayList
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    } else {
                        //else if tile is occupied.

                        //return the piece on that tile.
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        //return whether the piece is for White or Black.
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                        //if Occupying piece is the opposite of the rook's alliance, add the move to capture later.
                        if (this.pieceAlliance != pieceAlliance)
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));

                        //once the rook sees a piece in its path it no longer considers the tiles after it.
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }


    public Rook movePiece(Move move) {
        return new Rook(move.getDestinationCoordinate() ,move.getMovedPiece().getPieceAlliance());
    }

    //check whether the rook is in first column where the rule falls apart.
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1);
    }

    //check whether the rook is in eighth column where the rule falls apart.
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1);
    }

    @Override
    public String toString(){
        return ROOK.toString();
    }
}

