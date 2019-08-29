package com.baisi.whatsfreecall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.baisi.whatsfreecall.view.CallScreenKeyBoard;


public class TestActivity extends AppCompatActivity {
    RecyclerView rlv;
    private CallScreenKeyBoard mCallKeyboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
      /*  List<SkuDetails> skuDetailses = new ArrayList<>();
        skuDetailses.add(new SkuDetails("10", "No gift"));
        skuDetailses.add(new SkuDetails("10", "No gift"));
        skuDetailses.add(new SkuDetails("10", "No gift"));
        skuDetailses.add(new SkuDetails("10", "No gift"));
        skuDetailses.add(new SkuDetails("10", "No gift"));
        skuDetailses.add(new SkuDetails("10", "No gift"));
        rlv = (RecyclerView) findViewById(R.id.rlv);*/

       /* GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setAutoMeasureEnabled(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        rlv.setHasFixedSize(true);
        rlv.setNestedScrollingEnabled(false);
        rlv.setLayoutManager(layoutManager);*/
//        rlv.setAdapter(new SkuAdapter(this,skuDetailses));

    }

    private void initView() {

        mCallKeyboard = (CallScreenKeyBoard) findViewById(R.id.call_keyboard);
        mCallKeyboard.setOnNumberClickListener(new CallScreenKeyBoard.OnNumberClickListener() {
            @Override
            public void setNumber(String number) {
                Toast.makeText(TestActivity.this,number,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
