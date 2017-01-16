package ads.mobile.acp2demo.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import ads.mobile.acp2demo.R;
import ads.mobile.acp2demo.services.AppCheckerService;

public class AdDialogActivity extends AppCompatActivity {
    private static final String TAG = AdDialogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActionBar actionBar = getSupportActionBar();
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 0);
//        actionBar.hide();

        //Hide Titlebar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //Modal dialog.
        this.setFinishOnTouchOutside(false);

        setContentView(R.layout.ad_dialog);

        ImageView image = (ImageView) findViewById(R.id.closeImageView);

        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAdTerminatedSignal();
                finish();
            }
        });
    }

    public void sendAdTerminatedSignal(){
        Intent i = new Intent(AppCheckerService.AD_TERMINATED);
        i.putExtra("reason", "dno");
        sendBroadcast(i);
        Log.d(TAG, "Ad STOP broadcast is sended.");
    }
}
