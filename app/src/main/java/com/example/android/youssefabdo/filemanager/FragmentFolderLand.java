package com.example.android.youssefabdo.filemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.android.youssefabdo.filemanager.R.id.gridView;

/**
 * Created by EngYo on 06-Sep-17.
 */

public class FragmentFolderLand extends Fragment {

    private String mRoot = Environment.getExternalStorageDirectory().getPath();
    private String mRoot_;
    String mCurrentDirectory;
    ArrayList<String> mItem;
    ArrayList<String> mPath;
    ArrayList<String> mFiles;
    ArrayList<String> mFilesPath;
    Boolean mIsRoot = true;
    ListAdapter mListAdapter;
    ActionMode mActionMode;
    GridView mGridView;
    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;
    String mDefaultFolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_fragment_land, container, false);

        setHasOptionsMenu(true);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mDefaultFolder = sharedPrefs.getString(
                getString(R.string.settings_default_folder_key),
                getString(R.string.settings_default_folder_default));
        mRoot_ = mRoot + mDefaultFolder;
        File mCheckfile = new File(mRoot_);
        if (mCheckfile.exists()) {
            DefaultFolderAsyncTask task = new DefaultFolderAsyncTask();
            task.execute();
        }
        else {
            Toast.makeText(getActivity(), "Wrong Path: Double check Default Folder Setting", Toast.LENGTH_SHORT).show();
        }

//        sharedPrefs.registerOnSharedPreferenceChangeListener(listener);
        setRetainInstance(true);
        return rootView;
    }

//    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//            // listener implementation
//        }
//    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmptyStateTextView = (TextView) getView().findViewById(R.id.empty_view_grid);
        mGridView = (GridView) getView().findViewById(gridView);
        mGridView.setEmptyView(mEmptyStateTextView);
        mEmptyStateTextView.setText(R.string.wrong_path);

        mGridView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                File mIsFile = new File(mPath.get(position));
                if (mIsFile.isDirectory()) {
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.move);
                    view.startAnimation(animation);
                    getDirFromRoot(mIsFile.toString());
                } else {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(mIsFile);
                    String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                            MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                    intent.setDataAndType(uri, type == null ? "*/*" : type);
                    startActivity((Intent.createChooser(intent,
                            getString(R.string.open_using))));
                }
            }
        } );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_refresh) {
            File mCheckfile = new File(mRoot_);
            if (mCheckfile.exists()) {
                getDirFromRoot(mRoot_);
            }
            else {
                Toast.makeText(getActivity(), "Wrong Path: Please Double check Default Folder Setting", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //get directories and files from selected path
    public void getDirFromRoot(String p_rootPath) {
        mItem = new ArrayList<String>();
        mIsRoot = true;
        mPath = new ArrayList<String>();
        mFiles = new ArrayList<String>();
        mFilesPath = new ArrayList<String>();
        File mFile = new File(p_rootPath);
        File[] mFilesArray = mFile.listFiles();
        if (!p_rootPath.equals(mRoot)) {
            mItem.add("../");
            mPath.add(mFile.getParent());
            mIsRoot = false;
        }
        mCurrentDirectory = p_rootPath;
        //sorting file list in alphabetical order
        Arrays.sort(mFilesArray);
        for (int i = 0; i < mFilesArray.length; i++) {
            File file = mFilesArray[i];
            if (file.isDirectory()) {
                mItem.add(file.getName());
                mPath.add(file.getPath());
            } else {
                mFiles.add(file.getName());
                mFilesPath.add(file.getPath());
            }
        }
        for (String m_AddFile : mFiles) {
            mItem.add(m_AddFile);
        }
        for (String m_AddPath : mFilesPath) {
            mPath.add(m_AddPath);
        }
        mListAdapter = new ListAdapter(getActivity(), mItem, mPath, mIsRoot);
        mGridView.setAdapter(mListAdapter);
    }

    //Method to delete selected files
    void deleteFile() {
        for (int m_delItem : mListAdapter.mSelection.keySet()) {
            Boolean result = mListAdapter.mSelection.get(m_delItem);
            if (result) {
                File m_delFile = new File(mPath.get(m_delItem));
                boolean m_isDelete = false;
                if (m_delFile.isDirectory()) {
                    for (File child : m_delFile.listFiles())
                        child.delete();
                    m_delFile.delete();
                }
                else {
                    m_isDelete = m_delFile.delete();
                }
                if (m_isDelete) {
                    Toast.makeText(getActivity(), "File(s)/Folder(s) Deleted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Can not Delete File(s)/Folder(s)", Toast.LENGTH_SHORT).show();

                }
            }
        }
        getDirFromRoot(mCurrentDirectory);
    }

    private class DefaultFolderAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getDirFromRoot(mRoot_);
            return null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mGridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {

                if (checked) {
                    nr++;
                    mListAdapter.setNewSelection(position, checked);
                } else {
                    nr--;
                    mListAdapter.removeSelection(position);
                }
                mode.setTitle(nr + " Selected");
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.delete:
//                        deleteSelectedItems();
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setIcon(R.drawable.ic_delete_forever_black_24dp);
                        alert.setTitle("Delete");
                        alert.setMessage("Are you sure you want to delete selected items?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                deleteFile();
                                nr = 0;
                                mListAdapter.clearSelection();
                                dialog.dismiss();
                            }
                        });

                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListAdapter.clearSelection();
                                dialog.dismiss();
                            }
                        });

                        alert.show();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                nr = 0;
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.cab, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Toast.makeText(getActivity(), "On long click listener", Toast.LENGTH_LONG).show();
                if (mActionMode != null) {
                    return false;
                }
                mGridView.setItemChecked(position, !mListAdapter.isPositionChecked(position));
                return false;
            }
        });
    }
}