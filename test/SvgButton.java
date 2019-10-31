import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * Custom Button control for explicitly showing SVG based icon only without text and custom background.
 */
public class SvgButton extends Button {
    private final StackPane svg = new StackPane();

    /**
     * Default constructor used to create control from FXML files.
     */
    public SvgButton() {
        this(null,null);
    }

    public SvgButton(String buttonStyle, String svgStyle) {
        super();
        apply(this, buttonStyle);
        svg.getStyleClass().add("svg");
        setGraphic(svg);
        svgStyleClassProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != null && !oldVal.isEmpty()) {
                svg.getStyleClass().removeAll(oldVal);
            }
            if (newVal != null && !newVal.isEmpty()) {
                svg.getStyleClass().addAll(newVal);
            }
        });
        setSvgStyleClass(svgStyle);
    }

    public static void apply(Button b, String... buttonStyles) {
        b.getStyleClass().add("xfe-svg-button");
        if (buttonStyles != null) {
            b.getStyleClass().addAll(buttonStyles);
        }
    }

    public final StringProperty svgStyleClassProperty() {
        if (svgStyleClass == null) {
            svgStyleClass = new SimpleStringProperty(this, "svgStyleClass", "");
        }
        return svgStyleClass;
    }

    /**
     * The svg style class to set on button.
     */
    private StringProperty svgStyleClass;

    public final void setSvgStyleClass(String value) {
        svgStyleClassProperty().setValue(value);
    }

    public final String getSvgStyleClass() {
        return svgStyleClass == null ? "" : svgStyleClass.getValue();
    }
}
/*
<rect class="st0" width="10" height="1"/>
<polygon class="st0" points="10,0.6 9.4,0 5,4.4 0.6,0 0,0.6 4.4,5 0,9.4 0.6,10 5,5.6 9.4,10 10,9.4 5.6,5 "/>
    -fx-background-color: #8397AE;
    -fx-shape:"M8,10H2c-1.1,0-2-0.9-2-2V2c0-1.1,0.9-2,2-2h6c1.1,0,2,0.9,2,2v6C10,9.1,9.1,10,8,10z M2.14,1 C1.51,1,1,1.51,1,2.14v5.71C1,8.49,1.51,9,2.14,9h5.71C8.49,9,9,8.49,9,7.86V2.14C9,1.51,8.49,1,7.86,1H2.14z";
*/
