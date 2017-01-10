package ads.mobile.acp2demo;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.widget.ImageView;
import android.widget.ListView;

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
