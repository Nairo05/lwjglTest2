package jade;

import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;
import util.Assets;

import javax.swing.*;

public class LevelEditorScene extends Scene {


    public LevelEditorScene() { }

    @Override
    public void init() {
        loadResources();

        SpriteSheet sprites = AssetPool.getSpriteSheet(Assets.spriteSheet);

        this.camera = new Camera(new Vector2f());

        GameObject obj1 = new GameObject("obj1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("obj2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(15)));
        this.addGameObjectToScene(obj2);

    }

    private void loadResources() {
        AssetPool.getShader(Assets.defaultShader);

        AssetPool.addSpriteSheet(Assets.spriteSheet,
                new SpriteSheet(AssetPool.getTexture(Assets.spriteSheet), 16,16,26,0));
    }

    @Override
    public void update(float dt) {

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
