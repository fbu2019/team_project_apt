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
import java.util.List;


public class SkillVisualizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_visualization);

        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Polar polar = AnyChart.polar();

        List<DataEntry> data = new ArrayList<>();

  //

        String[] categoryArray= {"Culinary", "Education", "Fitness", "Arts/Crafts", "Other"};
        for (String category:categoryArray){
            final int[] teachTake = new int[2];
            Query parseQuery = new Query();
            ArrayList<String> singletonCategory = new ArrayList<String>();
            singletonCategory.add("Culinary");
            parseQuery.getClassesTaking().byCategory(singletonCategory);
            parseQuery.findInBackground(new FindCallback<Workshop>() {
                @Override
                public void done(List<Workshop> objects, ParseException e) {
                    if (e == null) {
                        teachTake[0] = objects.size();
                    } else {
                        e.printStackTrace();
                    }
                }
            });

            parseQuery.getClassesTeaching().byCategory(singletonCategory);
            parseQuery.findInBackground(new FindCallback<Workshop>() {
                @Override
                public void done(List<Workshop> objects, ParseException e) {
                    if (e == null) {
                        teachTake[1] = objects.size();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
            data.add(new CustomDataEntry(singletonCategory.get(0), 12814, 4376));
        }


    //
        data.add(new CustomDataEntry("Culinary", 12814, 4376));
        data.add(new CustomDataEntry("Education", 13012, 3987));
        data.add(new CustomDataEntry("Fitness", 11624, 3574 ));
        data.add(new CustomDataEntry("Arts/Crafts", 8814, 4376));
        data.add(new CustomDataEntry("Other", 12998, 4572 ));





//
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

        anyChartView.setChart(polar);
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }
    }
}
