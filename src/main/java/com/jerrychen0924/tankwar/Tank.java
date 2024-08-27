package com.jerrychen0924.tankwar;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

class Tank {
    private static final int MOVE_SPEED = 5;
    private static final int DISTANCE_TO_PET = 4;
    private static final int MAX_HP = 100;
    private int x;
    private int y;
    private Direction direction;
    private boolean enemy;
    private boolean live = true;
    private int hp = MAX_HP;
    private final Random random = new Random();
    private int step = random.nextInt(12) + 3;
    private int code;

    Tank(int x, int y, Direction direction) {
        this(x, y, false, direction);
    }

    Tank(Save.Position position, boolean enemy) {
        this(position.getX(), position.getY(), enemy, position.getDirection());
    }

    Tank(int x, int y, boolean enemy, Direction direction) {
        this.x = x;
        this.y = y;
        this.enemy = enemy;
        this.direction = direction;
    }

    boolean isLive() {
        return live;
    }

    void setLive(boolean live) {
        this.live = live;
    }

    int getHp() {
        return hp;
    }

    void setHp(int hp) {
        this.hp = hp;
    }

    Image getImage() {
        String prefix = enemy ? "e" : "";
        return direction.getImage(prefix + "tank");
    }

    private void move() {
        if (this.stopped) return;
        x += direction.xFactor * MOVE_SPEED;
        y += direction.yFactor * MOVE_SPEED;
    }

    boolean isDying() {
        return this.hp <= MAX_HP * 0.2;
    }

    private boolean stopped;

    private void determineDirection() {
        Direction newDirection = Direction.get(code);
        if (newDirection == null) {
            this.stopped = true;
        } else {
            this.direction = newDirection;
            this.stopped = false;
        }
    }

    void draw(Graphics graphics) {
        int oldX = x, oldY = y;
        this.move();
        //加入螢幕範圍碰撞檢定，限制坦克移動不能超出邊界。
        if (x < 0) {
            x = 0;
        } else if (x > GameClient.WIDTH - getImage().getWidth(null)) {
            x = GameClient.WIDTH - getImage().getWidth(null);
        }
        if (y < 0) {
            y = 0;
        } else if (y > GameClient.HEIGHT - getImage().getHeight(null)) {
            y = GameClient.HEIGHT - getImage().getHeight(null);
        }

        //加入牆壁碰撞檢定，限制坦克不能穿牆。
        Rectangle rec = this.getRectangle();
        for (Wall wall : GameClient.getInstance().getWalls()) {
            if (rec.intersects(wall.getRectangle())) {
                x = oldX;
                y = oldY;
                break;
            }
        }
        //加入敵方坦克碰撞檢定，限制坦克不能穿越敵方坦克。
        for (Tank tank : GameClient.getInstance().getEnemyTanks()) {
            if (tank != this && rec.intersects(tank.getRectangle())) {
                x = oldX;
                y = oldY;
                break;
            }
        }
        if (this.enemy && rec.intersects(GameClient.getInstance().getPlayerTank().getRectangle())) {
            x = oldX;
            y = oldY;
        }

        if (!enemy) {
            Blood blood = GameClient.getInstance().getBlood();
            if (this.getRectangle().intersects(GameClient.getInstance().getBlood().getRectangle())) {
                this.hp = MAX_HP;
                Tools.playAudio("revive.wav");
                blood.setLive(false);
            }
            graphics.setColor(Color.WHITE);
            graphics.fillRect(x, y - 10, this.getImage().getWidth(null), 10);

            //我方坦克血量
            graphics.setColor(Color.RED);
            int width = hp * getImage().getWidth(null) / MAX_HP;
            graphics.fillRect(x, y - 10, width, 10);

            //我方坦克寵物
            Image petImage = Tools.getImage("pet-camel.gif");
            graphics.drawImage(petImage, this.x - petImage.getWidth(null) - DISTANCE_TO_PET, this.y, null);
        }

        graphics.drawImage(this.getImage(), this.x, this.y, null);
    }

    Rectangle getRectangle() {
        if (enemy) {
            return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
        } else {
            Image petImage = Tools.getImage("pet-camel.gif");
            int delta = petImage.getWidth(null) + DISTANCE_TO_PET;
            return new Rectangle(x - delta, y, getImage().getWidth(null) + delta, getImage().getHeight(null));
        }
    }

    Rectangle getRectangleForHitDetection() {
        return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
    }

    void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                // |= 是"按位或", 兩個位運算符的複合賦值運算符。
                //它將變數的值與指定的值進行按位或運算，然後將結果賦給該變數。
                code |= Direction.UP.code;
                break;
            case KeyEvent.VK_DOWN:
                code |= Direction.DOWN.code;
                break;
            case KeyEvent.VK_LEFT:
                code |= Direction.LEFT.code;
                break;
            case KeyEvent.VK_RIGHT:
                code |= Direction.RIGHT.code;
                break;
            case KeyEvent.VK_CONTROL:
                fire();
                break;
            case KeyEvent.VK_A:
                superFire();
                break;
            case KeyEvent.VK_F2:
                GameClient.getInstance().restart();
                break;
        }
        this.determineDirection();
    }

    private void superFire() {
        for (Direction direction : Direction.values()) {
            Missile missile = new Missile(x + getImage().getWidth(null) / 2 - 6, y + getImage().getHeight(null) / 2 - 6, enemy, direction);
            GameClient.getInstance().getMissiles().add(missile);
        }

        String audioFile = new Random().nextBoolean() ? "supershoot.aiff" : "supershoot.wav";
        Tools.playAudio(audioFile);
    }

    private void fire() {
        Missile missile = new Missile(x + getImage().getWidth(null) / 2 - 6, y + getImage().getHeight(null) / 2 - 6, enemy, direction);
        GameClient.getInstance().getMissiles().add(missile);

        Tools.playAudio("shoot.wav");
    }

    void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                // ^= 是"按位異或", 兩個位運算符的複合賦值運算符。
                //它將變數的值與指定的值進行按位或運算，然後將結果賦給該變數。
                code ^= Direction.UP.code;
                break;
            case KeyEvent.VK_DOWN:
                code ^= Direction.DOWN.code;
                break;
            case KeyEvent.VK_LEFT:
                code ^= Direction.LEFT.code;
                break;
            case KeyEvent.VK_RIGHT:
                code ^= Direction.RIGHT.code;
                break;
        }
        this.determineDirection();
    }

    Save.Position getPosition() {
        return new Save.Position(x, y, direction);
    }

    void actRandomly() {
        Direction[] dir = Direction.values();
        if (step == 0) {
            step = random.nextInt(12) + 3;
            this.direction = dir[random.nextInt(dir.length)];
            if ((random.nextBoolean())) {
                this.fire();
            }
        }
        step--;
    }
}
