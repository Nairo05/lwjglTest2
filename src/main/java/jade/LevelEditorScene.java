package jade;

import components.SpriteRenderer;
import components.SpriteSheet;
import imgui.ImDouble;
import imgui.ImGui;
import org.joml.Vector2f;
import util.AssetPool;
import util.Assets;

public class LevelEditorScene extends Scene {


    private GameObject obj1;

    public LevelEditorScene() { }

    @Override
    public void init() {
        loadResources();

        SpriteSheet sprites = AssetPool.getSpriteSheet(Assets.spriteSheet);

        this.camera = new Camera(new Vector2f());

        obj1 = new GameObject("obj1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)), 1);
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("obj2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), -2);
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(15)));
        this.addGameObjectToScene(obj2);

        this.activeGameObject = obj1;

    }

    private void loadResources() {
        AssetPool.getShader(Assets.defaultShader);

        AssetPool.addSpriteSheet(Assets.spriteSheet,
                new SpriteSheet(AssetPool.getTexture(Assets.spriteSheet), 16,16,26,0));
    }

    @Override
    public void update(float dt) {;

        obj1.transform.position.x += 20 * dt;

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
