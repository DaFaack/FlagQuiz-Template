package com.nileworx.flagsquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ImpressumActivity extends AppCompatActivity {
SoundClass sou;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);

        sou = new SoundClass(ImpressumActivity.this);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.titleBar);

        TextView title = (TextView) layout.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.impressumTitle).toUpperCase());

        RelativeLayout scoreAndCoins = (RelativeLayout) layout.findViewById(R.id.scoreAndCoins);
        scoreAndCoins.setVisibility(View.GONE);


        TextView level = (TextView) layout.findViewById(R.id.level);
        level.setVisibility(View.GONE);

        ImageButton back = (ImageButton) layout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sou.playSound(R.raw.buttons);
                finish();

            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sou.playSound(R.raw.buttons);
        finish();
    }
}
