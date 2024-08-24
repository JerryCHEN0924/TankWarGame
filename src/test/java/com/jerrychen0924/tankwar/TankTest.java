package com.jerrychen0924.tankwar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TankTest {

    @Test
    @DisplayName("坦克圖片是否存在")
    void getImage() {
        //遍歷所有方向的值，確認坦克圖片資源是否存在。
        for (Direction direction : Direction.values()) {
            Tank tank = new Tank(0, 0, false, direction);
            assertTrue(tank.getImage().getWidth(null) > 0, direction + "cannot get valid image!");

            Tank enemyTank = new Tank(0, 0, true, direction);
            assertTrue(enemyTank.getImage().getWidth(null) > 0, direction + "cannot get valid image!");
        }
    }
}