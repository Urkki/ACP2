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
import ads.mobile.acp2demo.databinding.AppListElementLayoutBinding;

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
        AppListElementLayoutBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.app_list_element_layout, parent, false);
        binding.setAppInfo(list.get(position));

        return binding.getRoot();

//        View v = convertView;
//
//        if (v == null) {
//            LayoutInflater vi;
//            vi = LayoutInflater.from(getContext());
//            v = vi.inflate(R.layout.app_list_element_layout, null);
//
//        }
//
//        AppInfo p = getItem(position);
//
//        if (p != null) {
//            TextView tt1 = (TextView) v.findViewById(R.id.appName);
//            CheckBox cbx = (CheckBox) v.findViewById(R.id.cbx);
////            if (tt1 != null) {
////                tt1.setText(p.getAppName());
////            }
////            if (cbx != null) {
////                cbx.setChecked(p.getIsUsed());
////            }
//
//        }

//        return v;
    }


}