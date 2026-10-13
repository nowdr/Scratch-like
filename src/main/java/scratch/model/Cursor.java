package scratch.model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class Cursor {
    private final ObjectProperty<Point> position = new SimpleObjectProperty<>(new Point(Constants.INITIAL_X, Constants.INITIAL_Y));
    private final DoubleProperty angle = new SimpleDoubleProperty(Constants.INITIAL_ANGLE);
    private final BooleanProperty penDown = new SimpleBooleanProperty(true);
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.BLACK);

    public ObjectProperty<Point> positionProperty() {
        return position;
    }

    public DoubleProperty angleProperty() {
        return angle;
    }

    public void setPenDown(boolean penDown) {
        this.penDown.set(penDown);
    }

    public BooleanProperty penDownProperty() {
        return penDown;
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void moveForward(double distance) {
        double radians = Math.toRadians(angle.get());
        position.set(new Point(position.get().getX() + distance * Math.cos(radians),
                position.get().getY() + distance * Math.sin(radians)));
    }

    public void rotate(double degrees) {
        angle.set((angle.get() + degrees) % 360);

        if (angle.get() < 0) {
            angle.set(angle.get() + 360);
        }
    }

    public void reset() {
        position.set(new Point(Constants.INITIAL_X, Constants.INITIAL_Y));
        angle.set(Constants.INITIAL_ANGLE);
        penDown.set(true);
        color.set(Color.BLACK);
    }
}