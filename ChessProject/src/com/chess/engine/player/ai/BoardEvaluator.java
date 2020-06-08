package com.chess.engine.player.ai;

import com.chess.engine.board.Board;

public interface BoardEvaluator {
    //This is a zero-sum game, if the evaluate returns a positive integer, white is winning
    //if it returns a negative integer, black is winning.
    int evaluate(Board board, int depth);
}
