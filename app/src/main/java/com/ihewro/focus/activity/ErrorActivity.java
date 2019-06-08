package com.ihewro.focus.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.ihewro.focus.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ErrorActivity extends AppCompatActivity {

    @BindView(R.id.restart)
    Button restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        ButterKnife.bind(this);

        /*restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重新启动
                final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());
                assert config != null;
                CustomActivityOnCrash.restartApplication(ErrorActivity.this, config);
            }
        });*/
    }

}
