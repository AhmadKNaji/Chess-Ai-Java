package com.chess.engine.board;

import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    //the constructor takes in the board, the piece that will be moved, and where it is moved.
    public Move(final Board board,
                final Piece movedPiece,
                final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final Board board, final int destinationCoordinate){
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = false;
        this.movedPiece = null;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other)
            return true;
        if(!(other instanceof Move))
            return false;
        final Move otherMove = (Move) other;
        return  getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getMovedPiece().equals(otherMove.getMovedPiece());
    }

    public Board getBoard(){
        return this.board;
    }

    public int getCurrentCoordinate(){
        return this.movedPiece.getPiecePosition();
    }

    public Piece getMovedPiece(){
        return this.movedPiece;
    }

    public boolean isAttack(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    //where it is being moved to
    public int getDestinationCoordinate(){
        return this.destinationCoordinate;
    }


    /*to make a move on a board does not mean we mutate
      that existing board, we materialize a new board into existence that represents
      the board that would exist if you made a move on the incoming board.
     */
    //This method will return a new board, not mutate the existing final board that was passed in.
    public Board execute(){
        //use a builder to help materialize new board.
        final Builder builder = new Builder();
        //traverse through all of the current player's pieces.
        for (final Piece piece : this.board.currentPlayer().getActivePieces()){
            //for all the pieces that aren't the moved piece.
            if(!this.movedPiece.equals(piece)){
                //place them on the new board with no change.
                builder.setPiece(piece);
            }
        }
        //traverse through all of the current player's opponent's pieces.
        for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            //place them on virtual board because it is not his turn to move. Therefore not having a moved piece.
            builder.setPiece(piece);
        }
        //move the moved piece.
        builder.setPiece(this.movedPiece.movePiece(this));
        //set move maker to the opponent.
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }


    public static class MajorAttackMove extends AttackMove{
        public MajorAttackMove(final Board board,
                               final Piece pieceMoved,
                               final int destinationCoordinate,
                               final Piece pieceAttacked){
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        @Override
        public boolean equals(final Object o){
            return this == o || o instanceof MajorAttackMove && super.equals(o);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType() + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }


    //A major piece Move.
    public static final class MajorMove extends Move {
        public MajorMove(final Board board,
                         final Piece movedPiece,
                         final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }





    public static class AttackMove extends Move {
        //keep in memory the attacked piece.
        final Piece attackedPiece;
        public AttackMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other){
            if(this == other){
                return true;
            }if(!(other instanceof AttackMove)){
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }

    }

    public static final class PawnMove extends Move {
        public PawnMove(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object o){
            return this == o || o instanceof PawnMove && super.equals(o);
        }

        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board,
                              final Piece movedPiece,
                              final int destinationCoordinate,
                              final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0,1) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static final class PawnEnpassantAttackMove extends PawnAttackMove {
        public PawnEnpassantAttackMove(final Board board,
                                       final Piece movedPiece,
                                       final int destinationCoordinate,
                                       final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnEnpassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackedPiece())){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

    }

    public static class PawnPromotion extends Move{

        final Move decoratedMove;
        final Pawn promotedPawn;
        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public Board execute(){
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();
            for(final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()){
                if(!this.promotedPawn.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack(){
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece(){
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString(){
            return "";
        }

        @Override
        public int hashCode(){
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof PawnPromotion && (this.decoratedMove.equals(other));
        }
    }

    public static final class PawnJump extends Move {
        public PawnJump(final Board board,
                        final Piece movedPiece,
                        final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    static abstract class CastleMove extends Move {

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board,
                          final Piece movedPiece,
                          final int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination
                          ) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode(){
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other){
            if(this == other){
                return true;
            }
            if(!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove)other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }

    }
    public static class KingSideCastleMove extends CastleMove{
        public KingSideCastleMove(final Board board,
                                  final Piece movedPiece,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString(){
            return "O-O";
        }

    }
    public static class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(final Board board,
                                   final Piece movedPiece,
                                   final int destinationCoordinate,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString(){
            return "O-O-O";
        }

    }
    static class NullMove extends Move {
        public NullMove() {
            super(null, 65);
        }
        @Override
        public Board execute(){
            throw new RuntimeException("Cannot execute null move!");
        }

        @Override
        public int getCurrentCoordinate(){
            return -1;
        }
    }


    public static class MoveFactory{
        private MoveFactory(){
            throw new RuntimeException("Not instantiable!");
        }
        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate){
            for(final Move move : board.getAllLegalMoves()){
                if (move.getCurrentCoordinate() == currentCoordinate &&
                    move.getDestinationCoordinate() == destinationCoordinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
