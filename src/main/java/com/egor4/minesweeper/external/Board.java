package com.egor4.minesweeper.external;

public class Board {
    public int height = 9;		//default height of board
    public int width = 9;		//default width of board
    public int mines = 10;		//default number of mines
    public int uncovered = 0;	//number of non-mines uncovered
    public int[][] values;		//# of adjacent mines, -1 if mine
    public boolean[][] board;	//false: covered, true: uncovered
    public boolean endGame;	//false, becomes true if mine is uncovered or all 								cells are cleared
    public boolean flagMode = false;

    //default constructor, generates a board of height 9, width 9, and with 9 mines
    public Board(){
        this.values = new int[this.height][this.width];
        this.board = new boolean[this.height][this.width];
        placeMines();
        setValues();
    }

    //constructor for board with non-default height, width, and mine count
    public Board(int h, int w, int m) {
        this.height = h;
        this.width = w;
        this.mines = m;
        this.values = new int[this.height][this.width];
        this.board = new boolean[this.height][this.width];
        placeMines();
        setValues();
    }

    //places mines on the board
    public void placeMines() {
        int placed = 0;		//number of mines placed
        int y = 0;		//vertical coordinate
        int x = 0;		//horizontal coordinate

        //loops until number of mines placed equals the mine count
        while (placed < this.mines) {
            y = (int)(Math.random() * this.height);
            x = (int)(Math.random() * this.width);
            if (values[y][x] != -1) {
                values[y][x] = -1;
                placed++;
            }
        }
    }

    //populates values of board, -1 for mine and # of adj mines for non-mine
    public void setValues() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (values[i][j] != -1) {		//check if mine
                    values[i][j] = checkAdj(i, j);	//sets sum of adj mines to values[][]
                }
            }
        }
    }

    //returns the number of a cell's adjacent mines
    public int checkAdj(int y, int x) {
        int sum = 0;	//number of adjacent mines
        for (int a = -1; a <= 1; a++) {
            if (y + a >= 0 && y + a <= this.height - 1) {	//keeps check within height
                for (int b = -1; b <= 1; b++) {
                    //keeps check within width & skips over the center cell
                    if (x + b >= 0 && x + b <= this.width - 1 && (b != 0 || a != 0)) {
                        if (values[y + a][x + b] == -1) sum++;
                    }
                }
            }
        }
        return sum;
    }

    //uncovers the cell to reveal its value (-1 for mine or # for adj mines)
    public void uncover(int y, int x) {
        if(flagMode){
            setFlag(x, y);
            return;
        }
        if (values[y][x] == -1)	{	//if cell contains a mine, game is over
            endGame = true;
            board[y][x] = true;
            return;
        }
        else if (values[y][x] == 0) {	//if cell has no adj mines, uncover adj cells
            if (!board[y][x]) {
                board[y][x] = true;	//cell is uncovered
                uncovered++;		//not a mine, increment # of non-mines uncovered
            }
            for (int a = -1; a <= 1; a++) {
                if (y + a >= 0 && y + a <= this.height - 1) {	//keeps check within height
                    for (int b = -1; b <= 1; b++) {
                        //keeps check within width & skips over the center cell
                        if (x + b >= 0 && x + b <= this.width - 1 && (b != 0 || a != 0)) {
                            if (!board[y + a][x + b])
                                uncover(y + a, x + b);
                        }
                    }
                }
            }
        }
        else
        if (!board[y][x]) {
            board[y][x] = true;	//cell is uncovered
            uncovered++;		//not a mine, increment # of non-mines uncovered
        }
    }

    //checks end-game conditions: mine is uncovered or all non-mine cells are uncovered
    public boolean getGameState() {
        //all non-mines are uncovered
        if (uncovered >= width * height - mines) {
            endGame = true;

        }
        //game is over but some non-mines are still covered (mine was uncovered)
        return this.endGame;
    }

    public void setFlag(int y, int x) {
        if (values[x][y] == -5) {
            values[x][y] = checkAdj(x,y);
        } else {
            board[x][y] = true;
            values[x][y] = -5;
        }
    }
    //displays the board, using X for covered cells and a cell's value for uncovered cells
}