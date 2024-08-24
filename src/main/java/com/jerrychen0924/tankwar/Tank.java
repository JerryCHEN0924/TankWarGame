package com.jerrychen0924.tankwar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

class Tank {

    private int x;
    private int y;
    private Direction direction;
    private boolean up, down, left, right;
    private boolean enemy;


    Tank(int x, int y, Direction direction) {
        this(x, y, false, direction);
    }

    Tank(int x, int y, boolean enemy, Direction direction) {
        this.x = x;
        this.y = y;
        this.enemy = enemy;
        this.direction = direction;
    }

    Image getImage() {
        String prefix = enemy ? "e" : "";
        switch (direction) {
            case UP:
                return Tools.getImage(prefix + "tankU.gif");
            case UPLEFT:
                return Tools.getImage(prefix + "tankLU.gif");
            case UPRIGHT:
                return Tools.getImage(prefix + "tankRU.gif");
            case DOWN:
                return Tools.getImage(prefix + "tankD.gif");
            case DOWNLEFT:
                return Tools.getImage(prefix + "tankLD.gif");
            case DOWNRIGHT:
                return Tools.getImage(prefix + "tankRD.gif");
            case LEFT:
                return Tools.getImage(prefix + "tankL.gif");
            case RIGHT:
                return Tools.getImage(prefix + "tankR.gif");
        }
        return null;
    }

     private void move() {
        if (this.stopped) return;
        switch (direction) {
            case UP:
                y -= 5;
                break;

            case UPLEFT:
                y -= 5;
                x -= 5;
                break;

            case UPRIGHT:
                y -= 5;
                x += 5;
                break;

            case DOWN:
                y += 5;
                break;

            case DOWNLEFT:
                x -= 5;
                y += 5;
                break;

            case DOWNRIGHT:
                x += 5;
                y += 5;
                break;

            case LEFT:
                x -= 5;
                break;

            case RIGHT:
                x += 5;
                break;
        }
    }

    private boolean stopped;

    private void determineDirection() {
        if (!up && !right && !down && !left) {
            this.stopped = true;
        } else {
            if (up && left && !down && !right) this.direction = Direction.UPLEFT;
            else if (up && !left && !down && right) this.direction = Direction.UPRIGHT;
            else if (up && !left && !down && !right) this.direction = Direction.UP;
            else if (!up && !left && down && !right) this.direction = Direction.DOWN;
            else if (!up && left && down && !right) this.direction = Direction.DOWNLEFT;
            else if (!up && !left && down && right) this.direction = Direction.DOWNRIGHT;
            else if (!up && left && !down && !right) this.direction = Direction.LEFT;
            else if (!up && !left && !down && right) this.direction = Direction.RIGHT;
            this.stopped = false;
        }

    }

    void draw(Graphics graphics) {
        int oldX = x, oldY = y;
        this.determineDirection();
        this.move();
        //加入螢幕範圍碰撞檢定，限制坦克移動不能超出邊界。
        if (x < 0) x = 0;
        else if (x > 800 - getImage().getWidth(null)) x = 800 - getImage().getWidth(null);
        if (y < 0) y = 0;
        else if (y > 600 - getImage().getHeight(null)) y = 600 - getImage().getHeight(null);

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
        for(Tank tank : GameClient.getInstance().getEnemyTanks()){
            if (rec.intersects(tank.getRectangle())){
                x = oldX;
                y = oldY;
                break;
            }
        }

        graphics.drawImage(this.getImage(), this.x, this.y, null);
    }

    private Rectangle getRectangle() {
        return new Rectangle(x, y, getImage().getWidth(null), getImage().getHeight(null));
    }

    void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                up = true;
                break;
            case KeyEvent.VK_DOWN:
                down = true;
                break;
            case KeyEvent.VK_LEFT:
                left = true;
                break;
            case KeyEvent.VK_RIGHT:
                right = true;
                break;
        }

    }

    void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                up = false;
                break;
            case KeyEvent.VK_DOWN:
                down = false;
                break;
            case KeyEvent.VK_LEFT:
                left = false;
                break;
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
        }
        this.determineDirection();
        this.move();
    }
}
