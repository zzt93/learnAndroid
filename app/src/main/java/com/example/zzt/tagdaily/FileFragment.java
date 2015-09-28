package com.example.zzt.tagdaily;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.zzt.tagdaily.dummy.DummyContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FileFragment extends ListFragment implements AbsListView.OnItemClickListener {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam2 = "";

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private AbsListView secListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static FileFragment newInstance(String param1, String param2) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


//        mAdapter = new ArrayAdapter<>(getActivity(),
//                R.layout.with_icon, R.id.label, DummyContent.ITEMS);
//        mAdapter = new ArrayAdapter<>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
        List<Map<String, String>> infos = new ArrayList<>();
        infos.add(makeMap(R.mipmap.ic_launcher, "music", "music sub1"));
        infos.add(makeMap(R.mipmap.ic_launcher, "video", "video sub2"));

        /*
            using `String[i]` as index, to find resource in `List<Map<String, >>`
            to fit into `R.id.xxx`
         */
        mAdapter = new SimpleAdapter(getActivity(), infos, R.layout.with_icon,
                new String[]{"logo", "label", "label2"}, new int[]{R.id.logo, R.id.label, R.id.label2});
    }

    private HashMap<String, String> makeMap(int id, String text, String text2) {
        HashMap<String, String> map = new HashMap<>();
        map.put("logo", "" + id);
        map.put("label", text);
        map.put("label2", text2);
        return map;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);

        // Set first the data adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        // TODO not show at the same time
//        secListView = (AbsListView) view.findViewById(android.R.id.empty);
//        ((AdapterView<ListAdapter>) secListView).setAdapter(mAdapter);


        return view;
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
//        super.onListItemClick(l, v, position, id);
        System.out.println(mParam2);
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
        void onFragmentInteraction(String id);
    }

}
