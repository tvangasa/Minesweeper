package Minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Minesweeper {
	private class MineTile extends JButton {
		private int row;
		private int col;

		public MineTile(int row, int col) {
			this.setRow(row);
			this.setCol(col);
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getCol() {
			return col;
		}

		public void setCol(int col) {
			this.col = col;
		}

	}

	int tileSSize = 70;
	int numRows = 8;
	int numCols = numRows;
	int boardWidth = numCols * tileSSize;
	int boardHeight = numRows * tileSSize;

	JFrame frame = new JFrame("Minesweeper");
	JLabel textLabel = new JLabel();
	JPanel textPanel = new JPanel();
	JPanel boardPanel = new JPanel();

	int mineCount = 10;
	MineTile[][] board = new MineTile[numRows][numCols];
	ArrayList<MineTile> mineList;
	Random random = new Random();
	int tilesClicked = 0; // reveal all tiles except mines
	boolean gameOver = false;

	Minesweeper() {
		frame.setSize(boardWidth, boardHeight);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		textLabel.setFont(new Font("Arial", Font.BOLD, 25));
		textLabel.setHorizontalAlignment(JLabel.CENTER);
		textLabel.setText("MineSweeper: " + Integer.toString(mineCount));
		textLabel.setOpaque(true);

		textPanel.setLayout(new BorderLayout());
		textPanel.add(textLabel);
		frame.add(textPanel, BorderLayout.NORTH);

		boardPanel.setLayout(new GridLayout(numRows, numCols)); // 8x8
		boardPanel.setBackground(Color.gray);
		frame.add(boardPanel, BorderLayout.CENTER);

		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				MineTile tile = new MineTile(r, c);
				board[r][c] = tile;

				tile.setFocusable(false);
				tile.setMargin(new Insets(0, 0, 0, 0));
				tile.setFont(new Font("Arial Unicode Ms", Font.PLAIN, 45));
				// tile.setText("💣");
				boardPanel.add(tile);
				tile.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (gameOver) {
							return;
						}
						MineTile tile = (MineTile) e.getSource();
						// left click
						if (e.getButton() == MouseEvent.BUTTON1) {
							if (tile.getText() == "") {
								if (mineList.contains(tile)) {
									revealMines();
								} else {
									checkMine(tile.row, tile.col);
								}
							}
						}
						// right click
						else if (e.getButton() == MouseEvent.BUTTON3) {
							if (tile.getText() == "" && tile.isEnabled()) {
								tile.setText("🚩");
							} else if (tile.getText() == "🚩") {
								tile.setText("");
							}
						}
					}
				});
			}
		}
		frame.setVisible(true);
		setMines();
	}

	void setMines() {
		mineList = new ArrayList<MineTile>();
		int mineLeft = mineCount;
		while (mineLeft > 0) {
			int r = random.nextInt(numRows); // 0-7
			int c = random.nextInt(numCols); // 0-7

			MineTile tile = board[r][c];
			if (!mineList.contains(tile)) {
				mineList.add(tile);
				mineLeft -= 1;
			}
		}
	}

	void revealMines() {
		for (int i = 0; i < mineList.size(); i++) {
			MineTile tile = mineList.get(i);
			tile.setText("💣");
		}

		gameOver = true;
		textLabel.setText("Game Over!");
	}

	void checkMine(int r, int c) {
		if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
			return;
		}
		MineTile tile = board[r][c];
		if (!tile.isEnabled()) {
			return;
		}
		tile.setEnabled(false);
		tilesClicked += 1;
		int minesFound = 0;
		// top 3
		minesFound += countMine(r - 1, c - 1); // top left
		minesFound += countMine(r - 1, c); // top
		minesFound += countMine(r - 1, c + 1); // top right
		// left and right
		minesFound += countMine(r, c - 1); // left
		minesFound += countMine(r, c + 1); // right
		// bottom
		minesFound += countMine(r + 1, c - 1); // bottom left
		minesFound += countMine(r + 1, c); // bottom
		minesFound += countMine(r + 1, c + 1); // bottom right

		if (minesFound > 0) {
			tile.setText(Integer.toString(minesFound));
		} else {
			tile.setText("");

			// top 3
			checkMine(r - 1, c - 1); // top left
			checkMine(r - 1, c); // top
			checkMine(r - 1, c + 1); // top right
			// left and right
			checkMine(r, c - 1); // left
			checkMine(r, c + 1); // right
			// bottom 3
			checkMine(r + 1, c - 1); // bottom left
			checkMine(r + 1, c); // bottom
			checkMine(r + 1, c + 1); // bottom right

		}
		if (tilesClicked == numRows * numCols - mineList.size()) {
			gameOver = true;
			textLabel.setText("Mines Cleard!");
		}
	}

	int countMine(int r, int c) {
		if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
			return 0;
		}
		if (mineList.contains(board[r][c])) {
			return 1;
		}
		return 0;
	}

	public static void main(String[] args) {
		new Minesweeper();
	}
}