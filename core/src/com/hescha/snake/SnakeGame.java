package com.hescha.snake;

import com.badlogic.gdx.Game;

public class SnakeGame extends Game {
//	SpriteBatch batch;
//	Texture img;
	
	@Override
	public void create () {
//		batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
		setScreen(new GameScreen());
	}

//	@Override
//	public void render () {
//		ScreenUtils.clear(1, 0, 0, 1);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
//	}
	
//	@Override
//	public void dispose () {
//		batch.dispose();
//		img.dispose();
//	}
}
