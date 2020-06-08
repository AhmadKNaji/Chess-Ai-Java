package com.chess.engine.board;
public class BoardUtils {
    //initiate an array of boolean to handle exception to piece's location.
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);
    public static final boolean[] FIRST_RANK = initRow(56);
    public static final boolean[] SECOND_RANK = initRow(48);
    public static final boolean[] THIRD_RANK = initRow(40);
    public static final boolean[] FOURTH_RANK = initRow(32);
    public static final boolean[] FIFTH_RANK = initRow(24);
    public static final boolean[] SIXTH_RANK = initRow(16);
    public static final boolean[] SEVENTH_RANK = initRow(8);
    public static final boolean[] EIGHTH_RANK = initRow(0);
    public static final String[] ALGEBRAIC_NOTATION = initializeAlgebraicNotation();


    private static String[] initializeAlgebraicNotation() {
        return new String[] {
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        };
    }




    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private static boolean[] initColumn(int columnNumber) {
        //initialize array of booleans at size of chess board.
        final boolean[] column = new boolean[NUM_TILES];
        //iterate through the columns.
        do{
            //make the column true.
            column[columnNumber] = true;
            //ex: column1 = 0; column2 = 8, column3 = 16 etc...
            columnNumber += NUM_TILES_PER_ROW;
        }while(columnNumber < NUM_TILES);
        return column;
    }

    private static boolean[] initRow(int rowNumber) {
        //initialize array of booleans at size of chess board.
        final boolean[] row = new boolean[NUM_TILES];
        //iterate through the rows.
        do {
            //it would assign the tiles along the row to true.
            row[rowNumber] = true;
            rowNumber++;
        }while(rowNumber % NUM_TILES_PER_ROW != 0);

        return row;
    }

    //prevent the user from instantiating this class.
    private BoardUtils(){
        throw new RuntimeException("You cannot instantiate me!");
    }

    //keep in check if the legal move is within the board limit.
    public static boolean isValidTileCoordinate(final int coordinate) {
        return (coordinate >= 0 && coordinate < NUM_TILES);
    }


    public static String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION[coordinate];
    }

}
