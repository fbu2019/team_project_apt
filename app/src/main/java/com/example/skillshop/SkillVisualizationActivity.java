package com.example.skillshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Polar;
import com.anychart.core.polar.series.Column;
import com.anychart.data.Mapping;
import com.anychart.enums.PolarSeriesType;
import com.anychart.data.Set;

import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.ScaleTypes;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.scales.Linear;
import com.anychart.scales.OrdinalColor;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SkillVisualizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_visualization);
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));
        Polar polar = AnyChart.polar();

        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<Integer> skillsData = (ArrayList<Integer>) currentUser.get("skillsData");

        List<DataEntry> data = new ArrayList<>();
        data.add(new CustomDataEntry("Culinary", skillsData.get(0),  skillsData.get(5)));
        data.add(new CustomDataEntry("Education",  skillsData.get(1),  skillsData.get(6)));
        data.add(new CustomDataEntry("Fitness",  skillsData.get(2),  skillsData.get(7)));
        data.add(new CustomDataEntry("Arts/Crafts",  skillsData.get(3),  skillsData.get(8)));
        data.add(new CustomDataEntry("Other",  skillsData.get(4),  skillsData.get(9)));


        Set set = Set.instantiate();
        set.data(data);
        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");

        polar.column(series1Data);
        polar.column(series2Data);

        // set the inner radius
        polar.innerRadius(50);
        polar.title("Skill Graph");

        polar.sortPointsByX(true)
                .defaultSeriesType(PolarSeriesType.COLUMN)
                .yAxis(false)
                .padding("20")
                .xScale(ScaleTypes.ORDINAL);

        polar.title().margin().bottom(20d);

        ((Linear) polar.yScale(Linear.class)).stackMode(ScaleStackMode.VALUE);

        polar.tooltip()
                .displayMode(TooltipDisplayMode.UNION);

        anyChartView.setChart(polar);
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }
    }
}
