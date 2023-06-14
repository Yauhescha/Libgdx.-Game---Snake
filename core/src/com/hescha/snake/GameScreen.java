package com.hescha.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {
    private static final float MOVE_TIME = 0.1F;
    private static final int SNAKE_MOVEMENT = 32;
    private static final int GRID_CELL = SNAKE_MOVEMENT;
    public static final String GAME_OVER = "Game over.. Tap space to restart!";

    private SpriteBatch batch;
    private Texture snakeHead;
    private Texture snakeBody;
    private Texture apple;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;

    private MovingDirection direction = MovingDirection.RIGHT;
    private State state = State.PLAYING;
    private float timer = MOVE_TIME;
    private int snakeX = 0;
    private int snakeY = 0;
    private int snakeXBeforeUpdate = 0;
    private int snakeYBeforeUpdate = 0;
    private boolean isAppleAvailable;
    private boolean isDirectionSet;
    private boolean hasHit;
    private int appleX;
    private int appleY;
    private Array<BodyPart> bodyParts = new Array<>();


    @Override
    public void show() {
        batch = new SpriteBatch();
        snakeHead = new Texture(Gdx.files.internal("snakeHead.jpg"));
        snakeBody = new Texture(Gdx.files.internal("snakeBody.jpg"));
        apple = new Texture(Gdx.files.internal("apple.jpg"));
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        switch (state) {
            case PLAYING:
                queryInput();
                updateSnake(delta);
                checkAndPlaceApple();
                checkAppleCollision();
                break;
            case GAME_OVER:
                checkForRestart();
                break;
        }
        ScreenUtils.clear(Color.BLACK);
//        drawGrid();
        draw();
    }

    private void updateSnake(float delta) {
        timer -= delta;
        if (timer <= 0) {
            timer = MOVE_TIME;
            moveSnake();
            checkForOutOfBounds();
            updateBodyPartsPosition();
            checkSnakeBodyCollision();
            isDirectionSet = false;
        }
    }

    private void checkForRestart() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            doRestart();
        }
    }

    private void doRestart() {
        state = State.PLAYING;
        bodyParts.clear();
        direction = MovingDirection.RIGHT;
        isDirectionSet = false;
        timer = MOVE_TIME;
        snakeX = 0;
        snakeY = 0;
        snakeXBeforeUpdate = 0;
        snakeYBeforeUpdate = 0;
        isAppleAvailable = false;
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
        if (state == State.GAME_OVER) {
            glyphLayout.setText(bitmapFont, GAME_OVER);
            bitmapFont.draw(batch, glyphLayout, (Gdx.graphics.getWidth() - glyphLayout.width) / 2, Gdx.graphics.getHeight() / 2);
        }
        glyphLayout.setText(bitmapFont, "Scores: " + bodyParts.size);
        bitmapFont.draw(batch, glyphLayout, (Gdx.graphics.getWidth() - glyphLayout.width) / 2, Gdx.graphics.getHeight() / 2 - 100);
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

    private void updateBodyPartsPosition() {
        if (bodyParts.size > 0) {
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

        if (lPressed) updateDirection(MovingDirection.LEFT);
        if (rPressed) updateDirection(MovingDirection.RIGHT);
        if (uPressed) updateDirection(MovingDirection.UP);
        if (dPressed) updateDirection(MovingDirection.DOWN);
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

    private void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int x = 0; x < Gdx.graphics.getWidth(); x += GRID_CELL) {
            for (int y = 0; y < Gdx.graphics.getHeight(); y += GRID_CELL) {
                shapeRenderer.rect(x, y, GRID_CELL, GRID_CELL);
            }
        }
        shapeRenderer.end();
    }

    private void updateDirection(MovingDirection newSnakeDirection) {
        if (!isDirectionSet && direction != newSnakeDirection) {
            isDirectionSet = true;
            switch (newSnakeDirection) {
                case UP:
                    updateIfNotOppositeDirection(newSnakeDirection, MovingDirection.DOWN);
                    break;
                case DOWN:
                    updateIfNotOppositeDirection(newSnakeDirection, MovingDirection.UP);
                    break;
                case LEFT:
                    updateIfNotOppositeDirection(newSnakeDirection, MovingDirection.RIGHT);
                    break;
                case RIGHT:
                    updateIfNotOppositeDirection(newSnakeDirection, MovingDirection.LEFT);
                    break;
            }
        }
    }

    private void updateIfNotOppositeDirection(MovingDirection newSnakeDirection,
                                              MovingDirection oppositeDirection) {
        if (direction != oppositeDirection || bodyParts.size == 0) {
            direction = newSnakeDirection;
        }
    }

    private void checkSnakeBodyCollision() {
        for (BodyPart bodyPart : bodyParts) {
            if (bodyPart.x == snakeX && bodyPart.y == snakeY) {
                state = State.GAME_OVER;
                break;
            }
        }
    }
}

enum MovingDirection {
    LEFT, RIGHT, UP, DOWN;
}

enum State {
    PLAYING, GAME_OVER;
}