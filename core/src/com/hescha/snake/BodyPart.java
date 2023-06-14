package com.hescha.snake;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BodyPart {
    private Snake snake;
    private Texture texture;
    private int x;
    private int y;

    public BodyPart(Texture texture, Snake snake) {
        this.texture = texture;
        this.snake = snake;
    }

    public void updateBodyPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Batch batch) {
        if (!(x == snake.getSnakeX() && y == snake.getSnakeY())) {
            batch.draw(texture, x, y);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
