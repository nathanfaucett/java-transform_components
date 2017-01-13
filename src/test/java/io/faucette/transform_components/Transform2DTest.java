package io.faucette.transform_components;


import io.faucette.math.Vec2;
import io.faucette.math.Mat32;
import io.faucette.scene_graph.Scene;
import io.faucette.scene_graph.Entity;

import static org.junit.Assert.*;
import org.junit.*;


public class Transform2DTest {
    @Test
    public void testTransform2D() {
        Scene scene = new Scene();
        Transform2D parentTransform2D = new Transform2D();
        Transform2D childTransform2D = new Transform2D();
        Entity parent = new Entity().addComponent(parentTransform2D);
        Entity child = new Entity().addComponent(childTransform2D);

        parent.addChild(child);
        scene.addEntity(parent);

        parentTransform2D.translate(new Vec2(1, 1));
        parentTransform2D.rotate(Math.PI * 0.5d);

        childTransform2D.translate(new Vec2(1, 1));
        childTransform2D.rotate(Math.PI * 0.5d);

        assertEquals(parentTransform2D.getRotation(), (float) (Math.PI * 0.5d), 0.0001f);
        assertEquals(childTransform2D.getRotation(), (float) (-Math.PI), 0.0001f);

        assertEquals(parentTransform2D.getPosition(), new Vec2(1f, 1f));
        assertEquals(childTransform2D.getPosition(), new Vec2(0f, 2f));
    }

    @Test
    public void testModelView() {
        Scene scene = new Scene();
        Transform2D cameraTransform2D = new Transform2D();
        Transform2D transform2D = new Transform2D();
        Entity camera = new Entity().addComponent(cameraTransform2D);
        Entity entity = new Entity().addComponent(transform2D);

        scene.addEntity(camera);
        scene.addEntity(entity);

        cameraTransform2D.translate(new Vec2(1, 1));
        transform2D.translate(new Vec2(1, 1));

        Mat32 modelView = new Mat32();
        transform2D.getModelView(modelView, cameraTransform2D.getMatrix().inverse());

        assertEquals(modelView, new Mat32(
            1f, 0f, 0f,
            0f, 1f, 0f
        ));
    }
}
