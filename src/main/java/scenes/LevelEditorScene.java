package scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.Camera;
import jade.GameObject;
import jade.Prefabs;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.AssetPool;
import util.Assets;

public class LevelEditorScene extends Scene {

    private GameObject obj1;

    SpriteSheet sprites;

    GameObject levelEditorStuff = new GameObject("levelEditor", new Transform(new Vector2f()), 0);

    public LevelEditorScene() { }

    @Override
    public void init() {
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());

        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpriteSheet(Assets.blocksSheet);

        if (loadedLevel) {
            if (!gameObjects.isEmpty()) {
                this.activeGameObject = gameObjects.get(0);
            }
            return;
        }
    }

    private void loadResources() {
        AssetPool.getShader(Assets.defaultShader);
        AssetPool.addSpriteSheet(Assets.blocksSheet, new SpriteSheet(AssetPool.getTexture(Assets.blocksSheet), 16, 16, 81, 0));
        AssetPool.getTexture(Assets.testImage1);
        AssetPool.addSpriteSheet(Assets.spriteSheet,
                new SpriteSheet(AssetPool.getTexture(Assets.spriteSheet), 16,16,26,0));

        for (GameObject g : gameObjects) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }

    @Override
    public void update(float dt) {

        DebugDraw.addCircle(new Vector2f(200,200), 64, new Vector3f(0,1,0), 1);

        levelEditorStuff.update(dt);

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imGui() {
        ImGui.begin("Test Window");

        ImVec2 windowsPos = new ImVec2();
        ImGui.getWindowPos(windowsPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowsPos.x + windowSize.x;

        for (int i = 0; i < sprites.getSize(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
                //Attach this to mouse Cursor
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if (i + 1 < sprites.getSize() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }

        }


        ImGui.end();
    }
}
