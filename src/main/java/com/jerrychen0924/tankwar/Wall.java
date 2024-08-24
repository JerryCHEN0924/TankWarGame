package com.jerrychen0924.tankwar;

import java.awt.*;

class Wall {

    private int x;
    private int y;
    private boolean horizontal;
    private int bricks;
    private final Image brickImage;

    Rectangle getRectangle() {
        return horizontal ? new Rectangle(x, y,
                bricks * brickImage.getWidth(null), brickImage.getHeight(null)) :
                new Rectangle(x, y, brickImage.getWidth(null), brickImage.getHeight(null) * bricks);
    }

    Wall(int x, int y, boolean horizontal, int bricks) {
        this.brickImage = Tools.getImage("brick.png");
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
        this.bricks = bricks;
    }

    void draw(Graphics graphics) {
        if (horizontal) {
            for (int i = 0; i < bricks; i++) {
                graphics.drawImage(brickImage, x + i * brickImage.getWidth(null), y, null);
            }
        } else {
            for (int i = 0; i < bricks; i++) {
                graphics.drawImage(brickImage, x, y + i * brickImage.getHeight(null), null);
            }
        }
    }
}
