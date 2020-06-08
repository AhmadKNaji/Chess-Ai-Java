package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;

public class MinMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    public MinMax(final int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString(){
        return "Minmax";
    }

    @Override
    public Move execute(Board board) {

        //we want to time how long it takes to execute this function.
        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;

        int currentValue;
        System.out.println(board.currentPlayer().toString() + " is thinking with depth = " + searchDepth);

        //regardless of player, make that move
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            //check if move is done
            if(moveTransition.getMoveStatus().isDone()){
                //if current player is white, next move is going to be a minimizing move
                //if current player is black, next move is going to be a maximizing move
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(), searchDepth - 1) :
                        max(moveTransition.getTransitionBoard(), searchDepth - 1);
                if(board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                }
                    else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue){
                        lowestSeenValue = currentValue;
                        bestMove = move;
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.println(executionTime + " ms");
        return bestMove;
    }


    //our helper methods to visualize the minimax tree
    public int min(final Board board, final int depth){
        if(depth == 0 || isEndGame(board))
            return this.boardEvaluator.evaluate(board, depth);
        //start with the highest number possible.
        //we will never evaluate to this score.
        int lowestSeenValue = Integer.MAX_VALUE;
        //we go through each move that is legal, we execute these moves and score them
        //we record the minimum and return the lowest value of all legal moves.
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);
                if(currentValue <= lowestSeenValue){
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }
    public int max(final Board board, final int depth){
        if(depth == 0 || isEndGame(board))
            return this.boardEvaluator.evaluate(board, depth);
        //start with the lowest number possible.
        //we will never evaluate to this score.
        int highestSeenValue = Integer.MIN_VALUE;
        //we go through each move that is legal, we execute these moves and score them
        //we record the maximum and return the greatest value of all legal moves.
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = min(moveTransition.getTransitionBoard(), depth - 1);
                if(currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }

    //terminating case for a chess game
    private static boolean isEndGame(final Board board){
        return board.currentPlayer().isInCheckMate() ||
                board.currentPlayer().isInStaleMate();
    }
}
