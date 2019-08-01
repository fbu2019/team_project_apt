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
        final Integer[][] takingCategories = new Integer[1][5];
        final Integer[][] teachingCategories = new Integer[1][5];
        Polar polar = AnyChart.polar();

        List<DataEntry> data = new ArrayList<>();

        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<Integer> skillsData = (ArrayList<Integer>) currentUser.get("skillsData");

        data.add(new CustomDataEntry("Culinary", skillsData.get(0),  skillsData.get(5)));
        data.add(new CustomDataEntry("Education",  skillsData.get(1),  skillsData.get(6)));
        data.add(new CustomDataEntry("Fitness",  skillsData.get(2),  skillsData.get(7)));
        data.add(new CustomDataEntry("Arts/Crafts",  skillsData.get(3),  skillsData.get(8)));
        data.add(new CustomDataEntry("Other",  skillsData.get(4),  skillsData.get(9)));


        Set set = Set.instantiate();
        set.data(data);
        Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");

                              /*  OrdinalColor scaleBarColorScale = OrdinalColor.instantiate();
                                scaleBarColorScale.ranges(new String[]{
                                        "{ from: 0, to: 1, color: ['red 0.5'] }",
                                        "{ from: 1, to: 3, color: ['yellow 0.5'] }",
                                        "{ from: 3, to: 7, color: ['green 0.5'] }",
                                        "{ from: 7, to: 8, color: ['yellow 0.5'] }",
                                        "{ from: 8, to: 10, color: ['red 0.5'] }"
                                });*/
        //  Column series1 = polar.column(series1Data);

        //   series1.colorScale(scaleBarColorScale);
        //    Column series2 = polar.column(series2Data);
        // series2.colorScale(scaleBarColorScale);
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

    private Integer[]  countCategories(List<Workshop> workshops) {
        Integer[] categoryCount = Collections.nCopies(5, 0).toArray(new Integer[0]);
        for (int i = 0; i < workshops.size(); i++){
            if (workshops.get(i).getCategory().equals("Culinary")){
                categoryCount[0]++;

            }
            if (workshops.get(i).getCategory().equals("Education")){
                categoryCount[1]++;

            }
            if (workshops.get(i).getCategory().equals("Fitness")){
                categoryCount[2]++;


            }
            if (workshops.get(i).getCategory().equals("Arts/Crafts")){
                categoryCount[3]++;

            }
            if (workshops.get(i).getCategory().equals("Other")){
                categoryCount[4]++;
            }
        }
       return categoryCount;



    }

    private void countTeachingCategories() {

    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }
    }
}
