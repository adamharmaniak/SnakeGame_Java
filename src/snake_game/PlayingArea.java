package snake_game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class PlayingArea extends JPanel implements ActionListener {

    private final int AREA_WIDTH = 300;
    private final int AREA_HEIGHT = 400;
    private final int DOT_SIZE = 10;
    private final int MAX_DOTS = 1200;

    private final int[] snake_x = new int[MAX_DOTS];
    private final int[] snake_y = new int[MAX_DOTS];

    private int dots;
    private int score;
    private long startTime;
    private int apple_x;
    private int apple_y;

    private boolean inGame = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean leftDirection = false;
    private boolean rightDirection = true;
//    private boolean isVictory = false;    // For "Game with Score Limit only"

    private Timer timer;
    private Image dot;
    private Image apple;
    private BufferedImage head;
    private Image logo;

    public PlayingArea() {
        initPlayingArea();
    }

    private void initPlayingArea() {
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(AREA_WIDTH, AREA_HEIGHT+ 60));
        loadImages();
        initGame();
    }

    private void loadImages() {
        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        dot = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = new BufferedImage(iih.getImage().getWidth(null), iih.getImage().getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = head.createGraphics();
        g2d.drawImage(iih.getImage(), 0, 0, null);
        g2d.dispose();

        ImageIcon iil = new ImageIcon("src/resources/logo.png");
        logo = iil.getImage();
    }

    private void initGame() {
        dots = 3;
        score = 0;
        startTime = System.currentTimeMillis();

        for (int z = 0; z < dots; z++) {
            snake_x[z] = 50 - z * 10;
            snake_y[z] = 50;
        }

        generateApple();

        int DELAY = 140;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawObjects(g);
    }

    private void drawObjects(Graphics g) {
        if (inGame) {

            g.drawImage(logo, (AREA_WIDTH - logo.getWidth(this)) / 2, 5, this);

            g.drawImage(apple, apple_x, apple_y + 60, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    drawRotatedHead(g, snake_x[z], snake_y[z] + 60);
                } else {
                    g.drawImage(dot, snake_x[z], snake_y[z] + 60, this);
                }
            }

            drawScore(g);
            drawTime(g);

            Toolkit.getDefaultToolkit().sync();

        } else {
            gameOver(g);
        }
    }

    private void drawRotatedHead(Graphics g, int x, int y) {
        double rotationRequired = 0;
        if (leftDirection) {
            rotationRequired = Math.toRadians(90);
        } else if (rightDirection) {
            rotationRequired = Math.toRadians(270);
        } else if (upDirection) {
            rotationRequired = Math.toRadians(180);
        } else if (downDirection) {
            rotationRequired = Math.toRadians(0);
        }

        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, (double) head.getWidth() / 2, (double) head.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedHead = op.filter(head, null);
        g.drawImage(rotatedHead, x, y, this);
    }

    private void drawScore(Graphics g) {
        String scoreText = "Score: " + score;
        Font small = new Font("Helvetica", Font.BOLD, 12);
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(scoreText, 225, 420);
    }

    private void drawTime(Graphics g) {
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        String timeText = "Time: " + elapsedTime + " s";
        Font small = new Font("Helvetica", Font.BOLD, 12);
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(timeText, 10, 420);
    }

    private void gameOver(Graphics g) {
        timer.stop();
        int timeRow = AREA_HEIGHT / 2 + 60;
        int scoreRow = AREA_HEIGHT / 2 + 40;
//        int msgResultRow = AREA_HEIGHT / 2 + 20;  // For "Game with Score Limit" only

//        String msgResult;     // For "Game with Score Limit" only
        String msg = "Game Over";
        String scoreMsg = "Final Score: " + score;
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        String timeMsg = "Time: " + elapsedTime + " s";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);

//         ** Uncomment this if you want to play with score limit **
//        if (isVictory) {
//            msgResult = "Congratulations! You Win!";
//            g.drawString(msgResult, (AREA_WIDTH - metr.stringWidth(msgResult)) / 2, msgResultRow);
//        }
//        else {
//            msgResult = "You Lose!";
//            g.drawString(msgResult, (AREA_WIDTH - metr.stringWidth(msgResult)) / 2, msgResultRow);
//        }

        g.drawString(msg, (AREA_WIDTH - metr.stringWidth(msg)) / 2, AREA_HEIGHT / 2);
        g.drawString(scoreMsg, (AREA_WIDTH - metr.stringWidth(scoreMsg)) / 2, scoreRow);
        g.drawString(timeMsg, (AREA_WIDTH - metr.stringWidth(timeMsg)) / 2, timeRow);
    }

    private void checkAppleSituation() {
        if ((snake_x[0] == apple_x) && (snake_y[0] == apple_y)) {
            dots++;
            score++;
            generateApple();
//            ** Uncomment this if you want to play with score limit **
//            if (score >= ) {
//                inGame = false;
//                isVictory = true;
//                timer.stop();
//            }
        }
    }

    private void move() {
        for (int z = dots; z > 0; z--) {
            snake_x[z] = snake_x[(z - 1)];
            snake_y[z] = snake_y[(z - 1)];
        }

        if (leftDirection) {
            snake_x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            snake_x[0] += DOT_SIZE;
        }

        if (upDirection) {
            snake_y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            snake_y[0] += DOT_SIZE;
        }
    }

    private void checkIfCollided() {
        for (int z = dots; z > 0; z--) {

            if ((z > 3) && (snake_x[0] == snake_x[z]) && (snake_y[0] == snake_y[z])) {
                inGame = false;
                break;
            }
        }

        if (snake_y[0] >= AREA_HEIGHT) {
            inGame = false;
        }

        if (snake_y[0] < 0) {
            inGame = false;
        }

        if (snake_x[0] >= AREA_WIDTH) {
            inGame = false;
        }

        if (snake_x[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void generateApple() {
        int RAND_POS = 29;
        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkAppleSituation();
            checkIfCollided();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
