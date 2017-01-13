package io.faucette.transform_components;


import io.faucette.math.Vec2;
import io.faucette.math.Mat32;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.ComponentManager;


public class Transform2D extends Component {
    private Vec2 localPosition;
    private Vec2 position;

    private float localRotation;
    private float rotation;

    private Vec2 localScale;
    private Vec2 scale;

    private Mat32 localMatrix;
    private Mat32 matrix;

    private boolean needsUpdate;


    public Transform2D() {
        super();

        localPosition = new Vec2();
        position = new Vec2();

        localRotation = 0.0f;
        rotation = 0.0f;

        localScale = new Vec2(1f, 1f);
        scale = new Vec2(1f, 1f);

        localMatrix = new Mat32();
        matrix = new Mat32();

        needsUpdate = false;
    }

    @Override
    public Class<? extends ComponentManager> getComponentManagerClass() {
        return Transform2DManager.class;
    }
    @Override
    public ComponentManager createComponentManager() {
        return new Transform2DManager();
    }

    @Override
    public Component clear() {

        localPosition.set(0f, 0f);
        position.set(0f, 0f);

        localRotation = 0.0f;
        rotation = 0.0f;

        localScale.set(1f, 1f);
        scale.set(1f, 1f);

        localMatrix.identity();
        matrix.identity();

        needsUpdate = false;

        return this;
    }

    public boolean getNeedsUpdate() {
        return needsUpdate;
    }
    public Transform2D setNeedsUpdate(boolean force) {
        if (force || !needsUpdate) {
            Entity entity = getEntity();

            needsUpdate = true;

            if (entity != null) {
                Transform2D transform = null;

                for (Entity child: entity.getChildren()) {
                    if ((transform = child.getComponent(Transform2D.class)) != null) {
                        transform.setNeedsUpdate(force);
                    }
                }
            }
        }
        return this;
    }
    public Transform2D setNeedsUpdate() {
        return setNeedsUpdate(false);
    }

    public Transform2D setPosition(Vec2 position) {
        localPosition.copy(position);
        setNeedsUpdate();
        return this;
    }
    public Vec2 getPosition() {
        updateMatrix();
        return position;
    }
    public Vec2 getLocalPosition() {
        return localPosition;
    }

    public Transform2D setRotation(float rotation) {
        localRotation = rotation;
        setNeedsUpdate();
        return this;
    }
    public float getRotation() {
        updateMatrix();
        return rotation;
    }
    public float getLocalRotation() {
        return localRotation;
    }

    public Transform2D setScale(Vec2 scale) {
        localScale.copy(scale);
        setNeedsUpdate();
        return this;
    }
    public Vec2 getScale() {
        updateMatrix();
        return scale;
    }
    public Vec2 getLocalScale() {
        return localScale;
    }

    public Mat32 getMatrix() {
        updateMatrix();
        return matrix;
    }
    public Mat32 getLocalMatrix() {
        updateMatrix();
        return localMatrix;
    }

    public Transform2D translate(Vec2 v) {
        localPosition.add(v);
        setNeedsUpdate();
        return this;
    }
    public Transform2D rotate(float rotation) {
        localRotation += rotation;
        setNeedsUpdate();
        return this;
    }
    public Transform2D rotate(double rotation) {
        localRotation += (float) rotation;
        setNeedsUpdate();
        return this;
    }

    public Transform2D lookAt(Vec2 target) {
        Mat32 mat = new Mat32();
        mat.lookAt(this.getPosition(), target);
        localRotation = mat.getRotation();
        setNeedsUpdate();
        return this;
    }

    public Mat32 getModelView(Mat32 out, Mat32 viewMatrix) {
        return Mat32.mul(out, viewMatrix, this.getMatrix());
    };

    public Transform2D updateMatrix() {
        if (needsUpdate) {
            boolean update = false;

            needsUpdate = false;

            localMatrix.compose(localPosition, localScale, localRotation);

            Entity entity = getEntity();
            Entity parent = entity != null ? entity.getParent() : null;
            if (parent != null) {
                Transform2D parentTransform = parent.getComponent(Transform2D.class);

                if (parentTransform != null) {
                    update = true;
                    Mat32.mul(matrix, parentTransform.getMatrix(), localMatrix);
                }
            }

            if (update) {
                rotation = Mat32.decompose(matrix, position, scale);
            } else {
                matrix.copy(localMatrix);
                position.copy(localPosition);
                scale.copy(localScale);
                rotation = localRotation;
            }
        }
        return this;
    }
}
