import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * Custom Button control for explicitly showing SVG based icon only without text and custom background.
 */
public class SvgButton extends Button {
    private final StackPane svg = new StackPane();

    public SvgButton() {
        this(null);
    }

   public SvgButton(String buttonStyle) {
        super();
        apply(this, buttonStyle);
        svg.getStyleClass().add("svg");
        setGraphic(svg);
    }

    public void apply(Button b, String... buttonStyles) {
        b.getStyleClass().addAll("svg-button", "xfe-svg-button");
        if (buttonStyles != null) {
            b.getStyleClass().addAll(buttonStyles);
        }
    }

    public void setSize(double size) {
        svg.setMaxSize(size, size);
        svg.setMinSize(size, size);
    }

    public void setSize(double width, double height) {
        svg.setMaxSize(width, height);
        svg.setMinSize(width, height);
    }
}

/*
.xfe-window-pane {
    -fx-background-color: #0E273D;
    -fx-border-width: 2px;
    -fx-border-color: derive(#5f9eaf,-45%);
}

.xfe-window-pane:focused {
    -fx-border-color: #5f9eaf;
}

.xfe-title-bar {
    -fx-background-color: #0A141E;
    -fx-border-width: 0 0 2 0;
    -fx-border-color: #0C1D2D;
}

.xfe-title-bar .text {
    -fx-fill: #f7f7f7;
}
.xfe-title-label{
    -fx-font-weight: normal;
    -fx-font-size: 11pt;
}

.xfe-sub-title-label{
    -fx-font-weight: normal;
    -fx-font-size: 10pt;
}

.xfe-button-close,
.xfe-button-maximize,
.xfe-button-minimize {
    -fx-background-color: #0A141E !important;
    -fx-background-radius: 0;
    -fx-pref-width: 32;
    -fx-min-width: 32;
    -fx-max-width: 32;
    -fx-pref-height: 28;
    -fx-padding: 0px;
    -fx-background-insets: 0;
    -fx-background-position: center;
    -fx-background-repeat: no-repeat;
    -fx-background-size: 14 14;
}

.xfe-button-maximize:hover,
.xfe-button-minimize:hover {
    -fx-background-color: #1E415D !important;
}

.xfe-button-close:hover {
    -fx-background-color: #dd1111 !important;
}

.xfe-button-minimize .svg{
    -fx-background-color: #8397AE;
    -fx-shape:"M37.059,16H26H16H4.941C2.224,16,0,18.282,0,21s2.224,5,4.941,5H16h10h11.059C39.776,26,42,23.718,42,21 S39.776,16,37.059,16z";
    -fx-max-height:2px;
}
.xfe-button-maximize .svg{
    -fx-background-color: #8397AE;
    -fx-shape:"M8,10H2c-1.1,0-2-0.9-2-2V2c0-1.1,0.9-2,2-2h6c1.1,0,2,0.9,2,2v6C10,9.1,9.1,10,8,10z M2.14,1 C1.51,1,1,1.51,1,2.14v5.71C1,8.49,1.51,9,2.14,9h5.71C8.49,9,9,8.49,9,7.86V2.14C9,1.51,8.49,1,7.86,1H2.14z";
}
.xfe-button-close .svg{
    -fx-background-color: #8397AE;
    -fx-shape:"M28.228,23.986L47.092,5.122c1.172-1.171,1.172-3.071,0-4.242c-1.172-1.172-3.07-1.172-4.242,0L23.986,19.744L5.121,0.88 c-1.172-1.172-3.07-1.172-4.242,0c-1.172,1.171-1.172,3.071,0,4.242l18.865,18.864L0.879,42.85c-1.172,1.171-1.172,3.071,0,4.242 C1.465,47.677,2.233,47.97,3,47.97s1.535-0.293,2.121-0.879l18.865-18.864L42.85,47.091c0.586,0.586,1.354,0.879,2.121,0.879 s1.535-0.293,2.121-0.879c1.172-1.171,1.172-3.071,0-4.242L28.228,23.986z";
}


buttonMinimize = new SvgButton();
buttonMinimize.setSize(14,2);
*/
