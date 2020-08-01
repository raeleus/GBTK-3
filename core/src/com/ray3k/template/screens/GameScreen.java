package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.crashinvaders.vfx.effects.EarthquakeEffect;
import com.ray3k.template.Core.*;
import com.ray3k.template.*;
import com.ray3k.template.entities.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.ray3k.template.Core.*;

public class GameScreen extends JamScreen {
    public static GameScreen gameScreen;
    public static final Color BG_COLOR = new Color();
    public Stage stage;
    public ShapeDrawer shapeDrawer;
    public EntityController entityController;
    private EarthquakeEffect vfxEffect;
    public boolean paused;
    
    public GameScreen() {
        gameScreen = this;
        vfxEffect = new EarthquakeEffect();
        
        BG_COLOR.set(Color.PINK);
    
        paused = false;
        
        stage = new Stage(new ScreenViewport(), batch);
        
        skin = assetManager.get("skin/skin.json");
        shapeDrawer = new ShapeDrawer(batch, skin.getRegion("white"));
        shapeDrawer.setPixelSize(.5f);
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(1024, 576, camera);
        
        entityController = new EntityController();
        BallTestEntity ballTestEntity = new BallTestEntity();
        ballTestEntity.moveCamera = true;
        entityController.add(ballTestEntity);
        
        for (int i = 0; i < 10; i++) {
            ballTestEntity = new BallTestEntity();
            ballTestEntity.setPosition(MathUtils.random(viewport.getWorldWidth()), MathUtils.random(viewport.getWorldHeight()));
            entityController.add(ballTestEntity);
        }
        
        vfxManager.addEffect(vfxEffect);
    }
    
    @Override
    public void act(float delta) {
        if (!paused) {
            entityController.act(delta);
            vfxEffect.update(delta);
        }
        stage.act(delta);
    
        if (isBindingJustPressed(Binding.LEFT)) {
            System.out.println("left");
        }
        if (isBindingJustPressed(Binding.UP)) {
            System.out.println("up");
        }
    }
    
    @Override
    public void draw(float delta) {
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        vfxManager.cleanUpBuffers();
        vfxManager.beginCapture();
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shapeDrawer.setColor(isBindingPressed(Binding.LEFT) && isBindingPressed(Binding.UP) ? Color.ORANGE : Color.GREEN);
        shapeDrawer.filledRectangle(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        shapeDrawer.setColor(Color.BLUE);
        shapeDrawer.setDefaultLineWidth(10);
        shapeDrawer.rectangle(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        entityController.draw(paused ? 0 : delta);
        batch.end();
        vfxManager.endCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();
    
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        if (width + height != 0) {
            vfxManager.resize(width, height);
            viewport.update(width, height);
        
            stage.getViewport().update(width, height, true);
        }
    }
    
    @Override
    public void dispose() {
        vfxEffect.dispose();
    }
    
    @Override
    public void hide() {
        super.hide();
        vfxManager.removeAllEffects();
        vfxEffect.dispose();
    }
}
