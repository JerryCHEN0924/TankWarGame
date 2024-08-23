package com.jerrychen0924.tankwar;

import java.awt.*;

public class Wall {

    private int x;
    private int y;
    private boolean horizontal;
    private int bricks;

    public void draw(Graphics graphics){
        Image brickImage = Tools.getImage("brick.png");
        if(horizontal){
            for (int i = 0; i < bricks; i++) {
                graphics.drawImage(brickImage,x + i * brickImage.getWidth(null),y,null);
            }
        } else {
            for (int i = 0; i < bricks; i++) {
                graphics.drawImage(brickImage,x ,y + i * brickImage.getHeight(null),null);
            }
        }
    }

    public Wall(int x, int y, boolean horizontal, int bricks) {
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
        this.bricks = bricks;
    }
}
