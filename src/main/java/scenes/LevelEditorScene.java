package scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.Camera;
import jade.GameObject;
import jade.Prefabs;
import jade.Transform;
import org.joml.Vector2f;
import util.AssetPool;
import util.Assets;

public class LevelEditorScene extends Scene {

    private GameObject obj1;

    SpriteSheet sprites;

    MouseControls mouseControls = new MouseControls();

    public LevelEditorScene() { }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpriteSheet(Assets.blocksSheet);

        if (loadedLevel) {
            this.activeGameObject = gameObjects.get(0);
            return;
        }
    }

    private void loadResources() {
        AssetPool.getShader(Assets.defaultShader);
        AssetPool.addSpriteSheet(Assets.blocksSheet, new SpriteSheet(AssetPool.getTexture(Assets.blocksSheet), 16, 16, 81, 0));
        AssetPool.getTexture(Assets.testImage1);
        AssetPool.addSpriteSheet(Assets.spriteSheet,
                new SpriteSheet(AssetPool.getTexture(Assets.spriteSheet), 16,16,26,0));
    }

    @Override
    public void update(float dt) {

        mouseControls.update(dt);

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
            float spriteWidth = sprite.getWidth();
            float spriteHeight = sprite.getHeight();
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
                GameObject object = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                //Attach this to mouse Cursor
                mouseControls.pickupObject(object);
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
