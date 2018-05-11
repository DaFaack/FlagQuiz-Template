package com.nileworx.flagsquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;



public class ShopActivity
        extends Activity{

    SimpleMethods sm = new SimpleMethods();

    //VideoAD
    RelativeLayout btn_videoAd;
    private RewardedVideoAd mRewardedVideoAd;
    boolean rewarded, survey_achieved;
    TextView videoText;


    //rate App
    RelativeLayout btn_rate_app;
    ConnectionDetector cd;
    TextView rateText;

    RelativeLayout btn_insta, btn_share;
    TextView tv_insta, tv_share;

    TextView coinfield;

    //setAlpha
    RelativeLayout rateRL, instaRL, shareRL;


    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor e;

    String marketLink = "https://play.google.com/store/apps/details?id=com.engahmedgalal.successquotes";

    DAO db;
    Cursor c;

    public Typeface tf;

    SoundClass sou;
    CustomDialog dialog;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);


        tf = Typeface.createFromAsset(getAssets(), "fonts/HOBOSTD.OTF");

        //initialisation
        btn_videoAd = (RelativeLayout)findViewById(R.id.btn_videoAd);
        btn_rate_app = (RelativeLayout)findViewById(R.id.btn_rate_app);
        btn_insta = (RelativeLayout)findViewById(R.id.btn_instagram);
        btn_share = (RelativeLayout)findViewById(R.id.btn_share);
        tv_insta = (TextView)findViewById(R.id.instaText);
        tv_share = (TextView)findViewById(R.id.share_text);
        coinfield = (TextView)findViewById(R.id.coinfield);
        rateText = (TextView) findViewById(R.id.rateText);
        videoText = (TextView)findViewById(R.id.videoText);

        rateText.setTypeface(tf);
        tv_insta.setTypeface(tf);
        tv_share.setTypeface(tf);
        videoText.setTypeface(tf);

        //setAlpha
        rateRL = (RelativeLayout)findViewById(R.id.rateRL);
        instaRL = (RelativeLayout)findViewById(R.id.instaRL);
        shareRL = (RelativeLayout)findViewById(R.id.shareRL);

        btn_insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sm.getBoolean("insta_used", getApplicationContext())){
                    instagram();
                }else{
                    Toast.makeText(getApplicationContext(), "You can just follow us once, you had your chance...", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sm.getBoolean("sharing_used", getApplicationContext())){
                   shareapp();
                }else{
                    Toast.makeText(getApplicationContext(), "You can share this app once", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_videoAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoAd();
            }
        });


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewarded = false;
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
                if(rewarded){
                    videoAdCompleted();
                }
                rewarded = false;
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                rewarded = true;
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
        mRewardedVideoAd.loadAd(getText(R.string.videoAdID).toString(),
                new AdRequest.Builder().build());

        btn_rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_rate_click();
            }
        });


        if(sm.getBoolean("rate_used", getApplicationContext())){
            btn_rate_app.setBackgroundResource(R.drawable.button_shop_diabled);
            rateText.setAlpha(0.5f);
            rateRL.setAlpha(0.5f);
        }

        if(sm.getBoolean("sharing_used", getApplicationContext())){
            btn_share.setBackgroundResource(R.drawable.button_shop_diabled);
            tv_share.setAlpha(0.5f);
            instaRL.setAlpha(0.5f);
        }

        if(sm.getBoolean("insta_used", getApplicationContext())){
            btn_insta.setBackgroundResource(R.drawable.button_shop_diabled);
            tv_insta.setAlpha(0.5f);
            shareRL.setAlpha(0.5f);
        }
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/" + getResources().getString(R.string.main_font));


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
        title.setTypeface(tf);

        RelativeLayout scoreAndCoins = (RelativeLayout) layout.findViewById(R.id.scoreAndCoins);
        scoreAndCoins.setVisibility(View.GONE);

        TextView level = (TextView) layout.findViewById(R.id.level);
        level.setVisibility(View.GONE);

        TextView coinfield = (TextView) findViewById(R.id.coinfield);
        coinfield.setTypeface(tf);
        coinfield.setText(String.valueOf(getCoinsNumber()));


        ImageButton back = (ImageButton) layout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sou.playSound(R.raw.buttons);
                Intent intent = new Intent(ShopActivity.this, MainActivity.class);
                finish();
                startActivity(intent);


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







    public int getCoinsNumber() {
        Cursor cCoins = db.getCoinsCount();

        int coinsNumber = cCoins.getInt(cCoins.getColumnIndex("total_coins")) - cCoins.getInt(cCoins.getColumnIndex("used_coins"));
        return coinsNumber;
    }





    public void videoAd(){
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
        else if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.e("###","not loaded");
            Toast.makeText(getApplicationContext(), "Please wait a moment until the ad is loaded/you can watch another ad", Toast.LENGTH_SHORT).show();
        }
        mRewardedVideoAd.loadAd(getText(R.string.videoAdID).toString(),
                new AdRequest.Builder().build());

    }

    public void videoAdCompleted(){
        db.addTotalCoins(Integer.parseInt(getText(R.string.coins_for_videeoad).toString() ));
        sm.playSound(R.raw.rewardsound, getApplicationContext());
        Toast.makeText(getApplicationContext(), "Congratulations, you received " + getText(R.string.coins_for_videeoad).toString() + " coins!", Toast.LENGTH_SHORT).show();
        coinfield.setText(String.valueOf(getCoinsNumber()));
    }




    public void btn_rate_click(){
        cd = new ConnectionDetector(ShopActivity.this);
        if(cd.isConnectingToInternet()&&!sm.getBoolean("rate_used", getApplicationContext())){
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

                    sm.saveBoolean("rate_used", true, getApplicationContext());



                    dialog.showDialog(R.layout.blue_dialog, "afterRatingDlg", getResources().getString(R.string.afterRatingDlg), null);




                    db.addTotalCoins(Integer.parseInt(getText(R.string.coins_for_rating).toString() ));
                    coinfield.setText(String.valueOf(getCoinsNumber()));
                }
            }, 8000);



        }else if(!cd.isConnectingToInternet()&&!sm.getBoolean("rate_used", getApplicationContext())){
            AlertDialog.Builder a_builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                a_builder = new AlertDialog.Builder(ShopActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                a_builder = new AlertDialog.Builder(ShopActivity.this);
            }
            Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "You can only submit a rating once", Toast.LENGTH_SHORT).show();
        }
    }


    public void instagram(){
        if(isNetworkAvailable()){
            AlertDialog.Builder a_builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                a_builder = new AlertDialog.Builder(ShopActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                a_builder = new AlertDialog.Builder(ShopActivity.this);
            }
            a_builder.setMessage("Are you sure you want to follow our instagram page? You wont become a second chance...")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uriUrl = Uri.parse("http://redirection.lima-city.de/links/instagram.html");
                            Intent openUrl = new Intent(Intent.ACTION_VIEW, uriUrl);
                            startActivity(openUrl);

                            btn_insta.setBackgroundResource(R.drawable.button_shop_diabled);
                            tv_insta.setAlpha(0.5f);

                            sm.saveBoolean("insta_used", true, getApplicationContext());
                            db.addTotalCoins(Integer.parseInt(getText(R.string.coins_for_insta).toString()));
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alert = a_builder.create();
            alert.setTitle("Instagram");
            alert.show();

        }else{
            Toast.makeText(getApplicationContext(), "check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareapp(){
        if(isNetworkAvailable()){

        }else{
            Toast.makeText(getApplicationContext(), "check your interbet connection", Toast.LENGTH_SHORT).show();
        }

        if(isNetworkAvailable()){
            AlertDialog.Builder a_builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                a_builder = new AlertDialog.Builder(ShopActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                a_builder = new AlertDialog.Builder(ShopActivity.this);
            }
            a_builder.setMessage("Are you sure you want to share this amazing app with a friend? You wont become a second chance...")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Fortnite QUIZ");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "✶Play this new amazing Fortnite QUIZ✶how good are you?✶\n\n" + getText(R.string.link_to_app).toString());
                            startActivity(Intent.createChooser(shareIntent,  "Teilen über..."));

                            btn_share.setBackgroundResource(R.drawable.button_shop_diabled);
                            tv_share.setAlpha(0.5f);

                            sm.saveBoolean("sharing_used", true, getApplicationContext());
                            db.addTotalCoins(Integer.parseInt(getText(R.string.coins_for_share).toString()));

                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alert = a_builder.create();
            alert.setTitle("Share app");
            alert.show();

        }else{
            Toast.makeText(getApplicationContext(), "check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShopActivity.this, MainActivity.class);
        sou.playSound(R.raw.buttons);
        finish();
        startActivity(intent);
    }





    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


//    ------Pollfish stuff ---------------------------------------------------------------------------------



}
