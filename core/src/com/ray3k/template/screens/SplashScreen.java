package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.ray3k.template.*;

import static com.ray3k.template.Core.*;

public class SplashScreen extends JamScreen {
    private Stage stage;
    private final static Color BG_COLOR = new Color(Color.WHITE);
    private SpineDrawable spineDrawable;
    
    @Override
    public void show() {
        super.show();
        
        stage = new Stage(new ExtendViewport(800, 800), batch);
        Gdx.input.setInputProcessor(stage);
    
        var skeleton = new Skeleton(assetManager.get("spine/logo.json", SkeletonData.class));
        var animationState = new AnimationState(assetManager.get("spine/logo.json-animation"));
        spineDrawable = new SpineDrawable(skeletonRenderer, skeleton, animationState);
        spineDrawable.getAnimationState().setAnimation(0, "animation", true);
        var image = new Image(spineDrawable);
        image.setScaling(Scaling.none);
        image.setFillParent(true);
        stage.addActor(image);
    
        var root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        var label = new TypingLabel("CLICK TO BEGIN THE WINDOZE EXPERIENCE", skin);
        root.add(label).expand().bottom().padBottom(30);
        
        stage.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(null);
                core.transition(new GameScreen());
                assetManager.get("sfx/startup.mp3", Sound.class).play();
            }
        });
    
        stage.addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(null);
                core.transition(new GameScreen());
                assetManager.get("sfx/startup.mp3", Sound.class).play();
            }
        });
    }
    
    @Override
    public void act(float delta) {
        stage.act(delta);
        spineDrawable.update(delta);
    }
    
    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
