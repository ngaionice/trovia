package ui;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DesignProperties {

    // used for colors, fonts, background etcs, for design consistency

    // FONTS

    public Font fontHeader = Font.font("Roboto Medium", 15);
    public Font fontSubHeader = Font.font("Roboto Medium", 13);
    public Font fontNormal = Font.font("Roboto Regular", 12);

    // COLORS

    public String colorTextHeader = "#FAFAFA";
    public String colorTextNormal = "#C7C7C7";

    public String colorTextDialogButton = "#FF9800";
    public String colorTextMainButton = "#424242";

    public String colorTextFieldFocus = "#FF9800";

    // BACKGROUNDS

    public Background backgroundMainPane = new Background(new BackgroundFill(Color.rgb(27, 27, 27), CornerRadii.EMPTY, Insets.EMPTY));
    public Background backgroundMainButton = new Background(new BackgroundFill(Color.rgb(238, 238, 238), new CornerRadii(3), Insets.EMPTY));
    public Background backgroundDialogButton = new Background(new BackgroundFill(Color.rgb(109, 109, 109), new CornerRadii(3), Insets.EMPTY));
}
