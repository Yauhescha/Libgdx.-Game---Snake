package com.hescha.snake;

public class Snake {
    private int snakeX = 0;
    private int snakeY = 0;
    private int snakeXBeforeUpdate = 0;
    private int snakeYBeforeUpdate = 0;

    public int getSnakeX() {
        return snakeX;
    }

    public void setSnakeX(int snakeX) {
        this.snakeX = snakeX;
    }

    public int getSnakeY() {
        return snakeY;
    }

    public void setSnakeY(int snakeY) {
        this.snakeY = snakeY;
    }

    public int getSnakeXBeforeUpdate() {
        return snakeXBeforeUpdate;
    }

    public void setSnakeXBeforeUpdate(int snakeXBeforeUpdate) {
        this.snakeXBeforeUpdate = snakeXBeforeUpdate;
    }

    public int getSnakeYBeforeUpdate() {
        return snakeYBeforeUpdate;
    }

    public void setSnakeYBeforeUpdate(int snakeYBeforeUpdate) {
        this.snakeYBeforeUpdate = snakeYBeforeUpdate;
    }
}
