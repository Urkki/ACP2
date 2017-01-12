package ads.mobile.acp2demo.binders;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.widget.ListView;

import ads.mobile.acp2demo.classes.AppInfo;
import ads.mobile.acp2demo.adapters.ListAdapter;

public class ListBinder {

//    @BindingAdapter("bind:imageRes")
//    public static void bindImage(ImageView view, int r) {
//        view.setImageResource(r);
//    }

    @BindingAdapter("bind:items")
    public static void bindList(ListView view, ObservableArrayList<AppInfo> list) {
        ListAdapter adapter = new ListAdapter(list);
        view.setAdapter(adapter);
    }
}
