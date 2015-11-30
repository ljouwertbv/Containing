package nhl.containing.managmentinterface.navigationdrawer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nhl.containing.managmentinterface.data.ContainerProtos.*;
import nhl.containing.managmentinterface.data.ContainerArrayAdapter;
import nhl.containing.managmentinterface.communication.Communicator;
import nhl.containing.networking.protobuf.appDataProto;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ContainersFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    private ContainerArrayAdapter items;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContainersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<appDataProto.ContainerDataListItem> list = new ArrayList<>();
        items = new ContainerArrayAdapter(getActivity(),list);
        setListAdapter(items);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(items.getItem(position).getID());
        }
    }

    public void setData()
    {
        //make instruction
    }

    public void UpdateGraph(appDataProto.datablockApp block)
    {
        if(!block.getItemsList().isEmpty())
        {
            updateList(block.getItemsList());
        }
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e){}
    }


    private void updateList(final List<appDataProto.ContainerDataListItem> listItems)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                items.Update(listItems);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(int id);
    }

}
