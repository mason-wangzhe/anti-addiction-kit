    package com.seraph.gameprotection;

    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.content.pm.ActivityInfo;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Message;
    import android.util.Log;
    import android.view.KeyEvent;
    import android.view.View;
    import android.view.WindowManager;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import com.antiaddiction.debug.R;
    import com.antiaddiction.sdk.AntiAddictionKit;
    import com.antiaddiction.sdk.AntiAddictionPlatform;
    import com.antiaddiction.sdk.utils.LogUtil;

    import java.util.ArrayList;
    import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button login,login2,login3,pay,init_land,init_port,logout,chat,thirdInit,dialogFloat,pay2,netLink
            ,third_real,pay3;
    TextView textView;
    Spinner third_real_item;
    EditText id1,id2,id3;
    public static String TAG = "tap anti";
    AntiAddictionKit.AntiAddictionCallback protectCallBack;
    int payNum = 0;
    int third_real_type = 0;
    final static int LOGIN = 0x001;
    boolean netLinkState = false;
    String userId = "userId1";
     Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what  = msg.what;
            switch (what){
                case 0x001:
                    userId = (String) msg.obj;
                 //   int type = userId.contains("4") ? 0 : (userId.contains("5") ? AntiAddictionKit.USER_TYPE_CHILD : AntiAddictionKit.USER_TYPE_ADULT);
                    AntiAddictionKit.login(userId,msg.arg1);
                    pay.setEnabled(true);
                    pay2.setEnabled(true);
                    pay3.setEnabled(true);
                    logout.setEnabled(true);
                    chat.setEnabled(true);
                    login.setEnabled(false);
                    login2.setEnabled(false);
                    login3.setEnabled(false);
                    break;
            }
        }
    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            textView.setText("剩余： " + intent.getIntExtra("time",0) + " 秒");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
       AntiAddictionKit.getCommonConfig()
               .gusterTime(18 * 60)
               .childCommonTime(16 * 60)
               .childHolidayTime(20*60)
               .youngMonthPayLimit(200 * 100)
               .teenMonthPayLimit(150 * 100)
               .dialogBackground("#fffff0")
       .nightStrictStart(23*60*60);

       AntiAddictionKit.getFunctionConfig()
               .showSwitchAccountButton(false)
               .useSdkOnlineTimeLimit(true);

        login = findViewById(R.id.login);
        login2 = findViewById(R.id.login2);
        login3 = findViewById(R.id.login3);
        pay = findViewById(R.id.pay);
        pay2 = findViewById(R.id.pay2);
        pay3 = findViewById(R.id.pay3);
        logout = findViewById(R.id.logout);
        init_land = findViewById(R.id.init_land);
        init_port = findViewById(R.id.init_port);
        textView = findViewById(R.id.tv_time);
        chat = findViewById(R.id.chat);
        thirdInit = findViewById(R.id.init_third);
        dialogFloat = findViewById(R.id.bt_float);
        netLink = findViewById(R.id.netLink);
        third_real = findViewById(R.id.third_real);
        third_real_item = findViewById(R.id.third_real_item);

        id1 = findViewById(R.id.et_id1);
        id2 = findViewById(R.id.et_id2);
        id3 = findViewById(R.id.et_id3);

//        third_real.setVisibility(View.GONE);
//        third_real_item.setVisibility(View.GONE);
//        thirdInit.setVisibility(View.GONE);

        login.setOnClickListener(this);
        login2.setOnClickListener(this);
        login3.setOnClickListener(this);
        pay.setOnClickListener(this);
        pay2.setOnClickListener(this);
        pay3.setOnClickListener(this);
        logout.setOnClickListener(this);
        init_port.setOnClickListener(this);
        init_land.setOnClickListener(this);
        chat.setOnClickListener(this);
        thirdInit.setOnClickListener(this);
        dialogFloat.setOnClickListener(this);
        dialogFloat.setVisibility(View.GONE);
        netLink.setOnClickListener(this);
        third_real.setOnClickListener(this);

        List<String> items = new ArrayList<>();
        items.add("5岁");
        items.add("12岁");
        items.add("17岁");
        items.add("20岁");
        third_real_item.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items));
        third_real_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               third_real_type = position +1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        login.setEnabled(false);
        login2.setEnabled(false);
        login3.setEnabled(false);
        logout.setEnabled(false);
        pay.setEnabled(false);
        pay2.setEnabled(false);
        pay3.setEnabled(false);
        chat.setEnabled(false);
        third_real.setEnabled(false);
        protectCallBack = new AntiAddictionKit.AntiAddictionCallback() {
            @Override
            public void onAntiAddictionResult(int resultCode, String msg) {
                Log.e(TAG, String.format("code: %d, msg=%s", resultCode, msg));
                switch (resultCode){
                    case AntiAddictionKit.CALLBACK_CODE_SWITCH_ACCOUNT:
                        toast("logout success");
                        login.setEnabled(true);
                        login2.setEnabled(true);
                        login3.setEnabled(true);
                        pay.setEnabled(false);
                        pay2.setEnabled(false);
                        pay3.setEnabled(false);
                        logout.setEnabled(false);
                        chat.setEnabled(false);
                        break;
                    case AntiAddictionKit.CALLBACK_CODE_PAY_NO_LIMIT:
                        toast(" pay no limit");
                        AntiAddictionKit.paySuccess(payNum);
                        break;
                    case AntiAddictionKit.CALLBACK_CODE_PAY_LIMIT:
                        toast("pay limit");
                         break;
                    case AntiAddictionKit.CALLBACK_CODE_REAL_NAME_SUCCESS:
                        toast("realName success");
                       Log.d("antiAddiction","------real name success-----");
                         break;
                    case AntiAddictionKit.CALLBACK_CODE_REAL_NAME_FAIL:
                        toast("realName fail");
                        break;
                    case AntiAddictionKit.CALLBACK_CODE_TIME_LIMIT:
                        toast("time limit ");
                        Log.d("antiAddiction","------time limit-----");
                        break;
                    case AntiAddictionKit.CALLBACK_CODE_OPEN_REAL_NAME:
                       toast("open realName");
                       //假设通过第三方实名成功
//                       AntiAddictionKit.updateUserType(AntiAddictionKit.USER_TYPE_CHILD);
//                       if(msg.equals(AntiAddictionKit.TIP_OPEN_BY_PAY_LIMIT)){
//                           AntiAddictionKit.checkPayLimit(payNum);
//                       }
                       //注意：如果这个过程中游戏处在付费流程中，此时应该再调用一次CheckPayLimit();
                       break;
                    case AntiAddictionKit.CALLBACK_CODE_CHAT_LIMIT:
                        toast("chat limit");
                        break;
                    case AntiAddictionKit.CALLBACK_CODE_CHAT_NO_LIMIT:
                        toast("chat no limit");
                        break;
                    case AntiAddictionKit.CALLBACK_CODE_AAK_WINDOW_DISMISS:
                        Log.d("config","AAK WINDOW DISMISS");
                        break;
                    case AntiAddictionKit.CALLBACK_CODE_AAK_WINDOW_SHOWN:
                        Log.d("config","AAK WINDOW SHOW");
                        break;
                    case AntiAddictionKit.CALLBACK_CODE_USER_TYPE_CHANGED:
                        toast("USER TYPE CHANGE");
                        break;
                    case AntiAddictionKit.CALLBACK_CODE_LOGIN_SUCCESS:
                        toast("login success msg = " + msg);
                        Log.d("antiAddiction","-----login success----");
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        AntiAddictionKit.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter("antisdk.time.click"));

    }

    @Override
    protected void onStop() {
        super.onStop();
        AntiAddictionKit.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.pay:
                payNum = 30 * 100;
                AntiAddictionKit.checkPayLimit(30 * 100);
                break;
            case R.id.pay2:
                payNum = 120 * 100;
                AntiAddictionKit.checkPayLimit(120 * 100);
                break;
            case R.id.pay3:
                payNum = 100 * 100;
                AntiAddictionKit.checkPayLimit(100 * 100);
                break;
            case R.id.init_land:
                AntiAddictionKit.getFunctionConfig().useSdkRealName(true)
                        .useSdkOnlineTimeLimit(true);
                AntiAddictionKit.init(this,protectCallBack);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                init_land.setEnabled(false);
                init_port.setEnabled(false);
                thirdInit.setEnabled(false);
                login.setEnabled(true);
                login2.setEnabled(true);
                login3.setEnabled(true);
                break;
            case R.id.init_port:
                AntiAddictionKit.getFunctionConfig().useSdkRealName(true)
                        .useSdkOnlineTimeLimit(true);
                AntiAddictionKit.init(this,protectCallBack);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                init_land.setEnabled(false);
                init_port.setEnabled(false);
                thirdInit.setEnabled(false);
                login.setEnabled(true);
                login2.setEnabled(true);
                login3.setEnabled(true);
                break;
            case R.id.login:
               String id_1 = id1.getText().toString();
               if(id_1.trim().length() == 0){
                   id_1 = "userid30";
               }
                if(netLinkState){
                    getUserToken(id_1);
                    break;
                }
                userId = id_1;
                AntiAddictionKit.login(id_1,0);
                pay.setEnabled(true);
                pay2.setEnabled(true);
                pay3.setEnabled(true);
                logout.setEnabled(true);
                chat.setEnabled(true);
                login.setEnabled(false);
                login2.setEnabled(false);
                login3.setEnabled(false);
                break;
            case R.id.login2:
                String id_2 = id2.getText().toString();
                if(id_2.trim().length() == 0){
                    id_2 = "userid40";
                }
                if(netLinkState){
                    getUserToken(id_2);
                    break;
                }
                userId = id_2;
                AntiAddictionKit.login(id_2,AntiAddictionKit.USER_TYPE_CHILD);
                pay.setEnabled(true);
                pay2.setEnabled(true);
                pay3.setEnabled(true);
                logout.setEnabled(true);
                chat.setEnabled(true);
                login.setEnabled(false);
                login2.setEnabled(false);
                login3.setEnabled(false);
                break;
            case R.id.login3:
                String id_3 = id3.getText().toString();
                if(id_3.trim().length() == 0){
                    id_3 = "userid50";
                }
                if(netLinkState){
                    getUserToken(id_3);
                    break;
                }
                userId = id_3;
                AntiAddictionKit.login(id_3,AntiAddictionKit.USER_TYPE_ADULT);
                pay.setEnabled(true);
                pay2.setEnabled(true);
                pay3.setEnabled(true);
                logout.setEnabled(true);
                chat.setEnabled(true);
                login.setEnabled(false);
                login2.setEnabled(false);
                login3.setEnabled(false);
                break;
            case R.id.logout:
                AntiAddictionKit.logout();
                break;
            case R.id.chat:
                AntiAddictionKit.checkChatLimit();
                break;
            case R.id.init_third:
                AntiAddictionKit.getFunctionConfig().useSdkRealName(false)
                        .useSdkOnlineTimeLimit(true);
                AntiAddictionKit.init(this,protectCallBack);
                init_land.setEnabled(false);
                init_port.setEnabled(false);
                thirdInit.setEnabled(false);
                login.setEnabled(true);
                login2.setEnabled(true);
                login3.setEnabled(true);
                third_real.setEnabled(true);
                break;
            case R.id.bt_float:
                AntiAddictionPlatform.showCountTimePop("测试","测试内容。。。。。。",30,1);
                break;
            case R.id.netLink:
                netLinkState = !netLinkState;
                if(netLinkState){
                    AntiAddictionKit.getFunctionConfig().setHost("http://localhost/realname"); //游戏需要更换为自己服务器的域名
                    netLink.setText("联网模式：开");
                }else{
                    AntiAddictionKit.getFunctionConfig().setHost(null);
                    netLink.setText("联网模式：关");
                }
                break;
            case R.id.third_real:
                AntiAddictionKit.updateUserType(third_real_type);
                break;


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            System.exit(0);
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 这里模拟服务端生成对应用户的token,游戏需要替换为自己的模拟器
     * @param userId
     */
    void getUserToken(final String userId){
        Message msg = handler.obtainMessage();
        msg.what = LOGIN;
        msg.obj = "test token";
        msg.arg1 =  userId.contains(id1.getText().toString().trim()) ? 0 : (userId.contains(id2.getText().toString().trim()) ? AntiAddictionKit.USER_TYPE_CHILD : AntiAddictionKit.USER_TYPE_ADULT);
        handler.sendMessage(msg);
        LogUtil.logd("getAccess token success = " + msg.obj);
        return;
//        HttpUtil.getAsync("http://localhost:8080/create_user_token?user_id=" + userId, new NetUtil.NetCallback() {
//            @Override
//            public void onSuccess(String response) {
//                LogUtil.logd("getAccess token success = " + response);
//                Message msg = handler.obtainMessage();
//                msg.what = LOGIN;
//                try {
//                    String id_1 = id1.getText().toString().trim();
//                    String id_2 = id2.getText().toString().trim();
//                    msg.obj = new JSONObject(response).getString("token");
//                    msg.arg1 =  userId.contains(id1.getText().toString().trim()) ? 0 : (userId.contains(id2.getText().toString().trim()) ? AntiAddictionKit.USER_TYPE_CHILD : AntiAddictionKit.USER_TYPE_ADULT);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                handler.sendMessage(msg);
//            }
//
//            @Override
//            public void onFail(int code, String message) {
//                LogUtil.logd("get token fail code = " + code + " msg= " + message);
////                Toast.makeText(MainActivity.this,"网络连接失败 msg = " + message,Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
