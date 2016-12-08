
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.Duration;

// Java 8 code
public class Main extends Application {

    private static final int shadowSize = 50;

    @Override
    public void start(final Stage stage) {
        Image img = new Image("loading.png");
        img.getHeight();
        img.getWidth();

        ImageView imgView = new ImageView(img);
        final ScaleTransition scale
                = new ScaleTransition(new Duration(1000));
        scale.setNode(imgView);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(2.0);
        scale.setToY(0.75);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        stage.initStyle(StageStyle.TRANSPARENT);

        Timeline tl = new Timeline();
        tl.setCycleCount(Animation.INDEFINITE);
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded((WorkerStateEvent event) -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.close();
                    try {
                        new painter().start(new Stage());
                    } catch (Exception ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        });
        new Thread(sleeper).start();
        StackPane stackPane = new StackPane(createShadowPane());
        stackPane.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.5);"
                + "-fx-background-insets: " + shadowSize + ";"
        );
        stackPane.getChildren().add(imgView);

        Scene scene = new Scene(stackPane, 450, 450);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    // Create a shadow effect as a halo around the pane and not within
    // the pane's content area.
    private Pane createShadowPane() {
        Pane shadowPane = new Pane();
        // a "real" app would do this in a CSS stylesheet.
        shadowPane.setStyle(
                "-fx-background-color: white;"
                + "-fx-effect: dropshadow(gaussian, lightblue, " + shadowSize + ", 0, 0, 0);"
                + "-fx-background-insets: " + shadowSize + ";"
        );

        Rectangle innerRect = new Rectangle();
        Rectangle outerRect = new Rectangle();
        shadowPane.layoutBoundsProperty().addListener(
                (observable, oldBounds, newBounds) -> {
                    innerRect.relocate(
                            newBounds.getMinX() + shadowSize,
                            newBounds.getMinY() + shadowSize
                    );
                    innerRect.setWidth(newBounds.getWidth() - shadowSize * 2);
                    innerRect.setHeight(newBounds.getHeight() - shadowSize * 2);

                    outerRect.setWidth(newBounds.getWidth());
                    outerRect.setHeight(newBounds.getHeight());

                    Shape clip = Shape.subtract(outerRect, innerRect);
                    shadowPane.setClip(clip);
                }
        );

        return shadowPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
