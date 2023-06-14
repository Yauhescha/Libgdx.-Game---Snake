package com.hescha.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {
    public static final float MOVE_TIME = 0.1F;
    public static final int SNAKE_MOVEMENT = 32;
    private float timer = MOVE_TIME;
    private int snakeX = 0;
    private int snakeY = 0;
    private int snakeXBeforeUpdate = 0;
    private int snakeYBeforeUpdate = 0;
    private MovingDirection direction = MovingDirection.RIGHT;
    private boolean isAppleAvailable;
    private int appleX;
    private int appleY;
    private Array<BodyPart> bodyParts = new Array<>();
    private SpriteBatch batch;
    private Texture snakeHead;
    private Texture snakeBody;
    private Texture apple;

    @Override
    public void show() {
        batch = new SpriteBatch();
        snakeHead = new Texture(Gdx.files.internal("snakeHead.jpg"));
        snakeBody = new Texture(Gdx.files.internal("snakeBody.jpg"));
        apple = new Texture(Gdx.files.internal("apple.jpg"));
    }

    @Override
    public void render(float delta) {
        queryInput();
        timer -= delta;
        if (timer <= 0) {
            timer = MOVE_TIME;
            moveSnake();
            checkForOutOfBounds();
            updateBodyPartsPosition();
        }
        checkAndPlaceApple();
        checkAppleCollision();
        ScreenUtils.clear(Color.WHITE);

        draw();
    }

    private void checkAppleCollision() {
        if (isAppleAvailable && appleY == snakeY && appleX == snakeX) {
            isAppleAvailable = false;
            BodyPart part = new BodyPart(snakeBody);
            part.updateBodyPosition(snakeX, snakeY);
            bodyParts.insert(0, part);
        }
    }

    private void draw() {
        batch.begin();
        batch.draw(snakeHead, snakeX, snakeY);
        for (BodyPart bodyPart : bodyParts) {
            bodyPart.draw(batch);
        }
        if (isAppleAvailable) {
            batch.draw(apple, appleX, appleY);
        }
        batch.end();
    }

    private void checkAndPlaceApple() {
        if (!isAppleAvailable) {
            do {
                appleX = MathUtils.random(Gdx.graphics.getWidth() / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT;
                appleY = MathUtils.random(Gdx.graphics.getHeight() / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT;
                isAppleAvailable = true;
            }
            while (appleX == snakeX && appleY == snakeY);
        }
    }

    private void moveSnake() {
        snakeXBeforeUpdate = snakeX;
        snakeYBeforeUpdate = snakeY;
        switch (direction) {
            case UP:
                snakeY += SNAKE_MOVEMENT;
                break;
            case DOWN:
                snakeY -= SNAKE_MOVEMENT;
                break;
            case LEFT:
                snakeX -= SNAKE_MOVEMENT;
                break;
            case RIGHT:
                snakeX += SNAKE_MOVEMENT;
                break;
        }
    }

    private void updateBodyPartsPosition(){
        if(bodyParts.size>0){
            BodyPart body = bodyParts.removeIndex(0);
            body.updateBodyPosition(snakeXBeforeUpdate, snakeYBeforeUpdate);
            bodyParts.add(body);
        }
    }

    private void checkForOutOfBounds() {
        if (snakeX >= Gdx.graphics.getWidth()) {
            snakeX = 0;
        }
        if (snakeX < 0) {
            snakeX = Gdx.graphics.getWidth() - SNAKE_MOVEMENT;
        }
        if (snakeY >= Gdx.graphics.getHeight()) {
            snakeY = 0;
        }
        if (snakeY < 0) {
            snakeY = Gdx.graphics.getHeight() - SNAKE_MOVEMENT;
        }
    }

    private void queryInput() {
        boolean lPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean uPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        if (lPressed) direction = MovingDirection.LEFT;
        if (rPressed) direction = MovingDirection.RIGHT;
        if (uPressed) direction = MovingDirection.UP;
        if (dPressed) direction = MovingDirection.DOWN;
    }

    private class BodyPart {
        private int x;
        private int y;
        private Texture texture;

        public BodyPart(Texture texture) {
            this.texture = texture;
        }

        public void updateBodyPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void draw(Batch batch) {
            if (!(x == snakeX && y == snakeY)) {
                batch.draw(texture, x, y);
            }
        }
    }

}

enum MovingDirection {
    LEFT, RIGHT, UP, DOWN;
}