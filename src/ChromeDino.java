import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;

public class ChromeDino extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 750;
    int boardHeight = 300;

    Image dinoImg;
    Image deadImg;
    Image jumpImg;
    Image oneCactusImg;
    Image twoCactusImg;
    Image treCactusImg;

    static class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;

        Block (int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int dinoWidth = 88;
    int dinoHeight = 94;
    int dinoX = 50;
    int dinoY = boardHeight - dinoHeight;

    Block dino;

    int oneCactusWidth = 34;
    int twoCactusWidth = 69;
    int treCactusWidth = 102;
    int cactusHeight = 70;
    int cactusX = 690;
    int cactusY = boardHeight - cactusHeight;
    ArrayList<Block> cactiArray;

    int velocityX = -13;
    int velocityY = 0;
    int gravity = 1;

    boolean gameOver = false;
    int score = 0;

    Timer gameLoop;
    Timer placeCacti;

    public ChromeDino() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.darkGray);
        setFocusable(true);
        addKeyListener(this);

        dinoImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-run.gif"))).getImage();
        deadImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-dead.png"))).getImage();
        jumpImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-jump.png"))).getImage();
        oneCactusImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cactus1.png"))).getImage();
        twoCactusImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cactus2.png"))).getImage();
        treCactusImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cactus3.png"))).getImage();

        // com uma classe Block (bloco) fica mais fácil de armazenar e alterar a posição na tela
        dino = new Block(dinoX, dinoY, dinoWidth, dinoHeight, dinoImg);
        cactiArray = new ArrayList<Block>();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        placeCacti = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeCactus();
            }
        });
        placeCacti.start();
    }

    void placeCactus() {
        if (gameOver) {
            return;
        }

        double cactusChance = Math.random();
        if (cactusChance > 0.90) {
            Block cactus = new Block(cactusX, cactusY, treCactusWidth, cactusHeight, treCactusImg);
            cactiArray.add(cactus);
        } else if (cactusChance > 0.70) {
            Block cactus = new Block(cactusX, cactusY, twoCactusWidth, cactusHeight, twoCactusImg);
            cactiArray.add(cactus);
        } else if (cactusChance > 0.50) {
            Block cactus = new Block(cactusX, cactusY, oneCactusWidth, cactusHeight, oneCactusImg);
            cactiArray.add(cactus);
        }

        // evitar que o arraylist fique grande demais e trave o jogo
        if (cactiArray.size() > 7) {
            cactiArray.removeFirst();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(dino.img, dino.x, dino.y, dino.width, dino.height, null);

        for (Block cactus : cactiArray) {
            g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), boardWidth / 2 - 124, boardHeight / 2);
        } else {
            g.drawString("Score: " + score, 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        // poderia ser "velocityY++;", mas com gravidade há mais lógica no jogo
        dino.y += velocityY;

        if (dino.y > dinoY) {
            dino.y = dinoY;
            velocityY = 0;
            dino.img = dinoImg;
        }

        for (Block cactus : cactiArray) {
            cactus.x += velocityX;

            if (collision(dino, cactus)) {
                gameOver = true;
                dino.img = deadImg;
            }
        }

        score++;
    }

    boolean collision(Block dino, Block cactus) {
        return dino.x < cactus.x + cactus.width &&
                dino.x + dino.width > cactus.x &&
                dino.y < cactus.y + cactus.height &&
                dino.y + dino.height > cactus.y;
    }

    @Override // feito 60 vezes num segundo, atualiza a posição a cada frame
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            gameLoop.stop();
            placeCacti.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (dino.y == dinoY) {
                velocityY = -17;
                dino.img = jumpImg;
            }

            if (gameOver) {
                dino.y = dinoY; // se estava no ar, volta
                dino.img = dinoImg;
                velocityY = 0;
                cactiArray.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placeCacti.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
