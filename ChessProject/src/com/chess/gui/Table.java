package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tiles;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MinMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.awt.BorderLayout.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {
    private final Color lightTileColor = Color.LIGHT_GRAY;
    private final Color darkTileColor = Color.DARK_GRAY;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private Board chessBoard;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(800, 800);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 400);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private final static String defaultPieceImagesPath = "art/pieces/plain/";
    private Tiles sourceTile;
    private Tiles destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private Move computerMove;
    private boolean highlightLegalMoves;

    private static final Table INSTANCE = new Table();

    private Table() {
        JFrame gameFrame = new JFrame("Ai Chess");
        this.boardDirection = BoardDirection.NORMAL;
        final JMenuBar tableMenuBar = createTableMenuBar();
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setJMenuBar(tableMenuBar);
        gameFrame.setSize(OUTER_FRAME_DIMENSION);
        chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAiWatcher());
        this.gameSetup = new GameSetup(gameFrame, true);
        gameFrame.setVisible(true);
        gameFrame.add(this.takenPiecesPanel, WEST);
        gameFrame.add(this.boardPanel, CENTER);
        gameFrame.add(this.gameHistoryPanel, EAST);
        this.highlightLegalMoves = false;

    }

    public static Table get(){
        return INSTANCE;
    }

    private GameSetup getGameSetup(){
        return  this.gameSetup;
    }


    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        //Open a game from a PGN file, a game played by someone and GUI can load prior games
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(e -> System.out.println("Open up that PGN file!"));
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            boardPanel.drawBoard(chessBoard);
        });
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        legalMoveHighlighterCheckbox.addActionListener(e -> highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected());

        preferencesMenu.add(legalMoveHighlighterCheckbox);

        return preferencesMenu;
    }

    private JMenu createOptionsMenu(){
        final JMenu optionsMenu = new JMenu("Options");

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(e -> {
            Table.get().getGameSetup().promptUser();
            Table.get().setupUpdate(Table.get().getGameSetup());
        });
        optionsMenu.add(setupGameMenuItem);
        return  optionsMenu;
    }

    private void setupUpdate(final GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);
    }

    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    public void setComputerMove(Move computerMove) {
        this.computerMove = computerMove;
    }

    public Move getComputerMove() {
        return computerMove;
    }

    private static class TableGameAiWatcher implements Observer{

        //if current player is an AI player, we want the Ai to move
        @Override
        public void update(Observable o, Object arg) {
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                !Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                //create an Ai thread and execute Ai Work
                final AiThink aiThink = new AiThink();
                aiThink.execute();
            }
            //game ended due to checkmate
            if(Table.get().getGameBoard().currentPlayer().isInCheckMate()){
                System.out.println("GAME OVER, " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!");
            }
            //game ended due to stalemate
            if(Table.get().getGameBoard().currentPlayer().isInStaleMate()){
                System.out.println("GAME OVER, " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!");
            }
        }
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }

    private static class AiThink extends SwingWorker<Move, String>{
        private AiThink(){

        }

        @Override
        protected Move doInBackground() {
            final MoveStrategy minMax = new MinMax(GameSetup.getSearchDepth());
            return minMax.execute(Table.get().getGameBoard());
        }

        @Override
        public void done(){
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateComputerMove(final Move move) {
        setComputerMove(move);
    }

    private MoveLog getMoveLog(){
        return this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel(){
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel(){
        return this.takenPiecesPanel;
    }

    private BoardPanel getBoardPanel(){
        return this.boardPanel;
    }

    private void moveMadeUpdate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }



    private void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    public enum BoardDirection{
        NORMAL{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return boardTiles;
            }

            @Override
            BoardDirection opposite(){
                return FLIPPED;
            }
        },
        FLIPPED{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite(){
                return NORMAL;
            }
        };
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }
        //repaint method
        public void drawBoard(final Board board) {
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog{
        private final List<Move> moves;
        MoveLog(){
            this.moves = new ArrayList<>();
        }
        public List<Move> getMoves(){
            return this.moves;
        }
        public void addMove(final Move move){
            this.moves.add(move);
        }
        public int size(){
            return this.moves.size();
        }
        public void clear(){
            this.moves.clear();
        }

    }

    enum PlayerType{
        HUMAN, COMPUTER
    }

    private class TilePanel extends JPanel {
        private final int tileId;

        TilePanel(final BoardPanel boardPanel,
                  final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            //tile panel will know how to draw itself.
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            /*
               we are on a tile, for each of the 64 tiles, we are adding a listener that listens for click event
               if click happens we get tile id and assign in to source tile.
             */
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    //if the user presses the right mouse button on a tile,
                    // this will cancel piece selections i.e: deselecting
                    if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if (isLeftMouseButton(e)) {
                        //first click
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            //second click
                            destinationTile = chessBoard.getTile(tileId);
                            //System.out.println(chessBoard.getTile(tileId));
                            final Move move = Move.MoveFactory.createMove(chessBoard,
                                                                          sourceTile.getTileCoordinate(),
                                                                          destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            //System.out.println(sourceTile.getPiece().getTile() + "");
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(() -> {
                            gameHistoryPanel.redo(chessBoard, moveLog);
                            takenPiecesPanel.redo(moveLog);
                            if(gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                Table.get().moveMadeUpdate(PlayerType.HUMAN);
                            }
                            boardPanel.drawBoard(chessBoard);

                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()) {

                try {
                    /*all of my image files in a directory, each file references a piece
                        and that piece is going to be named in this way. Get piece alliance toString then concatenate
                        with actual piece and gif.
                        White => W
                        Black => B
                        ex: White Bishop would be "WB.gif"
                            Black Bishop would be "BB.gif"
                     */

                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath +
                            board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1) +
                            board.getTile(this.tileId).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void highlightLegals(final Board board){
            if(highlightLegalMoves){
                for(final Move move : pieceLegalMoves(board)){
                    if(move.getDestinationCoordinate() == this.tileId){
                        try{
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board){
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTileColor() {
            if (BoardUtils.FIRST_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] ||
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.SEVENTH_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SECOND_RANK[this.tileId] ||
                    BoardUtils.FOURTH_RANK[this.tileId] ||
                    BoardUtils.SIXTH_RANK[this.tileId] ||
                    BoardUtils.EIGHTH_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
