package com.example.skillshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Polar;
import com.anychart.data.Mapping;
import com.anychart.enums.PolarSeriesType;
import com.anychart.data.Set;

import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.ScaleTypes;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.scales.Linear;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;

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

        final int[] check = {0};
        Query parseQueryTaking = new Query();
        parseQueryTaking.getClassesTaking();

        parseQueryTaking.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> takingWorkshops, ParseException e) {
                if (e == null) {
                   takingCategories[0] = countCategories(takingWorkshops);


                    Query parseQueryTeaching = new Query();
                    parseQueryTeaching.getClassesTeaching();

                    parseQueryTeaching.findInBackground(new FindCallback<Workshop>() {
                        @Override
                        public void done(List<Workshop> teachingWorkshops, ParseException e) {
                            if (e == null) {
                                teachingCategories[0] = countCategories(teachingWorkshops);

                                data.add(new CustomDataEntry("Culinary", teachingCategories[0][0], takingCategories[0][0]));
                                data.add(new CustomDataEntry("Education", teachingCategories[0][1], takingCategories[0][1]));
                                data.add(new CustomDataEntry("Fitness", teachingCategories[0][2], takingCategories[0][2]));
                                data.add(new CustomDataEntry("Arts/Crafts", teachingCategories[0][3], takingCategories[0][3]));
                                data.add(new CustomDataEntry("Other", teachingCategories[0][4], takingCategories[0][4]));
                                Set set = Set.instantiate();
                                set.data(data);
                                Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
                                Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");

                                polar.column(series1Data);

                                polar.column(series2Data);


                                polar.title("Skill Graph");

                                polar.sortPointsByX(true)
                                        .defaultSeriesType(PolarSeriesType.COLUMN)
                                        .yAxis(false)
                                        .xScale(ScaleTypes.ORDINAL);

                                polar.title().margin().bottom(20d);

                                ((Linear) polar.yScale(Linear.class)).stackMode(ScaleStackMode.VALUE);

                                polar.tooltip()
                                        .displayMode(TooltipDisplayMode.UNION);


                                polar.palette(new String[] { "#0288d1", "#d4e157", "#ff6e40", "#f8bbd0" });
                                anyChartView.setChart(polar);

                            } else {
                                e.printStackTrace();
                            }
                        }
                    });



                } else {
                    e.printStackTrace();
                }
            }
        });












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
