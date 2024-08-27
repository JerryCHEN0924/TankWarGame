package com.jerrychen0924.tankwar;

import java.awt.*;

class Missile {
    private static final int SPEED = 10;
    private int x;
    private int y;
    private final boolean enemy;
    private final Direction direction;
    private boolean live = true;

    Missile(int x, int y, boolean enemy, Direction direction) {
        this.x = x;
        this.y = y;
        this.enemy = enemy;
        this.direction = direction;
    }

    boolean isEnemy() {
        return enemy;
    }

    boolean isLive() {
        return live;
    }

    void setLive(boolean live) {
        this.live = live;
    }

    Image getImage() {
        return direction.getImage("missile");
    }

    void move() {
        x += direction.xFactor * SPEED;
        y += direction.yFactor * SPEED;
    }

    void draw(Graphics graphics) {
        move();
        if (x < 0 || x > 800 || y < 0 || y > 600) {
            this.live = false;
            return;
        }

        Rectangle rectangle = this.getRectangle();
        for (Wall wall : GameClient.getInstance().getWalls()){
            if (rectangle.intersects(wall.getRectangle())){
                this.live = false;
                return;
            }
        }
        if(enemy){
            Tank playerTank = GameClient.getInstance().getPlayerTank();
            if (rectangle.intersects(playerTank.getRectangle())){
                playerTank.setHp(playerTank.getHp() - 20);
                if(playerTank.getHp() <= 0){
                    playerTank.setLive(false);
                }
                this.live = false;
            }
        }else {
            for (Tank tank: GameClient.getInstance().getEnemyTanks()){
                if(rectangle.intersects(tank.getRectangle())){
                    tank.setLive(false);
                    this.live = false;
                    break;
                }
            }
        }
        graphics.drawImage(getImage(), x, y, null);
    }

    Rectangle getRectangle(){
        return new Rectangle(x,y,getImage().getWidth(null),getImage().getHeight(null));
    }
}
