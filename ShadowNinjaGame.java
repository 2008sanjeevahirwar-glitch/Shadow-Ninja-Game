import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class ShadowNinjaGame extends JFrame {
    private GamePanel gamePanel;

    public ShadowNinjaGame() {
        setTitle("Shadow Ninja: Trials of the Dojo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        gamePanel = new GamePanel();
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShadowNinjaGame());
    }
}

class GamePanel extends JPanel implements KeyListener, Runnable {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int FPS = 60;

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Platform> platforms;
    private ArrayList<Coin> coins;
    private ArrayList<Trap> traps;
    private boolean[] keys = new boolean[256];
    private Thread gameThread;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        initializeGame();

        gameThread = new Thread(this);
        gameThread.start();
    }

    private void initializeGame() {
        player = new Player(100, 400);
        enemies = new ArrayList<>();
        enemies.add(new Enemy(300, 450));
        enemies.add(new Enemy(600, 250));

        platforms = new ArrayList<>();
        platforms.add(new Platform(0, 500, 800, 20));
        platforms.add(new Platform(200, 400, 200, 20));
        platforms.add(new Platform(500, 300, 200, 20));

        coins = new ArrayList<>();
        coins.add(new Coin(220, 380));
        coins.add(new Coin(520, 280));

        traps = new ArrayList<>();
        traps.add(new Trap(250, 480));
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long frameTime = 1000000000 / FPS;

        while (true) {
            long currentTime = System.nanoTime();
            if (currentTime - lastTime >= frameTime) {
                update();
                repaint();
                lastTime = currentTime;
            }
        }
    }

    private void update() {
        player.update(keys, platforms, enemies);

        for (Enemy enemy : enemies) {
            enemy.update(player);
        }

        for (Trap trap : traps) {
            trap.checkCollision(player);
        }

        coins.removeIf(coin -> coin.checkCollision(player));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw platforms
        g2d.setColor(Color.GRAY);
        for (Platform platform : platforms) {
            platform.draw(g2d);
        }

        // Draw coins
        g2d.setColor(Color.GREEN);
        for (Coin coin : coins) {
            coin.draw(g2d);
        }

        // Draw traps
        g2d.setColor(Color.RED);
        for (Trap trap : traps) {
            trap.draw(g2d);
        }

        // Draw player
        player.draw(g2d);

        // Draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g2d);
        }

        // Draw UI
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.drawString("Health: " + player.getHealth(), 10, 20);
        g2d.drawString("Coins: " + player.getCoins(), 10, 50);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() < 256) keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() < 256) keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

class Player {
    private int x, y, width = 40, height = 60;
    private int velX = 0, velY = 0;
    private int health = 100, coins = 0;
    private boolean onGround = false, doubleJump = true;
    private boolean stealth = false;
    private int dashCooldown = 0;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(boolean[] keys, ArrayList<Platform> platforms, ArrayList<Enemy> enemies) {
        velX = 0;
        if (keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A]) velX = -5;
        if (keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D]) velX = 5;

        stealth = keys[KeyEvent.VK_S];

        if (keys[KeyEvent.VK_SPACE] && onGround) {
            velY = -15;
            onGround = false;
        } else if (keys[KeyEvent.VK_SPACE] && doubleJump && !onGround) {
            velY = -15;
            doubleJump = false;
        }

        velY += 1; // Gravity

        x += velX;
        y += velY;

        onGround = false;
        for (Platform platform : platforms) {
            if (x + width > platform.x && x < platform.x + platform.width &&
                y + height >= platform.y && y + height <= platform.y + 20) {
                y = platform.y - height;
                velY = 0;
                onGround = true;
                doubleJump = true;
            }
        }

        if (keys[KeyEvent.VK_F]) {
            for (Enemy enemy : enemies) {
                if (Math.abs(x - enemy.x) < 100) {
                    enemy.takeDamage(20);
                }
            }
        }

        if (dashCooldown > 0) dashCooldown--;
    }

    public void draw(Graphics2D g) {
        g.setColor(stealth ? new Color(0, 0, 255, 100) : Color.BLUE);
        g.fillRect(x, y, width, height);
    }

    public int getHealth() { return health; }
    public int getCoins() { return coins; }
    public void addCoin() { coins++; }
    public void takeDamage(int damage) { health -= damage; }
}

class Enemy {
    public int x, y, width = 40, height = 60;
    public int health = 50;
    private int visionCone = 100;
    private boolean alerted = false;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(Player player) {
        if (Math.abs(x - player.x) < visionCone && !player.stealth) {
            alerted = true;
            if (x < player.x) x += 2;
            else x -= 2;
        } else {
            alerted = false;
            x += new Random().nextBoolean() ? 1 : -1;
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public void takeDamage(int damage) { health -= damage; }
}

class Platform {
    public int x, y, width, height;

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics2D g) {
        g.fillRect(x, y, width, height);
    }
}

class Coin {
    public int x, y, size = 20;

    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean checkCollision(Player player) {
        if (player.x + 40 > x && player.x < x + size &&
            player.y + 60 > y && player.y < y + size) {
            player.addCoin();
            return true;
        }
        return false;
    }

    public void draw(Graphics2D g) {
        g.fillOval(x, y, size, size);
    }
}

class Trap {
    public int x, y, width = 40, height = 20;

    public Trap(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void checkCollision(Player player) {
        if (player.x + 40 > x && player.x < x + width &&
            player.y + 60 > y && player.y < y + height) {
            player.takeDamage(10);
        }
    }

    public void draw(Graphics2D g) {
        g.fillRect(x, y, width, height);
    }
}
