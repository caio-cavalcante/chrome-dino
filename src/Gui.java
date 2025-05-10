import javax.swing.*;

public class Gui {
    public static void main(String[] args) {
        int boardWidth = 750;
        int boardHeight = 300;

        JFrame frame = new JFrame("Chrome Dinosaur");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ChromeDino chromeDino = new ChromeDino();
        frame.add(chromeDino);
        frame.pack();
        chromeDino.requestFocus();
        frame.setVisible(true);
    }
}
