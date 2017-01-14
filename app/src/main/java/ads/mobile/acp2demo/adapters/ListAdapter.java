package ads.mobile.acp2demo.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import ads.mobile.acp2demo.classes.AppInfo;
import ads.mobile.acp2demo.R;
import ads.mobile.acp2demo.databinding.AppListElementBinding;

public class ListAdapter extends BaseAdapter {

    private ObservableArrayList<AppInfo> list;
    private LayoutInflater inflater;

    public ListAdapter(ObservableArrayList<AppInfo> l) {
        list = l;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        AppListElementBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.app_list_element, parent, false);
        binding.setAppInfo(list.get(position));

        return binding.getRoot();
    }


}