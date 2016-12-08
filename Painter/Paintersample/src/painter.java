
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javafx.scene.Group;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Separator;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.image.ImageView;

public class painter extends Application {

    Canvas blurryCanvas;
    Slider slider_Opacity;
    GraphicsContext gc;
    Text text;
    private int tabNum = 0;
    private static final Random random = new Random(42);
    Group group_bottom;
    //TODO redo and undo
    private static double SCENE_WIDTH = 1280;
    private static double SCENE_HEIGHT = 720;
    private static double CANVAS_WIDTH = 840;
    private static double CANVAS_HEIGHT = 690;
    ImageView iv_paper1;
    CheckBox papercanvas_checkbox;
    Image image1;
    GraphicsContext gcpaper;
    Canvas papercanvas;
    Label label_system;
    boolean piccheck;

    public boolean isPiccheck() {
        return piccheck;
    }

    public void setPiccheck(boolean piccheck) {
        this.piccheck = piccheck;
    }

    Color save_Color;
    //
    Menu menuFile;
    Menu menuEffect;
    MenuItem menuLoad;
    MenuItem menuSave;
    //
    StackPane layerContainer;

    BorderPane root;
    ChoiceBox cb;
    VBox vb;
    VBox vBox;
    VBox vb_toolbar;
    VBox vb_layerbar;
    HBox hb_filebar;
    MenuBar menubar;
    HBox hb;
    CheckBox eraser;
    ListView lv;
    Canvas canvas;
    CheckBox Cor_system_checkBox;
    GraphicsContext graphicsContext;
    GraphicsContext graphicsContext1;
    GraphicsContext def_Content;
    Canvas canvasBackgroud;

    public GraphicsContext getDef_Content() {
        return def_Content;
    }

    public void setDef_Content(GraphicsContext def_Content) {
        this.def_Content = def_Content;
    }

    AnimationTimer loop;

    Point2D mouseLocation = new Point2D(0, 0);
    boolean mousePressed = false;
    Point2D prevMouseLocation = new Point2D(0, 0);

    Scene scene;
    Label label;
    Label current_label;
    Button clearLayer_button;
    Button remove_thisButton;
    Button addlayerButton;
    Button showallButton;
    Button showorderButton;
    //field for brush
    double brushMaxSize = 10;
    double pressure = 0;
    double pressureDelay = 0.03;
    double pressureDirection = 1;
    double strokeTimeMax = 60;
    double strokeTime = 0;
    double strokeTimeDelay = 0.01;
    private Image[] brushVariations = new Image[256];
    ColorPicker colorPicker = new ColorPicker();
    Image image;
    Image image_paper;

    Color saved_color;

    public Color getSaved_color() {
        return saved_color;
    }

    public void setSaved_color(Color saved_color) {
        this.saved_color = saved_color;
    }

    //Effects
    public ComboBox cbEffect;

    int layernumber = 2;

    //Main function
//    public static void main(String[] args) {
//        launch(args);
//    }
    private Canvas canvas1;

    private void saveColor(Color value) {
        this.save_Color = value;
    }

    private Color getColor() {
        return this.save_Color;
    }

    //Start of getter and setter
    public double getbrushMaxSize() {
        return this.brushMaxSize;
    }

    public void setbrushMaxSize(Number value) {
        this.brushMaxSize = (double) value;
    }

    public int getLayernumber() {
        return layernumber;
    }

    public void setLayernumber(int layernumber) {
        this.layernumber = layernumber;
    }

    public ColorPicker getcolorPicker() {
        return this.colorPicker;
    }
    //End of getter and setter

    Canvas def_layer = canvas;

    public Canvas getDef() {
        return def_layer;
    }

    public void setDef(Canvas def) {
        this.def_layer = def;
        def_layer.toFront();
    }

    Canvas canvas_current = this.canvasBackgroud;

    public Canvas getCanvas_current() {
        return canvas_current;
    }

    public void setCanvas_current(Canvas canvas_current) {
        this.canvas_current = canvas_current;
    }

    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        create_left_Bar();
        create_right_Bar();
        create_top_Bar();

        //createDynamicTab();
        //setting the layer out of the program
        createLayout();

        createCor_system();

        System.out.println("painter.start()");

//        createbackground();
        primaryStage.setTitle("Quick Brush");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.getIcons().add(new Image("logo.png"));
        validBrush();

    }

    //Start of Brush code
    //valid the brush code in main function
    public void validBrush() {

        createBrushVariations();

        addBrushListeners();

        startAnimation();
    }

    public void createBrushVariations() {

        for (int i = 0; i < brushVariations.length; i++) {

            double size = (this.getbrushMaxSize() - 1) / (double) brushVariations.length * (double) i + 1;

            brushVariations[i] = createBrush(size, colorPicker.getValue());
        }
    }

    public void startAnimation() {
        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (mousePressed) {
                    bresenhamLine(prevMouseLocation.getX(), prevMouseLocation.getY(), mouseLocation.getX(), mouseLocation.getY());
                    // increasing or decreasing
                    strokeTime += strokeTimeDelay * pressureDirection;
                    // invert direction
                    if (strokeTime > strokeTimeMax) {
                        pressureDirection = -1;
                    }
                    // while still
                    if (strokeTime > 0) {
                        pressure += pressureDelay * pressureDirection;
                        // clamp value of pressure to be [0,1]
                        if (pressure > 1) {
                            pressure = 1;
                        } else if (pressure < 0) {
                            pressure = 0;
                        }
                    } else {
                        pressure = 0;
                    }
                } else {
                    pressure = 0;
                    pressureDirection = 1;
                    strokeTime = 0;
                }
                prevMouseLocation = new Point2D(mouseLocation.getX(), mouseLocation.getY());
            }
        };
        loop.start();
    }

    public void bresenhamLine(double x0, double y0, double x1, double y1) {
        double dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1. : -1.;
        double dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1. : -1.;
        double err = dx + dy, e2;
        /* error value e_xy */
        while (true) {
            int variation = (int) (pressure * (brushVariations.length - 1));
            Image brushVariation = brushVariations[variation];
            getDef_Content().setGlobalAlpha(pressure);
            getDef_Content().drawImage(brushVariation, x0 - brushVariation.getWidth() / 2.0, y0 - brushVariation.getHeight() / 2.0);
            if (x0 == x1 && y0 == y1) {
                break;
            }
            e2 = 2. * err;
            if (e2 > dy) {
                err += dy;
                x0 += sx;
            }
            /* e_xy+e_x > 0 */

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
            /* e_xy+e_y < 0 */
        }
    }

    public void addBrushListeners() {
        getDef().addEventFilter(MouseEvent.ANY, e -> {
            mouseLocation = new Point2D(e.getX(), e.getY());
            mousePressed = e.isPrimaryButtonDown();
        });
    }

    public static Image createImage(Node node) {
        WritableImage wi;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        int imageWidth = (int) node.getBoundsInLocal().getWidth();
        int imageHeight = (int) node.getBoundsInLocal().getHeight();
        wi = new WritableImage(imageWidth, imageHeight);
        node.snapshot(parameters, wi);
        return wi;
    }

    public static Image createBrush(double radius, Color color) {
        // create gradient image with given color
        Circle brush = new Circle(radius);
        RadialGradient gradient1 = new RadialGradient(0, 0, 0, 0, radius, false, CycleMethod.NO_CYCLE, new Stop(0, color.deriveColor(1, 1, 1, 0.3)), new Stop(1, color.deriveColor(1, 1, 1, 0)));
        brush.setFill(gradient1);
        // create image
        return createImage(brush);
    }
    //End of Brush code

    //Start of Tab Danamic 
    private Button createTabControls(TabPane tabPane) {
        Button addTab = new Button("New Tab");
        addTab.setOnAction(event -> {
            tabPane.getTabs().add(
                    createTab()
            );
            tabPane.getSelectionModel().selectLast();
        });
        addTab.setMinSize(
                addTab.USE_PREF_SIZE,
                addTab.USE_PREF_SIZE
        );
        return addTab;
    }

    private Tab createTab() {
        tabNum++;
        Tab tab = new Tab("Tab: " + tabNum);

        StackPane tabLayout = new StackPane();
        tabLayout.setStyle("-fx-background-color: " + randomRgbColorString());
        Label tabText = new Label("" + tabNum);
        tabText.setStyle("-fx-font-size: 40px;");
        tabLayout.getChildren().add(tabText);

        tab.setContent(tabLayout);

        return tab;
    }

    private String randomRgbColorString() {
        return "rgb("
                + random.nextInt(255) + ", "
                + random.nextInt(255) + ", "
                + random.nextInt(255)
                + ");";
    }
    //End of Tab Danamic 

    //Creating Dynamictab
    private void createDynamicTab() {
        TabPane tabPane = new TabPane();
        tabPane.setPrefSize(200, 150);
        VBox layout = new VBox(10,
                createTabControls(tabPane),
                tabPane
        );
    }

    //Formating the layerView
    private void create_right_Bar() {
        current_label = new Label("The current working layer");
        lv = new ListView();
        lv.getSelectionModel().select(1);
        ObservableList<String> items = FXCollections.observableArrayList(
                "Layer 1", "Layer 2");
        showallButton = new Button("Show All the layer");
        showallButton.setOnAction((ActionEvent e) -> {
            canvas.setVisible(true);
            canvas1.setVisible(true);
        });
        showorderButton = new Button("Show the order");
        showorderButton.setOnAction((ActionEvent e) -> {
            this.canvas.setVisible(true);
            this.canvas1.setVisible(true);
            this.canvasBackgroud.setVisible(true);
        });
        clearLayer_button = new Button("Clear Layer");
        clearLayer_button.setOnAction((ActionEvent e) -> {
            this.getDef_Content().clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        });
        remove_thisButton = new Button("remove_this");
        remove_thisButton.setOnAction(event -> {
            final int selectedIdx = lv.getSelectionModel().getSelectedIndex();
            if (selectedIdx != -1) {
                String itemToRemove = (String) lv.getSelectionModel().getSelectedItem();

                final int newSelectedIdx
                        = (selectedIdx == lv.getItems().size() - 1)
                                ? selectedIdx - 1
                                : selectedIdx;

                lv.getItems().remove(selectedIdx);
                System.out.println("Removed " + itemToRemove);
                lv.getSelectionModel().select(newSelectedIdx);
                //remove the canvas color or stackpane
            }
        });
        Cor_system_checkBox = new CheckBox();
        Cor_system_checkBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            if (new_val == true) {
                this.blurryCanvas.setVisible(true);
                label_system.setText("Disenble Cor-System");
            } else {
                this.blurryCanvas.setVisible(false);
                label_system.setText("Enble Cor-System");
            }
        });
        papercanvas_checkbox = new CheckBox();
        papercanvas_checkbox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            if (new_val == true) {
                System.out.println("painter.create_right_Bar()");
            } else {
                System.out.println("painter.create_right_Bar()");
            }
        });
        addlayerButton = new Button("Add a layer with Color");
        addlayerButton.setOnAction(event -> {
            int i = getLayernumber();
            i++;
            setLayernumber(i);
            items.add("Layer " + i);
            System.out.println("Just add one layer");
            layerContainer.getChildren().add(
                    createCanvas()
            );
            System.out.println("painter.create_right_Bar()");
            lv.getSelectionModel().selectLast();
        });

        lv.setItems(items);
        lv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        lv.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            String x = (String) lv.getSelectionModel().getSelectedItem();
            current_label.setText("The current working layer is " + x);
            if (x.equals("Layer 1")) {
                setCanvas_current(canvas);
                canvas.setVisible(true);
                canvas1.setVisible(false);
                setDef(canvas);
                setDef_Content(canvas.getGraphicsContext2D());
                System.out.println("Layer 1!");
            } else if (x.equals("Layer 2")) {
                //TODO
                setCanvas_current(canvas1);
                canvas1.setVisible(true);
                canvas.setVisible(false);
                setDef(canvas1);
                setDef_Content(canvas1.getGraphicsContext2D());
                initDraw(getDef_Content());

                if (this.getEraser() == true) {
                    getDef().addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {

                        gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);

                    });
                    getDef().addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent e) -> {
                        gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);
                    });
                    getDef().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent e) -> {
                    });
                } else {
                    getDef().addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {

                        getDef_Content().beginPath();
                        getDef_Content().moveTo(event.getX(), event.getY());
                        getDef_Content().stroke();

                    });
                    getDef().addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
                        getDef_Content().lineTo(event.getX(), event.getY());
                        getDef_Content().stroke();
                    });
                    getDef().addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent event) -> {
                    });
                }
                System.out.println("Layer 2!");
            }
        });
    }

    public StackPane createCanvas() {

        Canvas canvs1 = new Canvas();
        canvs1.toBack();
        StackPane holder = new StackPane();
//        layerContainer.getChildren().add(canvs1);
        canvs1.getGraphicsContext2D();
        holder.getChildren().add(canvas);
        holder.setStyle("-fx-background-color: " + randomRgbColorString());
        System.out.println("painter.createCanvas()");
        return holder;
    }

    private void initDraw(GraphicsContext gc) {

        gc.setStroke(save_Color);

        gc.setLineWidth(5);

    }

    //Creating the Brush
    private void createBrush() {
        label = new Label("Pen Size is 3");
        final Slider slider = new Slider();
        final ProgressIndicator pi = new ProgressIndicator(0);
        slider.setMin(1);
        slider.setMax(50);
        hb = new HBox(slider, pi);
        slider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {

            if (10 <= new_val.intValue()) {
                label.setText("Pen Size is " + new_val.toString().substring(0, 2));
            } else {
                label.setText("Pen Size is " + new_val.toString().substring(0, 1));
            }

            setbrushMaxSize(new_val);

            pi.setProgress(new_val.doubleValue() / 50);

            createBrushVariations();
        });
    }

    //Creating the Eraser
    private void createEraser() {
        eraser = new CheckBox("eraser");
        eraser.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            if (new_val == true) {
                colorPicker.setValue(Color.WHITE);
                createBrushVariations();
                this.setEraser(true);

            } else {
                colorPicker.setValue(save_Color);
                createBrushVariations();
                this.setEraser(false);
            }
        });
    }

    //Creating the color picker
    private void createColorPicker() {
        colorPicker.setValue(Color.BLACK);
        colorPicker.setOnAction(e -> {
            createBrushVariations();
            saveColor(colorPicker.getValue());
        });
    }

    private void create_top_Bar() {

        menubar = new MenuBar();
//        menubar.setStyle(STYLESHEET_MODENA);
        // --- Menu File
        menuFile = new Menu("File");
        menuLoad = new MenuItem("Open");
        menuLoad.setGraphic(new ImageView(
                new Image("open.png", 16, 16, false, false)));
        menuLoad.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif"),
                    new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            System.out.println(file);
        });

        menuSave = new MenuItem("Save");
        menuSave.setGraphic(new ImageView(
                new Image("save.png", 16, 16, false, false)));
        menuSave.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilter
                    = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);

            //Show save file dialog
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                try {
                    WritableImage writableImage;
                    writableImage = new WritableImage(100, 100);

                    //changed
                    getDef().snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    Logger.getLogger(painter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuFile.getItems().add(menuLoad);
        menuFile.getItems().add(menuSave);

        menuEffect = new Menu("Effect");
        menuEffect.setOnAction((ActionEvent event) -> {
            // Doesn't go into this method on click of the menu
            System.out.println("Inside On Action!!!");
        });
    }

    public Group group;

    private void createLayout() {

        Separator separator1 = new Separator();
        ToggleButton tb1 = new ToggleButton("Pen");
        ToggleButton tb2 = new ToggleButton("Eraser");
        ToggleButton tb3 = new ToggleButton("toggle button 3");
        final ToggleGroup grouptoggle = new ToggleGroup();
        tb1.setToggleGroup(grouptoggle);
        tb2.setToggleGroup(grouptoggle);
        tb3.setToggleGroup(grouptoggle);
        VBox vb_Toggle_button = VBoxBuilder.create()
                .spacing(5.0) //In case you are using HBoxBuilder
                .padding(new Insets(10, 5, 10, 5))
                .children(tb1, tb2, tb3)
                .build();
        tb1.setMaxWidth(Double.MAX_VALUE);
        tb2.setMaxWidth(Double.MAX_VALUE);
        tb3.setMaxWidth(Double.MAX_VALUE);
        tb1.setStyle("-fx-base: lightgreen;");
        tb2.setStyle("-fx-base: lightblue;");
        tb3.setStyle("-fx-base: salmon;");
        tb2.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            if (new_val == true) {

                this.setSaved_color(colorPicker.getValue());
                colorPicker.setValue(Color.WHITE);
                createBrushVariations();
                setEraser(true);
//                canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
//                        new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent e) {
//                        gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);
//                    }
//                });

            } else {
                setEraser(false);
                colorPicker.setValue(this.getSaved_color());
                createBrushVariations();
            }
        });

        vb_toolbar = new VBox();
        vb_layerbar = new VBox();
        hb_filebar = new HBox();

        menubar.getMenus().addAll(menuFile, menuEffect);
        label_system = new Label("Enble Cor-System");

        HBox hbox_layer_button1 = HBoxBuilder.create()
                .spacing(5.0) //In case you are using HBoxBuilder
                .padding(new Insets(5, 5, 5, 5))
                .children(addlayerButton, showallButton)
                .build();
        HBox hbox_layer_button2 = HBoxBuilder.create()
                .spacing(5.0) //In case you are using HBoxBuilder
                .padding(new Insets(5, 5, 5, 5))
                .children(clearLayer_button, showorderButton)
                .build();
        HBox hbox_layer_button3 = HBoxBuilder.create()
                .spacing(5.0) //In case you are using HBoxBuilder
                .padding(new Insets(5, 5, 5, 5))
                .children(remove_thisButton)
                .build();
        HBox hbox_layer_button4 = HBoxBuilder.create()
                .spacing(5.0) //In case you are using HBoxBuilder
                .padding(new Insets(5, 5, 5, 5))
                .children(Cor_system_checkBox, label_system)
                .build();
//        HBox hbox_layer_button5 = HBoxBuilder.create()
//                .spacing(5.0) //In case you are using HBoxBuilder
//                .padding(new Insets(5, 5, 5, 5))
//                .children(papercanvas_checkbox)
//                .build();
        addlayerButton.setStyle("-fx-base: " + randomRgbColorString());
        showallButton.setStyle("-fx-base: " + randomRgbColorString());
        clearLayer_button.setStyle("-fx-base: " + randomRgbColorString());
        showorderButton.setStyle("-fx-base: " + randomRgbColorString());
        remove_thisButton.setStyle("-fx-base: " + randomRgbColorString());

        hb_filebar.getChildren().addAll(menubar);

        vb_layerbar.getChildren().addAll(current_label, lv, hbox_layer_button1, hbox_layer_button2, hbox_layer_button3, separator1, hbox_layer_button4);
        vb_toolbar.getChildren().addAll(colorPicker, label, hb, eraser, separator1, vb_Toggle_button, slider_Opacity);

        vb_toolbar.setPadding(new Insets(20, 10, 10, 10));

        root = new BorderPane();

        setting_layer();

        createbackground();

        layerContainer = new StackPane();

        layerContainer.getChildren().addAll(canvas, canvas1, canvasBackgroud, blurryCanvas);

        setDef(canvas);

        group_bottom = new Group();
        root.setCenter(layerContainer);
        root.setTop(hb_filebar);
        root.setLeft(vb_toolbar);
        root.setRight(vb_layerbar);
        root.setBottom(group_bottom);
    }

    private void createCor_system() {
        text = new Text("X =    Y =   ");
        text.setTranslateX(100);
        text.setTranslateY(40);
        text.setFont(new Font(10));
        getDef().setOnMouseMoved((MouseEvent t) -> {
            text.setText("X = " + t.getX() + "  Y = " + t.getY());
        });
        group_bottom.getChildren().addAll(text);
    }

    private void setting_layer() {
//        System.out.println("painter.createLayout()");
//        image_paper = new Image("paper.jpg", CANVAS_HEIGHT, CANVAS_WIDTH, false, false);
//        System.out.println("painter.createLayout()2");
//        // simple displays ImageView the image as is
//        iv_paper1 = new ImageView();
//        iv_paper1.setImage(image_paper);
//        iv_paper1.setVisible(true);
        blurryCanvas = createCanvasGrid(CANVAS_WIDTH, CANVAS_HEIGHT, false);
        blurryCanvas.setVisible(false);

        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        canvas1 = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        canvasBackgroud = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
//        canvas.toFront();
        setDef_Content(canvas1.getGraphicsContext2D());
        canvasBackgroud.toBack();
        canvasBackgroud.getGraphicsContext2D().setFill(Color.WHITE);
        canvasBackgroud.getGraphicsContext2D().fillRect(0, 0, 840, 690);
    }

    private void initDraw_image(GraphicsContext gc, String c) {

        double canvasWidth = 840;
        double canvasHeight = 690;

        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);

        gc.fill();
        gc.strokeRect(
                0, //x of the upper left corner
                0, //y of the upper left corner
                canvasWidth, //width of the rectangle
                canvasHeight);  //height of the rectangle

        gc.setLineWidth(1);

        image1 = new Image(getClass().getResourceAsStream(c));
    }

    private void create_left_Bar() {

        createOpacity();

        createColorPicker();

        createBrush();

        createEraser();
    }

    private void settingBackground(String filePath) {

        initDraw_image(canvasBackgroud.getGraphicsContext2D(), filePath);
        graphicsContext.drawImage(image1, 20, 20);

    }

    private void createbackground() {

        scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.WHITE);
        scene.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            } else {
                event.consume();
            }
        });

        // Dropping over surface
        scene.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath = "";
                for (File file : db.getFiles()) {
                    filePath = file.getAbsolutePath();
                    System.out.println(filePath);
                    this.setPiccheck(true);
                    settingBackground(filePath);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void createOpacity() {
        slider_Opacity = new Slider();
        slider_Opacity.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            System.out.println(new_val.toString());
            this.getCanvas_current().getGraphicsContext2D().setGlobalAlpha((double) new_val);

        });
    }

    Boolean isEraser = false;

    private void setEraser(Boolean new_val) {
        this.isEraser = new_val;
    }

    private Boolean getEraser() {
        return this.isEraser;
    }
// ColorAdjust colorAdjust = new ColorAdjust();
// colorAdjust.setContrast(0.1);
// colorAdjust.setHue(-0.05);
// colorAdjust.setBrightness(0.1);
// colorAdjust.setSaturation(0.2);
// 
// Image image = new Image("boat.jpg");
// ImageView imageView = new ImageView(image);
// imageView.setFitWidth(200);
// imageView.setPreserveRatio(true);
// imageView.setEffect(colorAdjust);

    private Canvas createCanvasGrid(double width, double height, boolean sharp) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
//        square.setFill(Color.GREEN);
//        square.setStroke(Color.BLUE);
        gc.setLineWidth(0.5);
        for (int x = 0; x < width; x += 20) {
            double x1;
            if (sharp) {
                x1 = x + 0.5;
            } else {
                x1 = x;
            }
            gc.moveTo(x1, 0);
            gc.lineTo(x1, height);
            gc.stroke();
            gc.setStroke(Color.GAINSBORO);
        }

        for (int y = 0; y < height; y += 20) {
            double y1;
            if (sharp) {
                y1 = y + 0.1;
            } else {
                y1 = y;
            }
            gc.moveTo(0, y1);
            gc.lineTo(width, y1);
            gc.stroke();
        }
        return canvas;
    }
}
