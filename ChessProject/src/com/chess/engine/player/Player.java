package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.player.MoveStatus.*;
import static com.chess.engine.player.MoveStatus.DONE;

public abstract class Player {

    //we want to keep track of our king
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves){
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        //pass in king's current tile location and enemy moves
        //if list is NOT empty then the king is in check
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    //go through each enemy move and see if destination coordinate of that enemy move overlaps with king's position
    //if it does, the king is in check
    //get back a collection of moves that attack king position

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for(final Move move : moves){
            if(piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    //this is to ensure that there is a king for the player on the board
    private King establishKing(){
        for(final Piece piece : getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board!!");
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }

    public boolean isInCheckMate(){
        return this.isInCheck && hasEscapeMoves();
    }


    /*
    In order to calculate whether the king can escape
    we will go through each of player moves and make these moves in an imaginary board
    after we make them and look at original board and we see if we can successfully make that move
    if you can't make it (illegal or inCheck) return false
    */
    protected boolean hasEscapeMoves() {
        for(final Move move : this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if(transition.getMoveStatus().isDone()){
                return false;
            }
        }
        return true;
    }

    public boolean isInStaleMate(){
        return !this.isInCheck && hasEscapeMoves();
    }

    public boolean isCastled(){
        return false;
    }

    public King getPlayerKing(){
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    public MoveTransition makeMove(final Move move){

        if(!isMoveLegal(move)){
            //if move is illegal
            //the transition does not take us to a new board
            //it returns same board and move status is illegal


            return new MoveTransition(this.board, ILLEGAL_MOVE);
        }

        //we use the move to polymorphically execute the move
        //and return us a new board that we transition to
        final Board transitionBoard = move.execute();


        //are there any attacks on the current player's king?
        final Collection<Move> kingAttacks =
                //calculates the attacks on tile for current player's opponent's king
                // and transition board current player get legal moves
                Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                                                                            transitionBoard.currentPlayer().getLegalMoves());

        //you can't make a move that exposes your king to check
        if(!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, LEAVES_PLAYER_IN_CHECK);
        }
        //otherwise execute move
        return new MoveTransition(transitionBoard, DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals);
    @Override
    public String toString(){
        if(getAlliance().isWhite()){
            return "White Player";
        }
        return "Black Player";
    }

}
