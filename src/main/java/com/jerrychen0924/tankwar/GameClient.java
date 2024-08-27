package com.jerrychen0924.tankwar;


import com.sun.javafx.application.PlatformImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GameClient extends JComponent {
    //單例模式(Singleton)-餓漢式（Eager Initialization）-在loading到此類初始化時就建立(實例化)。
    private static final GameClient INSTANCE = new GameClient();
    public static final Random RANDOM = new Random();

    public static GameClient getInstance() {
        return INSTANCE;
    }

    private Tank playerTank;
    private List<Tank> enemyTanks;
    private final AtomicInteger enemyKilled = new AtomicInteger(0);
    private List<Wall> walls;
    private List<Missile> missiles;
    private List<Explosion> explosions;
    private Blood blood;

    Blood getBlood() {
        return blood;
    }

    void add(Missile missile) {
        missiles.add(missile);
    }

    Tank getPlayerTank() {
        return playerTank;
    }

    List<Missile> getMissiles() {
        return missiles;
    }

    void addExplostion(Explosion explosion) {
        explosions.add(explosion);
    }

    List<Wall> getWalls() {
        return walls;
    }

    List<Tank> getEnemyTanks() {
        return enemyTanks;
    }

    //初始化建構子
    private GameClient() {
        this.playerTank = new Tank(400, 100, Direction.DOWN);
        this.missiles = new CopyOnWriteArrayList<>();
        this.explosions = new ArrayList<>();
        this.blood = new Blood(400, 250);
        this.walls = Arrays.asList(
                new Wall(280, 140, true, 12),
                new Wall(280, 540, true, 12),
                new Wall(100, 160, false, 12),
                new Wall(700, 160, false, 12)
        );
        this.initEnemyTank();
        this.setPreferredSize(new Dimension(800, 600));
    }

    private void initEnemyTank() {
        this.enemyTanks = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                this.enemyTanks.add(new Tank(200 + j * 120, 400 + 40 * i, true, Direction.UP));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);
        if (!playerTank.isLive()) {
            g.setColor(Color.RED);
            g.setFont(new Font("null", Font.BOLD, 100));
            g.drawString("GAME OVER", 100, 200);
            g.setFont(new Font("null", Font.BOLD, 60));
            g.drawString("PRESS F2 TO RESTART", 60, 360);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font("null", Font.BOLD, 16));
            g.drawString("Missiles:" + missiles.size(), 10, 50);
            g.drawString("Explostions:" + explosions.size(), 10, 70);
            g.drawString("Player Tank HP:" + playerTank.getHp(), 10, 90);
            g.drawString("Enemy Left:" + enemyTanks.size(), 10, 110);
            g.drawString("Enemy Killed:" + enemyKilled.get(), 10, 130);
            g.drawImage(Tools.getImage("tree.png"), 720, 10, null);
            g.drawImage(Tools.getImage("tree.png"), 10, 520, null);

            playerTank.draw(g);
            if (playerTank.isDying() && RANDOM.nextInt(3) == 2) {
                blood.setLive(true);
            }
            if (blood.isLive()) {
                blood.draw(g);
            }

            int count = enemyTanks.size();
            enemyTanks.removeIf(t -> !t.isLive());
            enemyKilled.addAndGet(count - enemyTanks.size());
            if (enemyTanks.isEmpty()) {
                this.initEnemyTank();
            }
            for (Tank tank : enemyTanks) {
                tank.draw(g);
            }

            for (Wall wall : walls) {
                wall.draw(g);
            }

            missiles.removeIf(m -> !m.isLive());
            for (Missile missile : missiles) {
                missile.draw(g);
            }
            explosions.removeIf(e -> !e.isLive());
            for (Explosion explosion : explosions) {
                explosion.draw(g);
            }
        }
    }

    public static void main(String[] args) {
        PlatformImpl.startup(() -> {
        });
        JFrame frame = new JFrame();
        frame.setTitle("坦克大戰遊戲-練習用");
        frame.setIconImage(new ImageIcon("assets/images/icon.png").getImage());
        final GameClient client = GameClient.getInstance();
        client.repaint();
        frame.add(client);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                client.playerTank.keyReleased(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                client.playerTank.keyPressed(e);
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                client.repaint();
                if (client.playerTank.isLive()) {
                    for (Tank tank : client.enemyTanks) {
                        tank.actRandomly();
                    }
                }
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void restart() {
        if (!playerTank.isLive()) {
            playerTank = new Tank(400, 100, Direction.DOWN);
        }
        this.initEnemyTank();
    }
}
