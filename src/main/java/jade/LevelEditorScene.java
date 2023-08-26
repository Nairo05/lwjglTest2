package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Rigidbody;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import imgui.ImDouble;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;
import util.Assets;

public class LevelEditorScene extends Scene {


    private GameObject obj1;

    public LevelEditorScene() { }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());

        if (loadedLevel) {
            this.activeGameObject = gameObjects.get(0);
            return;
        }

        obj1 = new GameObject("obj1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)), 1);
        SpriteRenderer spriteRenderer1 = new SpriteRenderer();
        Sprite sprite = new Sprite();
        sprite.setTexture(AssetPool.getTexture(Assets.testImage1));
        spriteRenderer1.setSprite(sprite);
        obj1.addComponent(spriteRenderer1);
        obj1.addComponent(new Rigidbody());
        this.addGameObjectToScene(obj1);

        this.activeGameObject = obj1;

    }

    private void loadResources() {
        AssetPool.getShader(Assets.defaultShader);

        AssetPool.getTexture(Assets.testImage1);
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

    @Override
    public void imGui() {
        ImGui.begin("Test Window");
        ImGui.text("test");
        ImGui.end();
    }
}
