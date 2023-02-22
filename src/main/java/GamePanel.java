import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555)); //casting
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;

    GamePanel() {
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true); //Now it will read key strokes
        this.addKeyListener(new AL());// action listener - from inner class
        this.setPreferredSize(SCREEN_SIZE);

        //adding thread
        gameThread = new Thread(this); //"this" because we are implementing "Runnable" interface
        gameThread.start();

    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH/2) - (BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT - BALL_DIAMETER),
                BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPaddles() {
        paddle1 = new Paddle(0, (GAME_HEIGHT/2) - (PADDLE_HEIGHT/2),
                PADDLE_WIDTH, PADDLE_HEIGHT, 1); //where we want it to be

        paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH, (GAME_HEIGHT/2) - (PADDLE_HEIGHT/2),
                PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics); //drawing all the components
        g.drawImage(image, 0, 0, this); // this - JPanel - GamePanel
    }

    public void draw(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);

        Toolkit.getDefaultToolkit().sync();  // some shit to help with the animations
    }

    public void move() {
    paddle1.move();
    paddle2.move();
    ball.move();
    }

    public void checkCollision() {
        // stops paddles at window edges
        if(paddle1.y<=0)
            paddle1.y = 0;
        if (paddle1.y >= (GAME_HEIGHT - PADDLE_HEIGHT))
            paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;

        if(paddle2.y<=0)
            paddle2.y = 0;
        if (paddle2.y >= (GAME_HEIGHT - PADDLE_HEIGHT))
            paddle2.y = GAME_HEIGHT - PADDLE_HEIGHT;

        // bounce ball off the window edges
        if(ball.y <= 0) {
            ball.setYDirection(-ball.yVelocity);
        }

        if(ball.y >= GAME_HEIGHT - BALL_DIAMETER)
            ball.setYDirection(-ball.yVelocity);

        //bounce ball off the paddles ------------------------------------------------------------------
        // look into here, somethings fucked
        //think about adding something here
        if(ball.intersects(paddle1)){
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity = ball.xVelocity - random.nextInt(4); // + - because of vectors direction
            if(ball.yVelocity > 0)
                ball.yVelocity = ball.yVelocity + random.nextInt(3);
            else
                ball.yVelocity = ball.yVelocity - random.nextInt(1);
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);

        }

        if(ball.intersects(paddle2)){
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity = ball.xVelocity + random.nextInt(4);
            if(ball.yVelocity > 0)
                ball.yVelocity = ball.yVelocity + random.nextInt(3);
            else
                ball.yVelocity = ball.yVelocity- random.nextInt(1);
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        //give player 1 point for scoring and create new paddles and ball
        if(ball.x <= 0){
            score.player2++;
            newPaddles();
            newBall();
        }

        if(ball.x >= GAME_WIDTH - BALL_DIAMETER){
            score.player1++;
            newPaddles();
            newBall();
        }
    }

    public void run() {
        //game loop - copied from Minecraft XD
        long lastTime = System.nanoTime(); // current value of system timer in nanoseconds
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while(true) {
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;
            if (delta >= 1){
                move();
                checkCollision();
                repaint();
                delta--;

            }
        }

    }

    public class AL extends KeyAdapter {    // Action listener
        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }

    }
}
