import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class PongGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PongGame::createMainMenu);
    }

    private static void createMainMenu() {
        JFrame menuFrame = new JFrame("Pong - Main Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400, 300);
        menuFrame.setLocationRelativeTo(null);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(4, 1, 10, 10));

        JLabel titleLabel = new JLabel("Pong", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));

        JButton startButton = new JButton("2 Spieler Modus");
        startButton.addActionListener(e -> {
            menuFrame.dispose();
            SwingUtilities.invokeLater(() -> new GameFrame(false));
        });

        JButton aiButton = new JButton("Gegen Computer spielen");
        aiButton.addActionListener(e -> {
            menuFrame.dispose();
            SwingUtilities.invokeLater(() -> new GameFrame(true));
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(titleLabel);
        menuPanel.add(startButton);
        menuPanel.add(aiButton);
        menuPanel.add(exitButton);

        menuFrame.add(menuPanel);
        menuFrame.setVisible(true);
    }

    static class GameFrame extends JFrame {

        public GameFrame(boolean aiMode) {
            setTitle("Pong");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(800, 600);
            setLocationRelativeTo(null);
            setResizable(false);

            GamePanel gamePanel = new GamePanel(aiMode);
            add(gamePanel);

            setVisible(true);
        }
    }

    static class GamePanel extends JPanel implements ActionListener {

        private final int PADDLE_WIDTH = 10;
        private final int PADDLE_HEIGHT = 100;
        private final int BALL_SIZE = 20;
        private final int SPEED = 5;

        private int paddle1Y = 250, paddle2Y = 250;
        private int ballX, ballY;
        private int ballXSpeed, ballYSpeed;
        private boolean aiMode;

        private int scorePlayer1 = 0;
        private int scorePlayer2 = 0;

        private Timer timer;
        private boolean up1, down1, up2, down2;
        private Random random = new Random();

        public GamePanel(boolean aiMode) {
            this.aiMode = aiMode;
            setBackground(Color.BLACK);
            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W -> up1 = true;
                        case KeyEvent.VK_S -> down1 = true;
                        case KeyEvent.VK_UP -> up2 = true;
                        case KeyEvent.VK_DOWN -> down2 = true;
                    }
                }
                @Override
                public void keyReleased(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W -> up1 = false;
                        case KeyEvent.VK_S -> down1 = false;
                        case KeyEvent.VK_UP -> up2 = false;
                        case KeyEvent.VK_DOWN -> down2 = false;
                    }
                }
            });
            resetBall();
            timer = new Timer(16, this);
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(20, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.fillRect(getWidth() - 30, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(scorePlayer1 + " - " + scorePlayer2, getWidth() / 2 - 20, 30);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (up1 && paddle1Y > 0) paddle1Y -= SPEED;
            if (down1 && paddle1Y < getHeight() - PADDLE_HEIGHT) paddle1Y += SPEED;
            if (aiMode) {
                if (ballY > paddle2Y + PADDLE_HEIGHT / 2 + random.nextInt(20) - 10) paddle2Y += SPEED;
                if (ballY < paddle2Y + PADDLE_HEIGHT / 2 + random.nextInt(20) - 10) paddle2Y -= SPEED;
            } else {
                if (up2 && paddle2Y > 0) paddle2Y -= SPEED;
                if (down2 && paddle2Y < getHeight() - PADDLE_HEIGHT) paddle2Y += SPEED;
            }

            ballX += ballXSpeed;
            ballY += ballYSpeed;
            if (ballY <= 0 || ballY >= getHeight() - BALL_SIZE) ballYSpeed = -ballYSpeed;
            if (ballX <= 30 && ballY + BALL_SIZE >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT) {
                ballXSpeed = -ballXSpeed + random.nextInt(3) - 1;
                ballYSpeed += random.nextInt(3) - 1;
            } else if (ballX >= getWidth() - 50 && ballY + BALL_SIZE >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT) {
                ballXSpeed = -ballXSpeed + random.nextInt(3) - 1;
                ballYSpeed += random.nextInt(3) - 1;
            }

            if (ballX <= 0) { scorePlayer2++; resetBall(); }
            if (ballX >= getWidth()) { scorePlayer1++; resetBall(); }
            repaint();
        }

        private void resetBall() {
            ballX = getWidth() / 2 - BALL_SIZE / 2;
            ballY = getHeight() / 2 - BALL_SIZE / 2;
            ballXSpeed = SPEED * (random.nextBoolean() ? 1 : -1);
            ballYSpeed = SPEED * (random.nextBoolean() ? 1 : -1);
        }
    }
}
