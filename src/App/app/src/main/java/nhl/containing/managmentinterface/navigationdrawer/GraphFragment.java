package nhl.containing.managmentinterface.navigationdrawer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.syncfusion.charts.*;
import com.syncfusion.charts.enums.*;

import java.util.List;

import nhl.containing.managmentinterface.R;
import nhl.containing.managmentinterface.communication.Communicator;

/**
 * Fragment for showing the Graph
 */
public class GraphFragment extends Fragment {

    private SfChart chart;
    private int graphID;
    private ObservableArrayList list;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        if(getArguments() != null)
        {
            graphID = getArguments().getInt("graphID");
            return;
        }
        graphID = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.graphfragment, container, false);
        chart = (SfChart)view.findViewById(R.id.graph);
        //setUpGraph(graph);
        setupChart();
        setData();
        return view;
    }

    /**
     * Setting up the graph/ chart
     */
    private void setupChart()
    {
        list = new ObservableArrayList();
        if(graphID == 0)
        {
            list.add(new ChartDataPoint("Tra", 0));
            list.add(new ChartDataPoint("Tru", 0));
            list.add(new ChartDataPoint("Sea", 0));
            list.add(new ChartDataPoint("Inl", 0));
            list.add(new ChartDataPoint("Sto", 0));
            list.add(new ChartDataPoint("AGV", 0));
            list.add(new ChartDataPoint("Rem", 0));
            PieSeries pieSeries = new PieSeries();
            pieSeries.getDataMarker().setShowLabel(true);
            pieSeries.getDataMarker().setLabelContent(LabelContent.YValue);
            pieSeries.getDataMarker().getLabelStyle().setBackgroundColor(Color.TRANSPARENT);
            pieSeries.setDataSource(list);
            chart.getSeries().add(pieSeries);
            chart.getLegend().setVisibility(Visibility.Visible);
            chart.getLegend().setDockPosition(ChartDock.Right);
            chart.getLegend().setOrientation(Orientation.Vertical);
        }
        else
        {
            ColumnSeries seriesChart = new ColumnSeries();
            list.add(new ChartDataPoint("Tra",0));
            list.add(new ChartDataPoint("Tru",0));
            list.add(new ChartDataPoint("Sea",0));
            list.add(new ChartDataPoint("Inl",0));
            seriesChart.setDataSource(list);
            seriesChart.getDataMarker().setShowLabel(true);
            seriesChart.getDataMarker().getLabelStyle().setLabelPosition(DataMarkerLabelPosition.Inner);
            chart.getSeries().add(seriesChart);
            chart.setPrimaryAxis(new CategoryAxis());
            NumericalAxis test = new NumericalAxis();
            test.setInterval(10);
            chart.setSecondaryAxis(test);
        }
    }

    /**
     * Update the Graph / chart
     * @param data update data
     */
    private void updateChart(final List<Integer> data)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.clear();
                if (graphID == 0) {
                    list.add(new ChartDataPoint("Train", data.get(0)));
                    list.add(new ChartDataPoint("Truck", data.get(1)));
                    list.add(new ChartDataPoint("Seaship", data.get(2)));
                    list.add(new ChartDataPoint("Inline Ship", data.get(3)));
                    list.add(new ChartDataPoint("Storage", data.get(4)));
                    list.add(new ChartDataPoint("AGV", data.get(5)));
                    list.add(new ChartDataPoint("Remainer", data.get(6)));
                } else {
                    list.add(new ChartDataPoint("Train", data.get(0)));
                    list.add(new ChartDataPoint("Truck", data.get(1)));
                    list.add(new ChartDataPoint("Seaship", data.get(2)));
                    list.add(new ChartDataPoint("Inline Ship", data.get(3)));
                }
            }
        });
    }

    /**
     * Gets new data and starts updating the graph
     */
    public void setData()
    {
        try{
            updateChart(Communicator.getData(graphID));
        }
        catch (Exception e){
            try{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (Exception e1){}
        }
    }

    /**
     * Returns the id of the graph
     * @return graphid
     */
    public int getGraphID()
    {
        return graphID;
    }




}
