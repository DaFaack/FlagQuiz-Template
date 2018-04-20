package com.nileworx.flagsquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


public class ShopActivity extends Activity {
    SaveData sv = new SaveData();

    //VideoAD
    Button btn_videoAd;
    private RewardedVideoAd mRewardedVideoAd;


    //rate App
    Button btn_rate_app;
    ConnectionDetector cd;
    TextView rateText;




    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor e;

    String marketLink = "https://play.google.com/store/apps/details?id=com.engahmedgalal.successquotes";

    DAO db;
    Cursor c;

    UpdateClass update;
    SoundClass sou;
    CustomDialog dialog;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);


        //initialisation
        btn_videoAd = (Button)findViewById(R.id.btn_videoAd);
        btn_rate_app = (Button)findViewById(R.id.btn_rate_app);
        rateText = (TextView)findViewById(R.id.rateText);

        btn_videoAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoAd();
            }
        });


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
        loadRewardedVideoAd();

        btn_rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_rate_click();
            }
        });



        if(sv.getBoolean("rate_used", getApplicationContext())){
            btn_rate_app.setBackgroundResource(R.drawable.button_shop_diabled);
            rateText.setAlpha(0.5f);
        }



        dialog = new CustomDialog(ShopActivity.this);
        sou = new SoundClass(ShopActivity.this);

        AdView ad = (AdView) findViewById(R.id.adView);
        if (ad != null) {
            ad.loadAd(new AdRequest.Builder().build());
        }

        mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        e = mSharedPreferences.edit();

        db = new DAO(this);
        db.open();

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.titleBar);

        TextView title = (TextView) layout.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.shopTitle).toUpperCase());

        RelativeLayout scoreAndCoins = (RelativeLayout) layout.findViewById(R.id.scoreAndCoins);
        scoreAndCoins.setVisibility(View.GONE);




        ImageButton back = (ImageButton) layout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }

    // =========================================================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (!mSharedPreferences.getString("flagsNum", "0").equals("0")) {
            String updatesDlgMsg = String.format(getResources().getString(R.string.updatesDlg), mSharedPreferences.getString("flagsNum", "0"));
            dialog.showDialog(R.layout.blue_dialog, "updatesDlg", updatesDlgMsg, mSharedPreferences.getString("flagsJSON", ""));
            e.putString("flagsNum", "0");
            e.commit();
        }
    }

    // ==============================================================================

    private boolean MyStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }












    public void videoAd(){
        Log.e("###","videoAd");
        db.addTotalCoins(1000);

        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }else{
            Log.e("###","not loaded");
        }

        loadRewardedVideoAd();

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }


    public void btn_rate_click(){
        cd = new ConnectionDetector(ShopActivity.this);
        if(cd.isConnectingToInternet()&&!sv.getBoolean("rate_used", getApplicationContext())){
            Log.i("###","connected");

            Uri uriUrl = Uri.parse(getText(R.string.link_to_app).toString());
            Intent openUrl = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(openUrl);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btn_rate_app.setBackgroundResource(R.drawable.button_shop_diabled);
                    rateText.setAlpha(0.5f);

                    sv.saveBoolean("rate_used", true, getApplicationContext());

                    AlertDialog.Builder a_builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        a_builder = new AlertDialog.Builder(ShopActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        a_builder = new AlertDialog.Builder(ShopActivity.this);
                    }
                    a_builder.setMessage("Thanks for your rating, you received 200 coins !")
                            .setCancelable(true)
                            .setPositiveButton("thanks", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = a_builder.create();
                    alert.setTitle("well done");
                    alert.show();
                }
            }, 8000);



        }else if(!cd.isConnectingToInternet()&&!sv.getBoolean("rate_used", getApplicationContext())){
            AlertDialog.Builder a_builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                a_builder = new AlertDialog.Builder(ShopActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                a_builder = new AlertDialog.Builder(ShopActivity.this);
            }
            a_builder.setMessage("Please check your interent connection to be able to give a rating")
                    .setCancelable(true)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = a_builder.create();
            alert.setTitle("No internet connection");
            alert.show();
        }
        else{
            Toast.makeText(getApplicationContext(), "You can only submit a rating once", Toast.LENGTH_SHORT).show();
        }
    }
}
