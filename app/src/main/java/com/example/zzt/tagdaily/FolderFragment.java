package com.example.zzt.tagdaily;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.zzt.tagdaily.logic.FileInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link FolderFragmentInteractionListener}
 * interface.
 */
public class FolderFragment extends Fragment implements AbsListView.OnItemClickListener {


    private FolderFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
//    private AbsListView secListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SimpleAdapter mAdapter;
    private List<Map<String, String>> category = new ArrayList<>();
    private int layoutId;
    private String[] names;
    private int[] ids;

    public static FolderFragment newInstance(ArrayList<FileInfo> fileInfos, int layoutId,
                                             String[] names, int[] ids) {
        FolderFragment fragment = new FolderFragment();
        Bundle args = new Bundle();
        fragment.layoutId = layoutId;
        fragment.names = names;
        fragment.ids = ids;

        for (FileInfo fileInfo : fileInfos) {
            fragment.category.add(fileInfo.convertFolderMap());
        }

        // give the data when onCreate() invoke need
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FolderFragment() {
    }

    /**
     * prepare data for the fragment to show
     *
     * @param savedInstanceState -- saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        init_category(category);
        /*
            using `String[i]` as index, to find resource in `List<Map<String, >>`
            to fit into `R.id.xxx`
         */
        mAdapter = new SimpleAdapter(getActivity(),
                category,
                layoutId,
                names,
                ids);

//        mAdapter = new ArrayAdapter<>(getActivity(),
//                R.layout.with_icon, R.id.label, DummyContent.ITEMS);
//        mAdapter = new ArrayAdapter<>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
    }


//    private void init_category(List<Map<String, String>> category) {
//        for (int i = 0; i < Category.numCategories(); i++) {
//            category.add(Category.makeMap(i));
//        }
//    }


    /**
     * set the relation between data and view for it to show
     * add listener
     *
     * @param inflater           -- which make view from xml
     * @param container          -- the ViewGroup to hold this view
     * @param savedInstanceState -- saved state
     * @return created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        // Set first the data adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        if (category.isEmpty()) {
            setEmptyText(getString(R.string.empty_list));
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FolderFragmentInteractionListener) activity;
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
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.folderFragmentClick(position);
        }
    }

    public SimpleAdapter addListView(ArrayList<FileInfo> files) {
        for (FileInfo file : files) {
            category.add(file.convertFolderMap());
        }
        return mAdapter;
    }

    public SimpleAdapter clearListView() {
        category.clear();
        return mAdapter;
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
    public interface FolderFragmentInteractionListener {
        void folderFragmentClick(int position);
    }

}
