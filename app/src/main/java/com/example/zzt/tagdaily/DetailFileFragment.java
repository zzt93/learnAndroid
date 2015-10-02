package com.example.zzt.tagdaily;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.example.zzt.tagdaily.dummy.DummyContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFileFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String TITLE = "title";
    public static final String INFO = "info";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private AbsListView mListView;
    private SimpleAdapter mAdapter;

    private DetailFragmentInteractionListener mListener;
    private List<Map<String, String>> fileList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFileFragment.
     */
    public static DetailFileFragment newInstance(String param1, String param2) {
        DetailFileFragment fragment = new DetailFileFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    public DetailFileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileList = new ArrayList<>();
        fileList.add(makeMap("music", "music sub1"));
        fileList.add(makeMap("video", "video sub2"));

        /*
            using `String[i]` as index, to find resource in `List<Map<String, >>`
            to fit into `R.id.xxx`
         */
        mAdapter = new SimpleAdapter(getActivity(),
                fileList,
                R.layout.file_info,
                new String[]{TITLE, INFO},
                new int[]{R.id.file_title, R.id.file_info});
    }

    private static Map<String, String> makeMap(String text, String text2) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TITLE, text);
        map.put(INFO, text2);
        return map;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_file, container, false);
        // Set data adapter for list view, ie associate data with view
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DetailFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FolderFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fileList.add(makeMap("music", "music sub1"));
        mAdapter.notifyDataSetChanged();
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.detailFragmentClick(fileList.get(position).get(TITLE));
        }
    }

    public void updateListView(ArrayList fileList) {

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
    public interface DetailFragmentInteractionListener {
        // TODO: Update argument type and name
        void detailFragmentClick(String id);
    }

}
