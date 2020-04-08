import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.thales.atm.thmi.widget.ThmiWindow;
import com.thales.atm.thmi.widget.property.LookAndFeel;
import com.thales.atm.thmi.widget.property.TitleProperty;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Customized ScenicView implementation for the needs of THMI widgets and application.
 *
 * WORK IN PROGRESS !! ADDED FOR TESTING IN PLATFORM
 */
public class ThmiScenicView {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ThmiScenicView.class.getName());

    /** Color code of the property label. */
    private static final String PROP_COLOR = "#0082DD";

    /**
     * Properties that needs to be highlighted to quickly identify them among the list. As of now these properties
     * values are displayed in bold & red color.
     */
    private final List<String> highlights = Arrays.asList("id", "width", "height", "layoutBounds");

    /** Map of all observable properties of the selected node in the tree view. */
    private final Map<String, ObservableValue<?>> properties = new TreeMap<>();

    /** Variable for incrementing the row index, declaring as instance variable as it is used in lambdas. */
    private int rowIndex = 0;

    /** Specifies whether the bounds overlay on the node is enabled or disabled. */
    private final BooleanProperty showBounds = new SimpleBooleanProperty();

    /**
     * Specifies whether the node preview is enabled or disabled. Taking snapshot is a bit expensive operation, so
     * switching quickly between nodes can be a performance issue. Turning off the CheckBox will not consider
     * taking snapshots of the node.
     */
    private final BooleanProperty showPreview = new SimpleBooleanProperty();

    /**
     * Specifies whether the preview background is dark or not. The idea is to pop out the preview if it is too
     * light/dark color.
     */
    private final BooleanProperty darkPreview = new SimpleBooleanProperty();

    /**
     * Overlay {@code Popup} to show the bounds of the node, over the node.
     *
     * Note: More work need to fix the spotty issue of displaying incorrect overlay. Just switching the nodes 2-3
     * times will fix it.
     */
    private final Popup overLayPopup = new Popup();

    /**
     * Applies the ScenicView implementation on the {@code Scene} of the provided {@code Node}.
     *
     * @param node Node whose scene needs to be registered
     */
    public static final void apply(final Node node) {
        if (node.getScene() == null) {
            node.sceneProperty().addListener((obs, old, scene) -> {
                if (scene != null) {
                    show(scene);
                }
            });
        } else {
            show(node.getScene());
        }
    }

    /**
     * Applies the ScenicView implementation on the provided {@code Scene}.
     *
     * @param scene Scene that needs to be registered
     */
    public static final void show(final Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() && e.isAltDown() && e.isShiftDown() && e.getCode() == KeyCode.T) {
                new ThmiScenicView().scan(scene.getRoot());
            }
        });
    }

    /**
     * Applies the ScenicView implementation on the {@code Scene} of the provided {@code Window}. This method
     * enables to inspect the scene of {@code ContextMenu} as well.
     *
     * @param window Window whose scene needs to be registered
     */
    public static final void show(final Window window) {
        if (window.getScene() == null) {
            window.sceneProperty().addListener((obs, old, scene) -> {
                if (scene != null) {
                    show(scene);
                }
            });
        } else {
            show(window.getScene());
        }
    }

    /**
     * Returns the simple class name of the provide node.
     *
     * @param node Node to which the class name need to be determined
     * @return Node class name
     */
    String getClassName(final Node node) {
        String className = node.getClass().getSimpleName();
        if (className == null || className.isEmpty()) {
            className = node.getClass().toString();
            className = className.substring(className.lastIndexOf('.') + 1);
        }
        return className;
    }

    /**
     * Builds the pane for displaying all properties of the provided node.
     *
     * @param node Node whose properties need to be displayed
     * @return GridPane listing all properties
     */
    private GridPane buildFullPropertiesDetails(final Node node) {
        LOGGER.log(Level.INFO, () -> "Building properties details of node " + node.toString());
        final GridPane detailsPane = buildGridPane();
        rowIndex = 0;
        properties.forEach((key, val) -> {
            final Node value = buildValueNode(val);
            if (highlights.contains(key)) {
                value.setStyle("-fx-font-weight:bold;-fx-fill:red;");
            }
            detailsPane.addRow(rowIndex++, buildPropText(key), value);
        });
        return detailsPane;
    }

    /**
     * Builds the properties layout template that will be displayed in different TitlePanes.
     *
     * @return GridPane template
     */
    private GridPane buildGridPane() {
        final GridPane detailsPane = new GridPane();
        detailsPane.setPadding(new Insets(8));
        detailsPane.setVgap(5);
        detailsPane.setHgap(5);
        final ColumnConstraints c = new ColumnConstraints();
        c.setMinWidth(200);
        c.setMaxWidth(200);
        c.setHalignment(HPos.RIGHT);
        detailsPane.getColumnConstraints().add(c);
        final RowConstraints r = new RowConstraints();
        r.setValignment(VPos.TOP);
        detailsPane.getRowConstraints().addAll(r);
        return detailsPane;
    }

    /**
     * Builds the grid layout for showing the Insets value with the provided label at the center.
     *
     * @param insets Insets to be shown
     * @param lbl Info label to be displayed at center
     * @return GridPane layout
     */
    private GridPane buildInsetsGrid(final Insets insets, final String lbl) {
        final Text lblText = new Text(lbl);
        lblText.setStyle("-fx-font-size:9px;-fx-fill:" + PROP_COLOR);
        final GridPane insetGrid = new GridPane();
        insetGrid.setVgap(2);
        insetGrid.setHgap(2);
        insetGrid.addRow(0, new Text(), new Text(insets.getTop() + ""), new Text());
        insetGrid.addRow(1, new Text(insets.getLeft() + ""), lblText, new Text(insets.getRight() + ""));
        insetGrid.addRow(2, new Text(), new Text(insets.getBottom() + ""), new Text());
        final ColumnConstraints cc = new ColumnConstraints();
        cc.setHalignment(HPos.CENTER);
        insetGrid.getColumnConstraints().addAll(new ColumnConstraints(), cc);
        return insetGrid;
    }

    /**
     * Builds the pane for displaying specific properties of the provided node. This is to just quickly refer to
     * some special properties that will be mostly referred.
     *
     * @param node Node whose properties need to be displayed
     * @return GridPane listing the properties
     */
    private GridPane buildNodeDetails(final Node node) {
        final GridPane detailsPane = buildGridPane();
        rowIndex = 0;
        highlights.forEach(key -> {
            final ObservableValue<?> val = properties.get(key);
            detailsPane.addRow(rowIndex++, buildPropText(key), buildValueNode(val));
        });

        final Bounds screenBounds = node.localToScreen(node.getLayoutBounds());
        detailsPane.addRow(rowIndex++, buildPropText("screenBounds"),
                buildValueNode(new SimpleObjectProperty<>(screenBounds)));

        final String styleClass = node.getStyleClass().stream().collect(Collectors.joining(", "));
        detailsPane.addRow(rowIndex++, buildPropText("styleClass"),
                buildValueNode(new SimpleObjectProperty<>(styleClass)));

        final String pseudoClass =
                node.getPseudoClassStates().stream().map(PseudoClass::getPseudoClassName).collect(
                        Collectors.joining(", "));
        detailsPane.addRow(rowIndex++, buildPropText("pseudoClass"),
                buildValueNode(new SimpleObjectProperty<>(pseudoClass)));

        if (properties.get("font") != null) {
            detailsPane.addRow(rowIndex++, buildPropText("font"), buildValueNode(properties.get("font")));
        }
        if (properties.get("vgap") != null) {
            detailsPane.addRow(rowIndex++, buildPropText("vgap"), buildValueNode(properties.get("vgap")));
        }
        if (properties.get("hgap") != null) {
            detailsPane.addRow(rowIndex++, buildPropText("hgap"), buildValueNode(properties.get("hgap")));
        }
        if (properties.get("spacing") != null) {
            detailsPane.addRow(rowIndex++, buildPropText("spacing"), buildValueNode(properties.get("spacing")));
        }
        detailsPane.addRow(rowIndex++, buildPropText("style"), buildValueNode(node.styleProperty()));

        if (node instanceof Region) {
            Insets padding = ((Region) node).getPadding();
            if (padding == null) {
                padding = Insets.EMPTY;
            }
            detailsPane.addRow(rowIndex++, buildPropText("padding"),
                    buildValueNode(new SimpleObjectProperty<>(padding)));
        }
        final List<String> styleSheets = new ArrayList<>();
        getAppliedStyleSheets(node, styleSheets);
        if (!styleSheets.isEmpty()) {
            detailsPane.addRow(rowIndex++, buildPropText("styleSheets"), buildValueNode(
                    new SimpleObjectProperty<>(styleSheets.stream().collect(Collectors.joining("\n")))));
        }

        return detailsPane;
    }

    /**
     * Builds the preview pane for the node.
     *
     * @param node Node whose preview need to be displayed
     * @return TitledPane for preview
     */
    private TitledPane buildPreview(final Node node) {
        final SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        final ImageView image = new ImageView(node.snapshot(sp, null));
        final Pane bg = new Pane(image);
        bg.setBackground(getPatternBackground());
        final StackPane previewPane = new StackPane(new Group(bg));
        previewPane.setStyle(darkPreview.get() ? "-fx-background-color:#555555;" : "");
        previewPane.setAlignment(Pos.TOP_LEFT);
        previewPane.setPadding(new Insets(10));
        final TitledPane previewNode = new TitledPane("Preview", previewPane);
        previewNode.setId("preview");
        darkPreview.addListener((obs, old,
                dark) -> previewNode.getContent().setStyle(dark ? "-fx-background-color:#555555;" : ""));

        return previewNode;
    }

    /**
     * Builds the display node for the provided property name.
     *
     * @param text Property of the node
     * @return Node to be displayed
     */
    private Node buildPropText(final String text) {
        final Text prop = new Text(text + ":");
        prop.setStyle("-fx-font-weight:bold;-fx-fill:" + PROP_COLOR);
        if (text.equals("background")
            || text.equals("padding")
            || text.equals("insets")
            || text.equals("border")) {
            final StackPane sp = new StackPane(prop);
            sp.setAlignment(Pos.TOP_RIGHT);
            return sp;
        }
        return prop;
    }

    /**
     * Builds the layout for displaying the corder radius details.
     *
     * @param radii Instance of CornerRadii
     * @return GridPane
     */
    private GridPane buildRadiiGrid(final CornerRadii radii) {
        final Text lblText = new Text("rad");
        lblText.setStyle("-fx-font-size:9px;-fx-fill:" + PROP_COLOR);
        final GridPane insetGrid = new GridPane();
        insetGrid.setVgap(2);
        insetGrid.setHgap(2);
        insetGrid.addRow(0, new Text(radii.getTopLeftHorizontalRadius() + ""), new Text(),
                new Text(radii.getTopRightHorizontalRadius() + ""));
        insetGrid.addRow(1, new Text(), lblText, new Text());
        insetGrid.addRow(2, new Text(radii.getBottomLeftHorizontalRadius() + ""), new Text(),
                new Text(radii.getBottomRightHorizontalRadius() + ""));
        final ColumnConstraints cc = new ColumnConstraints();
        cc.setHalignment(HPos.CENTER);
        insetGrid.getColumnConstraints().addAll(new ColumnConstraints(), cc);
        return insetGrid;
    }

    /**
     * Builds the title for the scenic view stage. The title consists of the provided scene's stage title or the
     * stage class name with time at which the scenic view is requested.
     *
     * @param rootNode Root node of the scene
     * @return Title for the stage
     */
    private String buildStageTitle(final Node rootNode) {
        final StringBuilder titleBuilder = new StringBuilder("ThmiScenicView - ");
        final Window window = rootNode.getScene().getWindow();
        String stageTitle = "";
        if (window instanceof Stage) {
            final Stage stg = (Stage) window;
            stageTitle = stg.getTitle() != null && !stg.getTitle().isEmpty() ? stg.getTitle() : "";
        }
        if (stageTitle.isEmpty()) {
            titleBuilder.append(window.getClass().getSimpleName());
        } else {
            titleBuilder.append(stageTitle);
        }
        titleBuilder.append(" ");
        final Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        final LocalTime time = instant.atZone(ZoneId.systemDefault()).toLocalTime();
        titleBuilder.append("[ ").append(time.format(DateTimeFormatter.ISO_LOCAL_TIME)).append(" ]");
        return titleBuilder.toString();
    }

    /**
     * Builds the appropriate node to display the value of the provided observable property.
     *
     * @param value Observable property whose value needs to be displayed
     * @return Node displaying the value
     */
    private Node buildValueNode(final ObservableValue<?> value) {
        if (value != null && value.getValue() != null) {
            /* Building insets grid for Insets type of value */
            if (value.getValue() instanceof Insets) {
                final Insets insets = (Insets) value.getValue();
                return buildInsetsGrid(insets, null);
            }
            /* Building background grid for displaying background value */
            else if (value.getValue() instanceof Background) {
                final Background background = (Background) value.getValue();
                final Text bgToString = new Text(background.toString());
                final GridPane pane = new GridPane();
                pane.setHgap(10);
                pane.setVgap(2);
                pane.add(bgToString, 0, 0, 4, 1);
                if (background.getFills().isEmpty()) {
                    bgToString.setText(bgToString.getText() + "  (no fills)");
                } else {
                    final AtomicInteger cnt = new AtomicInteger();
                    background.getFills().forEach(fill -> {
                        final int index = cnt.getAndIncrement();
                        final Text fillText = new Text("fill#" + index + ":");
                        fillText.setStyle("-fx-fill:" + PROP_COLOR);
                        final Paint fillPaint = fill.getFill();
                        String fillTextStr = "";
                        String fillBgStr = "";
                        final Text text = new Text();
                        final StackPane box = new StackPane(text);
                        box.setPrefSize(200, 32);
                        box.setMaxSize(200, 32);

                        if (fillPaint instanceof LinearGradient) {
                            fillBgStr = fillPaint.toString().replaceAll("0x", "#");
                            fillTextStr = fillBgStr;
                        } else {
                            final String hexColor = getHexColor(fillPaint);
                            final String rgbaColor = getRGBAColor(fillPaint);
                            fillTextStr = rgbaColor + "\nHex : " + hexColor;
                            fillBgStr = hexColor;
                            text.setText(fillTextStr);
                            final Paint constrastColor = getContrastColor(fillPaint);
                            text.setFill(constrastColor);
                            box.setBorder(new Border(new BorderStroke(constrastColor, BorderStrokeStyle.DASHED,
                                                                      CornerRadii.EMPTY, new BorderWidths(1))));
                        }
                        Tooltip.install(box, new Tooltip(fillTextStr));
                        box.setStyle("-fx-background-color:" + fillBgStr);
                        pane.addRow(index + 1, fillText, box, buildInsetsGrid(fill.getInsets(), "insets"),
                                buildRadiiGrid(fill.getRadii()));
                    });
                }
                return pane;
            }
            /* For all other type of values, building the text node to display */
            else if (!value.getValue().toString().trim().isEmpty()) {
                return new Text(value.getValue().toString().trim());
            }
        }
        return new Text("-");
    }

    /**
     * Changes the given color value to hex value.
     *
     * @param channelValue color channel value
     * @return Hex value of color channel
     */
    private String colorChannelToHex(final double channelValue) {
        String hex = Integer.toHexString((int) Math.min(Math.round(channelValue * 255), 255));
        if (hex.length() == 1) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * Gets the applied style sheets on the node and add them to the provided list.
     *
     * @param node Node whose styling details need to be fetched
     * @param styleSheets List to which the styleSheets need to be copied
     */
    private void getAppliedStyleSheets(final Node node, final List<String> styleSheets) {
        if (node instanceof Parent) {
            final String styleSheet = ((Parent) node).getStylesheets().stream().collect(Collectors.joining("\n"));
            if (styleSheet != null && !styleSheet.isEmpty()) {
                styleSheets.add(styleSheet);
            }
        }
        if (node.getParent() != null) {
            getAppliedStyleSheets(node.getParent(), styleSheets);
        }
    }

    /**
     * Recursive method to build the tree items for the children of the provided node.
     *
     * @param node Node to be inspected
     * @return List of tree items of the child nodes
     */
    private List<TreeItem<Node>> getChildItems(final Node node) {
        final List<TreeItem<Node>> list = new ArrayList<>();
        if (node instanceof Parent) {
            final Parent parent = (Parent) node;
            parent.getChildrenUnmodifiable().forEach(childNode -> {
                final TreeItem<Node> childNodeItem = new TreeItem<>(childNode);
                childNodeItem.getChildren().addAll(getChildItems(childNode));
                childNodeItem.setExpanded(true);
                list.add(childNodeItem);
            });
        }
        return list;
    }

    /**
     * Returns the contrast color of the provided {@link Color}. The return value will be in gray/white.
     *
     * @param paint Color reference object
     * @return Contrast color
     */
    private Paint getContrastColor(final Paint paint) {
        if (paint instanceof Color) {
            final Color color = (Color) paint;
            final double base = (color.getRed() + color.getGreen() + color.getBlue()) / 3 > 0.5 ? 0 : 1;
            final int val = (int) (255 * base);
            return Color.rgb(val, val, val);
        }
        return paint;
    }

    /**
     * Returns the hex color value for the provided {@link Color}.
     *
     * @param paint Color reference object
     * @return Hex Color value in {@link Color}
     */
    private String getHexColor(final Paint paint) {
        if (paint instanceof Color) {
            final Color color = (Color) paint;
            return "#"
                + colorChannelToHex(color.getRed())
                + colorChannelToHex(color.getGreen())
                + colorChannelToHex(color.getBlue())
                + colorChannelToHex(color.getOpacity());
        }
        return "-";
    }

    /**
     * @return a patterned background
     */
    private Background getPatternBackground() {
        final String color1 = "linear-gradient(from 0px 0px to 0px 20px , repeat, #DDDDDD 10px , #FFFFFF 10px )";
        final String color2 =
                "linear-gradient(from 0px 0px to 20px 0px , repeat, #DDDDDD60 10px , #FFFFFF60 10px )";
        return new Background(new BackgroundFill(LinearGradient.valueOf(color1), CornerRadii.EMPTY, Insets.EMPTY),
                              new BackgroundFill(LinearGradient.valueOf(color2), CornerRadii.EMPTY, Insets.EMPTY));
    }

    /**
     * Returns the RGBA color values for the provided {@link Color}.
     *
     * @param paint Color reference object
     * @return RGBA color values in {@link Color}
     */
    private String getRGBAColor(final Paint paint) {
        if (paint instanceof Color) {
            final Color color = (Color) paint;
            final int r = (int) Math.round(color.getRed() * 255.0);
            final int g = (int) Math.round(color.getGreen() * 255.0);
            final int b = (int) Math.round(color.getBlue() * 255.0);
            final int a = (int) Math.round(color.getOpacity() * 255.0);
            return String.format("RGBA : (%s,%s,%s,%s)", r, g, b, a);
        }
        return "-";
    }

    /**
     * Loads all the observable properties of the provided node.
     *
     * @param node Node of which the properties need to be loaded
     */
    private void loadProperties(final Node node) {
        properties.clear();
        final Method[] methods = node.getClass().getMethods();
        final int length = methods.length;
        for (int count = 0; count < length; ++count) {
            final Method method = methods[count];
            if (method.getName().endsWith("Property")) {
                try {
                    final Class<?> returnType = method.getReturnType();
                    if (ObservableValue.class.isAssignableFrom(returnType)) {
                        final String propertyName =
                                method.getName().substring(0, method.getName().lastIndexOf("Property"));
                        method.setAccessible(true);
                        final ObservableValue<?> property = (ObservableValue<?>) method.invoke(node);
                        properties.put(propertyName, property);
                    }
                } catch (final Exception e) {
                    LOGGER.log(Level.SEVERE,
                            () -> "Error in loading the properties of the node ::  " + e.getMessage());
                }
            }
        }
    }

    /**
     * Scans the complete node hierarchy of the provided root node of the scene and builds the treeView.
     *
     * @param rootNode Root node of the scene
     */
    private void scan(final Node rootNode) {
        final VBox root = new VBox();
        final ThmiWindow stage = new ThmiWindow(root);
        stage.setLookAndFeel(LookAndFeel.DOMAIN);
        final TitleProperty titleProp = new TitleProperty();
        titleProp.set(buildStageTitle(rootNode));
        stage.setTitleProperty(titleProp);

        stage.setWidth(1300);
        stage.setHeight(800);
        stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            /* Ctrl + Alt + X */
            if (e.isControlDown() && e.isAltDown() && e.getCode() == KeyCode.X) {
                overLayPopup.hide();
                stage.closeWindow();
            }
        });
        stage.setOnHiding(e -> overLayPopup.hide());
        stage.showWindow();

        final ScrollPane detailsContent = new ScrollPane();
        detailsContent.setFitToHeight(true);
        detailsContent.setFitToWidth(true);

        final TreeView<Node> treeView = new TreeView<>();
        treeView.setCellFactory(param -> new TreeCell<Node>() {

            @Override
            protected void updateItem(final Node item, final boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (item != null) {
                    final Text id = new Text(item.getId() != null ? " [#" + item.getId() + "]" : "");
                    id.setStyle("-fx-font-weight:bold;");
                    setGraphic(new HBox(new Text(getClassName(item)), id));
                }
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, old, item) -> {
            if (item != null) {
                final Node selectedNode = item.getValue();
                showDetails(selectedNode, detailsContent);
            }
        });
        showBounds.addListener((obs, old, show) -> {
            if (!show) {
                overLayPopup.hide();
            } else if (treeView.getSelectionModel().getSelectedItem() != null) {
                showBounds(treeView.getSelectionModel().getSelectedItem().getValue());
            }
        });
        final TreeItem<Node> rootItem = new TreeItem<>(rootNode);
        rootItem.getChildren().addAll(getChildItems(rootNode));
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);

        final Tab detailsTab = new Tab("Details");
        detailsTab.setClosable(false);
        detailsTab.setContent(detailsContent);
        final Tab cssTab = new Tab("Scene CSS");
        cssTab.setClosable(false);
        final VBox cssList = new VBox();
        cssList.setPadding(new Insets(10));
        cssList.setSpacing(10);
        final ScrollPane cssScrollContent = new ScrollPane();
        cssScrollContent.setContent(cssList);
        cssScrollContent.setFitToHeight(true);
        cssScrollContent.setFitToWidth(true);
        cssTab.setContent(cssScrollContent);
        rootNode.getScene().getStylesheets().forEach(sheet -> cssList.getChildren().add(new Text(sheet)));

        final TabPane tabPane = new TabPane(detailsTab, cssTab);
        final SplitPane splitPane = new SplitPane();
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        splitPane.getItems().addAll(treeView, tabPane);
        splitPane.setDividerPosition(0, .30);

        final CheckBox showBoundsCB = new CheckBox("Show Layout Bounds");
        showBoundsCB.setSelected(true);
        showBounds.bind(showBoundsCB.selectedProperty());

        final CheckBox showPreviewCB = new CheckBox("Show Node Preview");
        showPreviewCB.setSelected(true);
        showPreview.bind(showPreviewCB.selectedProperty());

        final CheckBox darkPreviewCB = new CheckBox("Dark Preview");
        darkPreview.bind(darkPreviewCB.selectedProperty());
        darkPreviewCB.disableProperty().bind(showPreviewCB.selectedProperty().not());

        final ToolBar toolBar = new ToolBar(showBoundsCB, showPreviewCB, darkPreviewCB);
        root.getChildren().addAll(toolBar, splitPane);
    }

    /**
     * Shows the overlay bounds at the location of provided node.
     *
     * @param node Node to be inspected
     */
    private void showBounds(final Node node) {
        final Bounds bounds = node.getBoundsInLocal();
        final Bounds screenBounds = node.localToScreen(bounds);
        final StackPane overLay = new StackPane();
        overLay.setStyle(
                "-fx-background-color:#FFFF0066;-fx-border-color:green;-fx-border-width:1px;-fx-border-style: segments(5, 5, 5, 5)  line-cap round ;");
        overLay.setPrefSize(bounds.getWidth(), bounds.getHeight());
        overLayPopup.getContent().clear();
        overLayPopup.getContent().addAll(overLay);
        overLayPopup.setX(screenBounds.getMinX());
        overLayPopup.setY(screenBounds.getMinY());
        overLayPopup.show(node.getScene().getWindow());
    }

    /**
     * Displays the details of the selected node in the details section.
     *
     * @param node Node which needs to be inspected
     * @param detailsContent ScrollPane to which the details need to be set
     */
    private void showDetails(final Node node, final ScrollPane detailsContent) {
        loadProperties(node);
        final TitledPane nodeDetails = new TitledPane("Node Details", buildNodeDetails(node));
        final TitledPane fullPropDetails =
                new TitledPane("Full Properties Details", buildFullPropertiesDetails(node));
        final VBox vPane = new VBox(nodeDetails, fullPropDetails);
        if (showPreview.get()) {
            vPane.getChildren().add(0, buildPreview(node));
        }
        showPreview.addListener((obs, old, show) -> {
            if (show) {
                vPane.getChildren().add(0, buildPreview(node));
            } else if ("preview".equals(vPane.getChildren().get(0).getId())) {
                vPane.getChildren().remove(0);
            }
        });
        detailsContent.setContent(vPane);
        if (showBounds.get()) {
            showBounds(node);
        }
    }
}
