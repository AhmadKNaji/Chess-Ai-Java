package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.PawnJump;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;
import static com.chess.engine.pieces.Piece.PieceType.PAWN;

public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {8, 16, 7, 9};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PAWN, piecePosition, pieceAlliance, true);
    }


    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            //apply offset to piece position, but it would multiply by 1 if piece is black, and by -1 if piece is white.
            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);

            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            //if you are moving one tile forward and tile is not occupied.
            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                } else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
                //check if offset available.
            } else if (currentCandidateOffset == 16 &&
                    //check if this is the pawn's first move.
                    this.isFirstMove() &&
                    //check if the piece is on second row and it's a black piece to allow jumping two steps.
                    ((BoardUtils.SECOND_RANK[this.piecePosition] && this.getPieceAlliance().isWhite()) ||
                            //check if the piece is on seventh row and it's a white piece to allow jumping two steps.
                            (BoardUtils.SEVENTH_RANK[this.piecePosition] && this.getPieceAlliance().isBlack()))) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                //if the behind Candidate is not occupied
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()
                        //and the desired candidate is not occupied.
                        // execute if statement.
                        && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }

            } else
                //check if pawn can attack on its left.
                if (currentCandidateOffset == 7 &&
                        //if piece is not on 8th column & the piece is white.
                        !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                                //if piece is not 1st column & the piece is black.
                                (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {

                    //check if the tile we desire to attack is occupied.
                    if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {

                        //if occupied give me information about the piece.
                        final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                        //if piece designated is opposite to the moving piece.
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                                legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                            } else {
                                //add a new move.
                                legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                            }
                        }
                    } else if (board.getEnPassantPawn() != null) {
                        if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                            final Piece pieceOnCandidate = board.getEnPassantPawn();
                            if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                                legalMoves.add(new PawnEnpassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                            }
                        }
                    }
                } else
                    //check if pawn can attack on its right
                    if (currentCandidateOffset == 9 &&
                            //if piece is not on 8th column & the piece is black.
                            !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack() ||
                                    //if piece is not 1st column & the piece is white.
                                    (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite())))) {

                        //check if the tile we desire to attack is occupied.
                        if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {

                            //if occupied give me information about the piece.
                            final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                            //if piece designated is opposite to the moving piece.
                            if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                                    legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                                } else {
                                    //add a new move.
                                    legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                                }
                            }
                        } else if (board.getEnPassantPawn() != null) {
                            if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                                final Piece pieceOnCandidate = board.getEnPassantPawn();
                                if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                                    legalMoves.add(new PawnEnpassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                                }
                            }
                        }
                    }
        }
        return ImmutableList.copyOf(legalMoves);
    }


    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() {
        return PAWN.toString();
    }

    public Piece getPromotionPiece(){
        return new Queen(this.piecePosition, this.pieceAlliance, false);
    }
}
