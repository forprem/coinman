package com.saar.su.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;



public class Saar extends ApplicationAdapter {
	SpriteBatch batch;
	Texture bgImg;
	Texture[] su;
	int suState;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int suY = 0;
	Rectangle suRectangle;
	int score = 0;
	//Display score
	BitmapFont font;
	int gameState = 0;
	Texture dizzy;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();


	Texture coin;
	int coinCount;
	Random random;

	ArrayList<Integer> bomXs = new ArrayList<Integer>();
	ArrayList<Integer> bomYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bomRectangles = new ArrayList<Rectangle>();

	Texture bom;
	int bomCount;

	public void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBom() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bomXs.add((int)height);
		bomYs.add(Gdx.graphics.getWidth());
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		bgImg = new Texture("bg.png");
		su = new Texture[4];

		su[0] = new Texture("frame-1.png");
		su[1] = new Texture("frame-2.png");
		su[2] = new Texture("frame-3.png");
		su[3] = new Texture("frame-4.png");
		suY = Gdx.graphics.getHeight() / 2 ;

		coin = new Texture("coin.png");
		bom = new Texture("bomb.png");
		dizzy = new Texture("dizzy-1.png");
		random = new Random();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(bgImg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState ==1) {
			// Game is LIVE
			// Bomb

			if (bomCount < 250){
				bomCount ++;
			}else {
				bomCount = 0;
				makeBom();
			}
			bomRectangles.clear();
			for (int i =0; i< bomXs.size() ; i++ ) {
				batch.draw(bom, bomXs.get(i), bomYs.get(i));
				bomXs.set(i, bomXs.get(i)-8);
				bomRectangles.add(new Rectangle(bomXs.get(i), bomYs.get(i), bom.getWidth(), bom.getHeight()));
			}

			// Coin
			if (coinCount < 100){
				coinCount ++;
			}else {
				coinCount = 0;
				makeCoin();
			}
			coinRectangles.clear();
			for (int i =0; i<coinXs.size() ; i++ ) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i)-4);
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			if (Gdx.input.justTouched()) {
				velocity = -10;
			}

			if (pause < 8) {
				pause ++;

			} else {
				pause =0;
				if (suState < 3) {
					suState ++;
				} else {
					suState = 0;
				}
			}

			velocity += gravity;
			suY -= velocity;

			if (suY <= 0) {
				suY = 0;
			}




		} else if(gameState==0) {
			//Waiting to Start
			if (Gdx.input.justTouched()) {
				gameState=1;
			}

		} else if(gameState==2) {
			// Game Over
			if (Gdx.input.justTouched()) {
				gameState=1;
				suY = Gdx.graphics.getHeight() / 2 ;
				score = 0;
				velocity =0.2f;
				coinYs.clear();
				coinXs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bomYs.clear();
				bomXs.clear();
				bomRectangles.clear();
				bomCount = 0;
			}
		}

		if (gameState ==2 ){
			batch.draw(dizzy, Gdx.graphics.getWidth()/2 - su[suState].getWidth()/2, suY);
		} else {
			batch.draw(su[suState], Gdx.graphics.getWidth()/2 - su[suState].getWidth()/2, suY);
		}


		suRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - su[suState].getWidth()/2, suY, su[suState].getHeight(), su[suState].getHeight());
		//Coin COllission
		for (int i =0; i< coinRectangles.size(); i++) {
			if(Intersector.overlaps(suRectangle, coinRectangles.get(i))) {
				//Gdx.app.log("Coin", "Collision");
				score++;

				//remove overriden coins
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		//Bomb COllission
		for (int i =0; i< bomRectangles.size(); i++) {
			if(Intersector.overlaps(suRectangle, bomRectangles.get(i))) {
				//Gdx.app.log("Bomb", "Collision");
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score), 100,200);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		bgImg.dispose();
	}
}
