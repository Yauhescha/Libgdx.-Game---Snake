package com.hescha.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;
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
    private Viewport viewport;
    private Camera camera;

    private Snake snake;
    private boolean isDirectionSet;

    private MovingDirection direction = MovingDirection.RIGHT;
    private State state = State.PLAYING;
    private float timer = MOVE_TIME;
    private boolean isAppleAvailable;
    private int appleX;
    private int appleY;
    private Array<BodyPart> bodyParts = new Array<>();


    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        batch = new SpriteBatch();
        snakeHead = new Texture(Gdx.files.internal("snakeHead.jpg"));
        snakeBody = new Texture(Gdx.files.internal("snakeBody.jpg"));
        apple = new Texture(Gdx.files.internal("apple.jpg"));
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();

        snake = new Snake();
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
        drawGrid();
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
        timer = MOVE_TIME;
        snake = new Snake();
        isAppleAvailable = false;
    }

    private void checkAppleCollision() {
        if (isAppleAvailable && appleY == snake.getSnakeY() && appleX == snake.getSnakeX()) {
            isAppleAvailable = false;
            BodyPart part = new BodyPart(snakeBody, snake);
            part.updateBodyPosition(snake.getSnakeX(), snake.getSnakeY());
            bodyParts.insert(0, part);
        }
    }

    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.draw(snakeHead, snake.getSnakeX(), snake.getSnakeY());
        for (BodyPart bodyPart : bodyParts) {
            bodyPart.draw(batch);
        }
        if (isAppleAvailable) {
            batch.draw(apple, appleX, appleY);
        }
        if (state == State.GAME_OVER) {
            glyphLayout.setText(bitmapFont, GAME_OVER);
            bitmapFont.draw(batch, glyphLayout, (viewport.getWorldWidth() - glyphLayout.width) / 2, viewport.getWorldHeight() / 2);
        }
        glyphLayout.setText(bitmapFont, "Scores: " + bodyParts.size);
        bitmapFont.draw(batch, glyphLayout, (viewport.getWorldWidth() - glyphLayout.width) / 2, viewport.getWorldHeight() / 2 - 100);
        batch.end();
    }

    private void checkAndPlaceApple() {
        if (!isAppleAvailable) {
            do {
                int xRandom = (int) MathUtils.random(viewport.getWorldWidth() / SNAKE_MOVEMENT - 1);
                int yRandom = (int) MathUtils.random(viewport.getWorldHeight() / SNAKE_MOVEMENT - 1);
                appleX = xRandom * SNAKE_MOVEMENT;
                appleY = yRandom * SNAKE_MOVEMENT;
                isAppleAvailable = true;
            }
            while (appleX == snake.getSnakeX() && appleY == snake.getSnakeY());
        }
    }

    private void moveSnake() {
        snake.setSnakeXBeforeUpdate(snake.getSnakeX());
        snake.setSnakeYBeforeUpdate(snake.getSnakeY());
        switch (direction) {
            case UP:
                snake.setSnakeY(snake.getSnakeY() + SNAKE_MOVEMENT);
                break;
            case DOWN:
                snake.setSnakeY(snake.getSnakeY() - SNAKE_MOVEMENT);
                break;
            case LEFT:
                snake.setSnakeX(snake.getSnakeX() - SNAKE_MOVEMENT);
                break;
            case RIGHT:
                snake.setSnakeX(snake.getSnakeX() + SNAKE_MOVEMENT);
                break;
        }
    }

    private void updateBodyPartsPosition() {
        if (bodyParts.size > 0) {
            BodyPart body = bodyParts.removeIndex(0);
            body.updateBodyPosition(snake.getSnakeXBeforeUpdate(), snake.getSnakeYBeforeUpdate());
            bodyParts.add(body);
        }
    }

    private void checkForOutOfBounds() {
        if (snake.getSnakeX() >= viewport.getWorldWidth()) {
            snake.setSnakeX(0);
        }
        if (snake.getSnakeX() < 0) {
            snake.setSnakeX((int) (viewport.getWorldWidth() - SNAKE_MOVEMENT));
        }
        if (snake.getSnakeY() >= viewport.getWorldHeight()) {
            snake.setSnakeY(0);
        }
        if (snake.getSnakeY() < 0) {
            snake.setSnakeY((int) (viewport.getWorldHeight() - SNAKE_MOVEMENT));
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

    private void drawGrid() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int x = 0; x < viewport.getWorldWidth(); x += GRID_CELL) {
            for (int y = 0; y < viewport.getWorldHeight(); y += GRID_CELL) {
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
            if (bodyPart.getX() == snake.getSnakeX() && bodyPart.getY() == snake.getSnakeY()) {
                state = State.GAME_OVER;
                break;
            }
        }
    }
}