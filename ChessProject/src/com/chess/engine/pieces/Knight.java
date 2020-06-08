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
import static com.chess.engine.pieces.Piece.PieceType.KNIGHT;

public class Knight extends Piece {
    //the destinations where the knight can reach by adding its position to each one of the following elements.
    private final static int[] CANDIDATE_MOVE_COORDINATES
                                = { -17, -15, -10, -6, 6, 10, 15, 17};
    //specify the location and alliance of knight.
    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(KNIGHT ,piecePosition, pieceAlliance, true);
    }
    @Override
    //calculate the legal moves for the knight.
    public Collection<Move> calculateLegalMoves(final Board board) {
        //initialize an ArrayList to store the legal moves.
        List<Move> legalMoves = new ArrayList<>();
        //we use an enhanced loop to check all legal moves for the knight.
        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES){
            //as mentioned we add the current position of our knight to the elements of our array.
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            //check if the candidate tile is valid.
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                //check if knight is any of exceptional tiles.
                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                        isEighthColumnExclusion(this.piecePosition, currentCandidateOffset))
                    continue;
                //get the coordinate of the designated tile and save it.
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

                    //if Occupying piece is the opposite of the knight's alliance, add the move to capture later.
                    if(this.pieceAlliance != pieceAlliance)
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                }
            }
        }
        //return an immutable copy of the total legal moves.
        return ImmutableList.copyOf(legalMoves);
    }


    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate() ,move.getMovedPiece().getPieceAlliance());
    }

    //check if knight is on First column.
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -17 || candidateOffset == -10 ||
                candidateOffset == 6 || candidateOffset == 15);
    }
    //check if knight is on Second column.
    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
    }
    //check if knight is on Second column.
    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6 || candidateOffset == 10);
    }
    //check if knight is on Second column.
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -15 || candidateOffset == -6 ||
                candidateOffset == 10 || candidateOffset==17 );
    }
    @Override
    public String toString(){
        return KNIGHT.toString();
    }
}
