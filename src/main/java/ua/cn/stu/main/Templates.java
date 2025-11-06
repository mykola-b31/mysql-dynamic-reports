package ua.cn.stu.main;

import static ch.unibas.medizin.dynamicreports.report.builder.DynamicReports.*;
import ch.unibas.medizin.dynamicreports.report.builder.ReportTemplateBuilder;
import ch.unibas.medizin.dynamicreports.report.builder.style.StyleBuilder;
import ch.unibas.medizin.dynamicreports.report.constant.HorizontalTextAlignment;
import ch.unibas.medizin.dynamicreports.report.constant.VerticalTextAlignment;
import java.awt.Color;

public class Templates {

    public static final StyleBuilder boldStyle;
    public static final StyleBuilder boldCenteredStyle;
    public static final StyleBuilder columnTitleStyle;

    static {

        boldStyle = stl.style().bold();

        boldCenteredStyle = stl.style(boldStyle)
                .setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE);


        columnTitleStyle = stl.style(boldCenteredStyle)
                .setBorder(stl.pen1Point())
                .setBackgroundColor(Color.LIGHT_GRAY);

    }
}