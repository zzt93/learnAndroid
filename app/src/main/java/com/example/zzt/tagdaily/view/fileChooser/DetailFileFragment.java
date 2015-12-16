package com.example.zzt.tagdaily.view.fileChooser;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.example.zzt.tagdaily.R;

import java.util.ArrayList;
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
public class DetailFileFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private AbsListView mListView;
    /**
     * An easy adapter to map static data to views defined in an XML file. You can specify the data
     * backing the list as an ArrayList of Maps. Each entry in the ArrayList corresponds to one row
     * in the list. The Maps contain the data for each row. You also specify an XML file that
     * defines the views used to display the row, and a mapping from keys in the Map to specific
     * views.
     */
    private SimpleAdapter mAdapter;

    private DetailFragmentInteractionListener mListener;
    private List<Map<String, String>> fileList = new ArrayList<>();
    private String fatherName;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param UIFileInfos Parameter 1.
     * @param fatherName      father folder name
     * @return A new instance of fragment DetailFileFragment.
     */
    public static DetailFileFragment newInstance(ArrayList<UIFileInfo> UIFileInfos, String fatherName) {
        DetailFileFragment fragment = new DetailFileFragment();
        Bundle args = new Bundle();

        fragment.fatherName = fatherName;
        fragment.setArguments(args);

        for (UIFileInfo UIFileInfo : UIFileInfos) {
            fragment.fileList.add(UIFileInfo.convertFileMap());
        }
        return fragment;
    }

    public DetailFileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            using `String[i]` as index, to find resource in `List<Map<String, >>`
            to fit into `R.id.xxx`
         */
        mAdapter = new SimpleAdapter(getActivity(),
                fileList,
                R.layout.file_info,
                new String[]{UIFileInfo.NAME, UIFileInfo.LAST_MODIFIED},
                new int[]{R.id.file_title, R.id.file_info});
    }

//    private static Map<String, String> makeMap(String text, String text2) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put(TITLE, text);
//        map.put(INFO, text2);
//        return map;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_file, container, false);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        View empty = view.findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);
        if (fileList.isEmpty()) {
            setEmptyVisibility(View.VISIBLE);
            setEmptyText(fatherName);
        }
        // Set data adapter for list view, ie associate data with view
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        return view;
    }

    private void setEmptyVisibility(int visible) {
        TextView emptyView = (TextView) mListView.getEmptyView();
        emptyView.setVisibility(visible);
    }

    private void setEmptyText(String fatherName) {
        TextView emptyView = (TextView) mListView.getEmptyView();
        emptyView.setText(String.format(getResources().getString(R.string.empty_list_msg), fatherName));
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
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.detailFragmentClick(position);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.detailFragmentLongPress(position);
        }
        return true;
    }


    /**
     * xxxListView() method -- all for update data in the this file fragment
     * which will be invoked by this fragment's container
     */

    public DetailFileFragment clearListView() {
        this.fileList.clear();
        return this;
    }

    public DetailFileFragment addListView(ArrayList<UIFileInfo> fileList) {
        for (UIFileInfo UIFileInfo : fileList) {
            this.fileList.add(UIFileInfo.convertFileMap());
        }
        return this;
    }

    public void notifyDataSetChanged(String fatherName) {
        if (fileList.isEmpty()) {
            setEmptyVisibility(View.VISIBLE);
            setEmptyText(fatherName);
        } else {
            setEmptyVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
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
        void detailFragmentClick(int position);

        void detailFragmentLongPress(int position);
    }

}
