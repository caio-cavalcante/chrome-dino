import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;

public class ChromeDino extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 750;
    int boardHeight = 300;

    Image cloudImg;
    Image dinoImg;
    Image deadImg;
    Image duckImg;
    Image jumpImg;
    Image oneCactusImg;
    Image twoCactusImg;
    Image treCactusImg;
    Image birdImg;

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
    int cactusX = 759;
    int cactusY = boardHeight - cactusHeight;
    ArrayList<Block> cactiArray;

    int cloudWidth = 84;
    int cloudHeight = 101;
    int cloudX = 759;
    int cloudY = 40;
    ArrayList<Block> cloudArray;

    int birdWidth = 97;
    int birdHeight = 68;
    int birdX = 759;
    int birdY = boardHeight - birdHeight - dinoHeight + 10;
    ArrayList<Block> flockArray;

    int velocityX = -13;
    int velocityY = 0;
    int gravity = 1;

    boolean gameOver = false;
    int score = 0;
    int highScore = 0;

    Timer cloudMove;
    Timer gameLoop;
    Timer placeCacti;
    Timer birdsFly;

    public ChromeDino() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.darkGray);
        setFocusable(true);
        addKeyListener(this);

        cloudImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cloud.png"))).getImage();
        dinoImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-run.gif"))).getImage();
        deadImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-dead.png"))).getImage();
        duckImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-duck1.png"))).getImage();
        jumpImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/dino-jump.png"))).getImage();
        oneCactusImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cactus1.png"))).getImage();
        twoCactusImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cactus2.png"))).getImage();
        treCactusImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/cactus3.png"))).getImage();
        birdImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./img/bird.gif"))).getImage();

        // com uma classe Block (bloco) fica mais fácil de armazenar e alterar a posição na tela
        dino = new Block(dinoX, dinoY, dinoWidth, dinoHeight, dinoImg);
        cactiArray = new ArrayList<>();
        cloudArray = new ArrayList<>();
        flockArray = new ArrayList<>();

        cloudMove = new Timer(2650, _ -> newClouds());
        cloudMove.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        placeCacti = new Timer(1500, _ -> placeCactus());
        placeCacti.start();

        birdsFly = new Timer(2125, _ -> flockFlying());
        birdsFly.start();
    }

    void newClouds() {
        if (gameOver) {
            return;
        }

        Block cloud = new Block(cloudX, cloudY, cloudWidth, cloudHeight, cloudImg);
        cloudArray.add(cloud);

        if (cloudArray.size() > 3) {
            cloudArray.removeFirst();
        }
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

    void flockFlying() {
        if (gameOver || score < 863) {
            return;
        }

        Block bird = new Block(birdX, birdY, birdWidth, birdHeight, birdImg);
        flockArray.add(bird);

        if (flockArray.size() > 3) {
            flockArray.removeFirst();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(dino.img, dino.x, dino.y, dino.width, dino.height, null);

        for (Block cloud : cloudArray) {
            g.drawImage(cloud.img, cloud.x, cloud.y, cloud.width, cloud.height, null);
        }

        for (Block cactus : cactiArray) {
            g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }

        for (Block bird : flockArray) {
            g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (gameOver) {
            String newHighScoreText = "New high score! " + score;
            String highScoreText = "High Score: " + highScore;
            String gameOverText = "Game Over.";
            String gameOverScoreText = "Game Over: " + score;
            String restartText = "Press Space to restart";

            int newHighScoreWidth = g.getFontMetrics().stringWidth(newHighScoreText);
            int highScoreWidth = g.getFontMetrics().stringWidth(highScoreText);
            int gameOverWidth = g.getFontMetrics().stringWidth(gameOverText);
            int gameOverScoreWidth = g.getFontMetrics().stringWidth(gameOverScoreText);
            int restartWidth = g.getFontMetrics().stringWidth(restartText);

            int centerX = boardWidth / 2;

            if (score > highScore) {
                g.drawString(newHighScoreText, centerX - newHighScoreWidth / 2, boardHeight / 2 - 40);
                g.drawString(gameOverText, centerX - gameOverWidth / 2, boardHeight / 2);
            } else {
                g.drawString(highScoreText, centerX - highScoreWidth / 2, boardHeight / 2 - 40);
                g.drawString(gameOverScoreText, centerX - gameOverScoreWidth / 2, boardHeight / 2);
            }
            g.drawString(restartText, centerX - restartWidth / 2, boardHeight / 2 + 40);
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

        for (Block cloud : cloudArray) {
            cloud.x = cloud.x + velocityX + 5;
        }

        for (Block bird : flockArray) {
            bird.x = bird.x + velocityX + 2;

            if (collision(dino, bird)) {
                gameOver = true;
                dino.img = deadImg;
            }
        }

        score++;
    }

    boolean collision(Block dino, Block obstacle) {
        return dino.x < obstacle.x + obstacle.width &&
                dino.x + dino.width > obstacle.x &&
                dino.y < obstacle.y + obstacle.height &&
                dino.y + dino.height > obstacle.y;
    }

    @Override // feito 60 vezes num segundo, atualiza a posição a cada frame
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            cloudMove.stop();
            gameLoop.stop();
            placeCacti.stop();
            birdsFly.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (dino.y == dinoY) {
                velocityY = -17;
                dino.img = jumpImg;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                if (score > highScore) {
                    highScore = score;
                }
                dino.y = dinoY; // se estava no ar, volta
                dino.img = dinoImg;

                velocityY = 0;
                score = 0;
                gameOver = false;

                cactiArray.clear();
                cloudArray.clear();
                flockArray.clear();

                gameLoop.start();
                placeCacti.start();
                birdsFly.start();
                cloudMove.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}