package com.jerrychen0924.tankwar;

import java.awt.*;

class Explosion {

    private int x, y;
    private int step = 0;
    private boolean live = true;

    Explosion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    boolean isLive() {
        return live;
    }

    void setLive(boolean live) {
        this.live = live;
    }

    void draw(Graphics graphics) {
        if (step > 10 ){
            this.setLive(false);
            return;
        }
        graphics.drawImage(Tools.getImage(step++ + ".gif"), x, y, null);
    }
}
