package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

//not extendable
public final class StandardBoardEvaluator implements BoardEvaluator {

    private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    @Override
    public int evaluate(final Board board, final int depth) {

        //get the score for white nad subtract from score of black
        return scorePlayer(board.whitePlayer() , depth) -
                scorePlayer(board.blackPlayer() , depth);
    }

    private int scorePlayer(final Player player, final int depth) {
        return pieceValue(player) +
               mobility(player) +
               check(player) +
               checkMate(player, depth) +
               castled(player);
    }

    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    //if we find checkmate at a higher depth, sooner within our search, we give a bigger bonus
    private static int checkMate(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? (CHECK_MATE_BONUS * depthBonus(depth)) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    //if given board evaluation, at leaf node if opponent is in check
    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    //for given board, how many legal moves does this player have
    //this indicates a better position in the game
    private static int mobility(final Player player) {
        return player.getLegalMoves().size();
    }

    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;
        for (final Piece piece : player.getActivePieces()){
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }
}
