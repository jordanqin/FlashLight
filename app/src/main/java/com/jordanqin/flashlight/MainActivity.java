package com.jordanqin.flashlight;

import android.app.Activity;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * Created by qjd on 2015/9/6.
 * desc:
 */

public class MainActivity extends Activity {

    private Boolean m_IsOpened = false;
    private Boolean hasFlashLight=false;
    private Camera camera;
    private ImageView ivSwitch;
    private RelativeLayout rlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //友盟统计
        //MobclickAgent.setDebugMode(true);
        MobclickAgent.updateOnlineConfig(this);

        //友盟更新
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);

        ivSwitch = (ImageView)this.findViewById(R.id.ivSwitch);

        //以下代码判断手机是否带闪光灯
        FeatureInfo[] feature=MainActivity.this.getPackageManager().getSystemAvailableFeatures();
        for (FeatureInfo featureInfo : feature) {
            if (PackageManager.FEATURE_CAMERA_FLASH.equals(featureInfo.name)) {
                hasFlashLight=true;
                break;
            }
        }

        //带闪光灯
        if(hasFlashLight) {
            ivSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (!m_IsOpened) {
                        ivSwitch.setImageResource(R.drawable.on);
                        camera = Camera.open();
                        Camera.Parameters params = camera.getParameters();
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(params);
                        camera.startPreview(); // 开始亮灯

                        m_IsOpened = true;
                    } else {
                        ivSwitch.setImageResource(R.drawable.off);
                        camera.stopPreview(); // 关掉亮灯
                        camera.release(); // 关掉照相机
                        m_IsOpened = false;
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "不支持闪光灯,改用屏幕照明。", Toast.LENGTH_LONG).show();
            ivSwitch.setVisibility(android.view.View.GONE);
            rlMain=(RelativeLayout)findViewById(R.id.rlMain);
            //设置背景色为白色
            rlMain.setBackgroundColor(0xFFFFFFFF);
            //设置背景亮度为最高
            WindowManager.LayoutParams lParams=getWindow().getAttributes();
            lParams.screenBrightness=1.0f;
            getWindow().setAttributes(lParams);
            //设置背景常亮
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
