import java.util.HashMap;
import java.util.Map;

public class CssGenerator_Type2 {

    public static void main(String... a) {
        Map<Integer, Number[]> sizes = new HashMap<>();
        sizes.put(-4, new Number[]{14, 6, 11, 6}); // cellSize(px), font-size(pt), icon-size(px), glyph-font-size(px)
        sizes.put(-3, new Number[]{15, 7, 12, 8});
        sizes.put(-2, new Number[]{16, 8, 13, 10});
        sizes.put(-1, new Number[]{17, 9, 14, 11});
        sizes.put(0, new Number[]{18, 10, 16, 13.333333});
        sizes.put(1, new Number[]{20, 11, 18, 14.666667});
        sizes.put(2, new Number[]{22, 12, 20, 16});
        sizes.put(3, new Number[]{24, 13, 22, 17.333333});
        sizes.put(4, new Number[]{26, 14, 24, 18.666667});
        sizes.put(5, new Number[]{28, 15, 26, 20});
        sizes.put(6, new Number[]{30, 16, 28, 21.333333});
        sizes.put(7, new Number[]{32, 17, 30, 22.666667});
        sizes.put(8, new Number[]{34, 18, 32, 24});
        sizes.put(9, new Number[]{36, 19, 34, 25.333333});
        sizes.put(10, new Number[]{38, 20, 36, 26.666667});

        for (int zoom = -4; zoom < 11; zoom++) {
            System.out.println("/* ### ZOOM LEVEL "+zoom+" ### */");
            System.out.println(".instruments-tab-pane-zoom" + zoom + ".table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell,");
            System.out.println(".instruments-tab-pane-zoom" + zoom + ".table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell > .table-cell,");
            System.out.println(".instruments-tab-pane-zoom" + zoom + ".table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell > .table-cell > .root {");
            System.out.println("    -fx-cell-size: " + sizes.get(zoom)[0] + "px;");
            System.out.println("}");

            System.out.println(".instruments-tab-pane-zoom" + zoom + ".table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell > .table-cell > .text,");
            System.out.println(".instruments-tab-pane-zoom" + zoom + ".table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell > .table-cell > .label,");
            System.out.println(".instruments-tab-pane-zoom" + zoom + ".table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell > .table-cell .wrapper > .label,");
            System.out.println(".instruments-tab-pane-zoom" + zoom + ".table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell > .table-cell > .text-field {");
            System.out.println("    -fx-font-size: " + sizes.get(zoom)[1] + "pt;");
            System.out.println("}");

            System.out.println(".instruments-tab-pane-zoom" + zoom + ".table-view > .virtual-flow > .clipped-container > .sheet > .table-row-cell > .table-cell .xfe-cell-button {");
            System.out.println("    -xfe-icon-size: " + sizes.get(zoom)[2] + "px;");
            System.out.println("}");

            System.out.println(".instruments-tab-pane-zoom" + zoom + ".table-view .action-column{");
            System.out.println("    -fx-pref-width:" + sizes.get(zoom)[0] + "px;");
            System.out.println("}");
            System.out.println("");
        }
    }
}
