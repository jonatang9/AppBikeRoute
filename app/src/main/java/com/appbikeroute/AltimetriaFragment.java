package com.appbikeroute;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class AltimetriaFragment extends Fragment {

    private CombinedChart chart;
    int p2;
    double [] distotal, desnivel,pendiente;

    public AltimetriaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AltimetriaFragment altimetriaFragment = new AltimetriaFragment();
        Bundle args = new Bundle();
        args.putInt("p2",p2);
        args.putDoubleArray("distotal",distotal);
        args.putDoubleArray("desnivel",desnivel);
        args.putDoubleArray("pendiente",pendiente);
        altimetriaFragment.setArguments(args);

        if (getArguments() != null){
            p2 =getArguments().getInt("p2");
            distotal= getArguments().getDoubleArray("distotal");
            desnivel=getArguments().getDoubleArray("desnivel");
            pendiente=getArguments().getDoubleArray("pendiente");

        }
        //Toast.makeText(getActivity().getApplicationContext(), "Entro  p2: "+p2, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_altimetria, container, false);



        insetarDatosEnGrafica(view);
        return view;
    }

    private void insetarDatosEnGrafica(View view) {
        ////////////////////////////////grafica

        chart = view.findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.GRAY);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        /*
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE,
                CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

         */
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{ CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BUBBLE });

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);


        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);

        //rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        //leftAxis.setAxisMaximum(2);
        //leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        //xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        String[] distt = new String[p2];
        for (int i=0; i<p2; i++ ) {
            distt[i]= String.format("%.2f",distotal[i]);
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(distt));


        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBarData());
        data.setData(generateBubbleData());
        //data.setValueFormatter(new DefaultAxisValueFormatter(2));
        xAxis.setAxisMaximum(data.getXMax());

        chart.setData(data);
        chart.invalidate();
        chart.animateX(3000);
        chart.setVisibleXRangeMaximum(5000);
        chart.getXAxis().setAxisMinimum(0.0f);
        chart.getAxisLeft().setCenterAxisLabels(true);
        chart.getAxisLeft().setAxisMaximum(generateLineData().getYMax());
        chart.getAxisLeft().setAxisMinimum(generateLineData().getYMin());
        chart.getAxisLeft().setValueFormatter(new DefaultAxisValueFormatter(2));
        chart.getAxisRight().setAxisMaximum(generateBarData().getYMax());
        chart.getAxisRight().setAxisMinimum(generateBarData().getYMin());
        chart.getAxisRight().setValueFormatter(new DefaultAxisValueFormatter(2));//new PercentFormatter());

        //chart.getAxisLeft().setAxisMinimum(-1.0f);



    }

    private LineData generateLineData() {


        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entries2 = new ArrayList<>();
        for (int index = 0; index < p2; index++){
            //entries.add(new Entry(index + 0.5f, getRandom(15, 5)));
            //float temp = Float.parseFloat(String.format("%.2f",desnivel[index]));
            entries.add(new Entry(index,(float)desnivel[index]));
            entries2.add(new Entry(index,(float) pendiente[index]));
        }


        LineDataSet set = new LineDataSet(entries, "Desnivel(m)");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineDataSet set2 = new LineDataSet(entries, "Pendiente(m)");
        set2.setColor(Color.rgb(61, 165, 255));
        set2.setLineWidth(2.5f);
        set2.setCircleColor(Color.rgb(61, 165, 255));
        set2.setCircleRadius(5f);
        set2.setFillColor(Color.rgb(61, 165, 255));
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //set.setDrawValues(true);
        set2.setValueTextSize(10f);
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);


        LineData d = new LineData();
        d.addDataSet(set);

        return d;
    }


    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<>();

        for (int index = 0; index < p2; index++) {
            //entries1.add(new BarEntry(index+0.01f, getRandom( 0.1f, 5.2f)));
            float l = 5;
            entries1.add(new BarEntry(index,(float) l));//altitud[index]));

        }

        BarDataSet set1 =  new BarDataSet(entries1, "Altitud(m)");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10);

        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);


        BarData d = new BarData();//(set1, set2);
        d.setBarWidth(10);
        d.addDataSet(set1);


        return d;
    }

    private BubbleData generateBubbleData() {

        BubbleData bd = new BubbleData();

        ArrayList<BubbleEntry> entries = new ArrayList<>();

        for (int index = 0; index < p2; index++) {
            float size = (float)pendiente[index];//getRandom(10, 105);
            float y = 100;//getRandom(100, 105);
            entries.add(new BubbleEntry(index, y, size));
        }


        BubbleDataSet set = new BubbleDataSet(entries, "Bubble DataSet");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setValueFormatter(new PercentFormatter());
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.WHITE);
        set.setHighlightCircleWidth(10f);
        set.setDrawValues(true);
        bd.addDataSet(set);

        return bd;
    }

}