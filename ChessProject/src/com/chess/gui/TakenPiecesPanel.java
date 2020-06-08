package com.chess.gui;

import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.chess.gui.Table.*;

public class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel;
    private final JPanel southPanel;
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(80, 80);
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public TakenPiecesPanel(){
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        add(this.northPanel, BorderLayout.NORTH);
        add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final MoveLog moveLog){
        this.southPanel.removeAll();
        this.northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for(final Move move : moveLog.getMoves()){
            if(move.isAttack()){
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite()){
                    whiteTakenPieces.add(takenPiece);
                }else if(takenPiece.getPieceAlliance().isBlack()){
                    blackTakenPieces.add(takenPiece);
                }else {
                    throw new RuntimeException("Should not reach here!");
                }
            }
        }
        whiteTakenPieces.sort((o1, o2) -> Ints.compare(o1.getPieceValue(), o2.getPieceValue()));
        blackTakenPieces.sort((o1, o2) -> Ints.compare(o1.getPieceValue(), o2.getPieceValue()));

        for(final Piece takenPiece : whiteTakenPieces){
            try{
                File f = new File("art/pieces/plain/" +
                        takenPiece.getPieceAlliance().toString().substring(0,1) +
                        "" +
                        takenPiece.toString() +
                        ".gif");
                final BufferedImage image = ImageIO.read(f);
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(icon);
                this.northPanel.add(imageLabel);

            }catch(final IOException e){
                e.printStackTrace();
            }
        }

        for(final Piece takenPiece : blackTakenPieces){
            try{
                File f = new File("art/pieces/plain/" +
                                            takenPiece.getPieceAlliance().toString().substring(0,1) +
                                            "" +
                                            takenPiece.toString() +
                                            ".gif");
                final BufferedImage image = ImageIO.read(f);
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(icon);
                this.southPanel.add(imageLabel);


            }catch(final IOException e){
                e.printStackTrace();
            }
        }

        validate();

    }
}
