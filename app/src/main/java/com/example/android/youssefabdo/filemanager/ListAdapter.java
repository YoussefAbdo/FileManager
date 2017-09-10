package com.example.android.youssefabdo.filemanager;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by EngYo on 04-Sep-17.
 */

public class ListAdapter extends BaseAdapter {

    public HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    private List<String> mItem;
    private List<String> mPath;
    Context mContext;
    Boolean mIsRoot;
    public ListAdapter(Context pContext,List<String> pItem, List<String> pPath,Boolean pIsRoot) {
        mContext=pContext;
        mItem=pItem;
        mPath=pPath;
        mIsRoot=pIsRoot;
    }

    @Override
    public int getCount() {
        return mItem.size();
    }

    @Override
    public Object getItem(int position) {
        return mItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int pPosition, View pConvertView, ViewGroup pParent)
    {
        View mView = null;
        ViewHolder mViewHolder = null;
        if (pConvertView == null)
        {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            mView = mInflater.inflate(R.layout.list_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.mFileNameTextView = (TextView) mView.findViewById(R.id.file_name_text_view);
            mViewHolder.mDateTextView = (TextView) mView.findViewById(R.id.date_text_view);
            mViewHolder.mIconImageView = (ImageView) mView.findViewById(R.id.icon_img_file);
            mView.setTag(mViewHolder);
        }
        else
        {
            mView = pConvertView;
            mViewHolder = ((ViewHolder) mView.getTag());
        }

        mViewHolder.mFileNameTextView.setText(mItem.get(pPosition));
        mViewHolder.mIconImageView.setImageResource(setFileImageType(new File(mPath.get(pPosition))));
        mViewHolder.mDateTextView.setText(getLastDate(pPosition));
        return mView;
    }

    class ViewHolder
    {
        ImageView mIconImageView;
        TextView mFileNameTextView;
        TextView mDateTextView;
    }

    private int setFileImageType(File mFile)
    {
        int mLastIndex=mFile.getAbsolutePath().lastIndexOf(".");
        String mFilepath=mFile.getAbsolutePath();
        if (mFile.isDirectory())
            return R.drawable.ic_folder_black_24dp;
        else
        {
            if(mLastIndex == -1)
            {
                return R.drawable.ic_insert_drive_file_black_24dp;
            }
            else if(mFilepath.substring(mLastIndex).equalsIgnoreCase(".png"))
            {
                return R.drawable.ic_photo_black_24dp;
            }
            else if(mFilepath.substring(mLastIndex).equalsIgnoreCase(".jpg"))
            {
                return R.drawable.ic_photo_black_24dp;
            }
            else if(mFilepath.substring(mLastIndex).equalsIgnoreCase(".mp4"))
            {
                return R.drawable.ic_video_label_black_24dp;
            }
            else if(mFilepath.substring(mLastIndex).equalsIgnoreCase(".mp3"))
            {
                return R.drawable.ic_audiotrack_black_24dp;
            }
            else if(mFilepath.substring(mLastIndex).equalsIgnoreCase(".pdf"))
            {
                return R.drawable.ic_library_books_black_24dp;
            }
            else
            {
                return R.drawable.ic_insert_drive_file_black_24dp;
            }
        }
    }

    String getLastDate(int pPos)
    {
        File mFile=new File(mPath.get(pPos));
        SimpleDateFormat mDateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return mDateFormat.format(mFile.lastModified());
    }

    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }
}
