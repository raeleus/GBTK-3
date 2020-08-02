package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.crashinvaders.vfx.effects.EarthquakeEffect;
import com.ray3k.template.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.ray3k.template.Core.*;

public class GameScreen extends JamScreen {
    public static GameScreen gameScreen;
    public static final Color BG_COLOR = Color.valueOf("3A6EA5");
    public Stage stage;
    public ShapeDrawer shapeDrawer;
    private EarthquakeEffect vfxEffect;
    private int mode;
    private Array<String> tasks;
    
    public GameScreen() {
        tasks = new Array<>();
        tasks.addAll("Explorer", "Systray", "Bpcpost", "CMD.COM", "REAL_PLAYER", "Macromedia Flash Updater", "spooler.exe", "svchost");
        gameScreen = this;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(800, 800, camera);
        vfxEffect = new EarthquakeEffect();
        stage = new Stage(viewport, batch);
        
        var root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        var table = new Table();
        table.pad(30);
        root.add(table).grow();
        
        table.defaults().expandX().left().space(30);
        var imageTextButton = new ImageTextButton("Play Sweeper", skin, "game");
        imageTextButton.row();
        imageTextButton.add(imageTextButton.getLabel());
        table.add(imageTextButton);
        imageTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                switch (mode) {
                    case 0:
                        mode++;
                        showDialog("ERROR System32", "Error System32: Not enough RAM...", Actions.run(() -> {
                            var imageTextButton = stage.getRoot().findActor("ram");
                            imageTextButton.addAction(repeat(3, sequence(delay(.2f), visible(false), delay(.2f), visible(true))));
                        }));
                        break;
                    case 1:
                        showDialog("ERROR System32", "Error System32: I told you already,\nNot enough RAM...");
                        break;
                }
            }
        });
        
        table.row();
        imageTextButton = new ImageTextButton("Task Manager", skin, "taskmanager");
        imageTextButton.row();
        imageTextButton.add(imageTextButton.getLabel());
        table.add(imageTextButton);
        imageTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showTaskManager();
            }
        });
    
        table.row();
        imageTextButton = new ImageTextButton("Download More\nRAM", skin, "explorer");
        imageTextButton.setName("ram");
        imageTextButton.setVisible(false);
        imageTextButton.row();
        imageTextButton.add(imageTextButton.getLabel());
        table.add(imageTextButton);
        imageTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showDialog("ERROR System32", "Error System32: Not enough RAM...");
            }
        });
        
        table.row();
        table.add().expand();
        
        root.row();
        table = new Table();
        table.setBackground(skin.getDrawable("taskbar-10"));
        root.add(table).growX();
        
        var imageButton = new ImageButton(skin, "start");
        table.add(imageButton).left().expandX();
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showDialog("Admin Priveleges Required", "This feature has been blocked by your administrator. Please consult your IT department.");
            }
        });
        
        skin = assetManager.get("skin/skin.json");
        shapeDrawer = new ShapeDrawer(batch, skin.getRegion("white"));
        shapeDrawer.setPixelSize(.5f);
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        vfxManager.addEffect(vfxEffect);
    }
    
    @Override
    public void act(float delta) {
        vfxEffect.update(delta);
        stage.act(delta);
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
    
    private void showDialog(String title, String text) {
        showDialog(title, text, null);
    }
    
    private void showDialog(String title, String text, Action action) {
        assetManager.get("sfx/error.mp3", Sound.class).play();
        var dialog = new Dialog(title, skin);
        
        var hideListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide(action);
            }
        };
        
        var imageButton = new ImageButton(skin, "help");
        dialog.getTitleTable().add(imageButton);
    
        imageButton = new ImageButton(skin, "close");
        dialog.getTitleTable().add(imageButton);
        imageButton.addListener(hideListener);
        
        dialog.text(text);
        
        var textButton = new TextButton("OK", skin);
        dialog.getButtonTable().add(textButton);
        textButton.addListener(hideListener);
        
        dialog.show(stage, null);
        dialog.setPosition(Math.round((stage.getWidth() - dialog.getWidth()) / 2), Math.round((stage.getHeight() - dialog.getHeight()) / 2));
    }
    
    private void showTaskManager() {
        assetManager.get("sfx/error.mp3", Sound.class).play();
        var dialog = new Dialog("Close Program", skin);
    
        var hideListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide(null);
            }
        };
        
        var list = new List<String>(skin);
        list.setItems(tasks);
        
        dialog.getContentTable().add(list).grow().minHeight(200);
    
        dialog.getContentTable().row();
        dialog.text("WARNING: Pressing CTRL+ALT+DEL has been\ndeactivated by your administrator.");
    
        var textButton = new TextButton("End Task", skin);
        dialog.getButtonTable().add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                tasks.removeValue(list.getSelected(), false);
                list.setItems(tasks);
            }
        });
    
        textButton = new TextButton("Shut Down", skin);
        dialog.getButtonTable().add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showDialog("No, no, no...", "Can't shut down yet, genius!\nYou haven't even got to the good part yet...");
            }
        });
    
        textButton = new TextButton("Cancel", skin);
        dialog.getButtonTable().add(textButton);
        textButton.addListener(hideListener);
    
        dialog.show(stage, null);
        dialog.setPosition(Math.round((stage.getWidth() - dialog.getWidth()) / 2), Math.round((stage.getHeight() - dialog.getHeight()) / 2));
    }
}
