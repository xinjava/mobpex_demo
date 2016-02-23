package com.mobpex.sdk;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.mobpex.plug.MobpexPaymentActivity;
import com.mobpex.plug.utils.MobpexLog;
import com.mobpex.plug.utils.PayChannel;
import com.mobpex.plug.utils.PaymentRequest;
import com.mobpex.sdk.R;
import com.tencent.mm.sdk.modelpay.PayReq;

public class PaymentActivity extends Activity implements View.OnClickListener {

	private static final int REQUEST_CODE_PAYMENT = 1;
	private EditText amountEditText;
	private String currentAmount = "";
	public String channel = "";
	public ProgressDialog progressDialog;

	// 修改自己的服务端签名地址
	public static String URL = "https://220.181.25.235/yop-center/demo";
	// 修改为自己的appId
	 String appId = "15122404366710489367";
//	String appId = "16011411397104893671";
	
	// 测试账号
	String userId = "demo@test.com";
	
	// 秘钥
	String secretKey = "LS_1bcda2pcqItkRtL1rtoafhb7b13rmdhg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		MobpexLog.DEBUG = true;
		amountEditText = (EditText) findViewById(R.id.amountEditText);
		findViewById(R.id.upmpButton).setOnClickListener(this);
		findViewById(R.id.alipayButton).setOnClickListener(this);
		findViewById(R.id.wechatButton).setOnClickListener(this);
		findViewById(R.id.yeepayButton).setOnClickListener(this);

		findViewById(R.id.radioGroup).setVisibility(View.GONE);

		initListener();
	}

	private void initListener() {
		RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				int radioButtonId = arg0.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) findViewById(radioButtonId);
				appId = rb.getText().toString();
			}
		});

	}

	private void dismiss() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	String amount;

	@Override
	public void onClick(View view) {
		amount = amountEditText.getText().toString();
		if (amount.equals("")) {
			Toast.makeText(getApplication(), "请输入金额", 0).show();
			return;
		}
		if (view.getId() == R.id.upmpButton) {
			onPay(PayChannel.upacp);
		} else if (view.getId() == R.id.alipayButton) {
			onPay(PayChannel.alipay);
		} else if (view.getId() == R.id.wechatButton) {
			onPay(PayChannel.wx);
		} else if (view.getId() == R.id.yeepayButton) {
			onPay(PayChannel.mobpex);
		}
	}

	public static String getRandomString() {
		String base = "123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 12; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	@SuppressLint("NewApi")
	public class HttpTask extends AsyncTask<String, String, String> {
		private HashMap<String, String> paramMap;

		public HttpTask(HashMap<String, String> map) {
			this.paramMap = map;
		}

		@Override
		protected String doInBackground(String[] params) {
			String charge = "";
			try {
				charge = WebUtils.doPost(URL, paramMap);
			} catch (Exception e) {
			}
			return charge;
		}

		@Override
		protected void onPostExecute(String charge) {
			dismiss();
			if (charge != null) {
				onExecute(charge);
			}
		}
	}

	public void onPay(String channel) {
		this.channel = channel;
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
		String orderId = getRandomString(); // 订单号
		HashMap<String, String> params = ParametersUtils.getMapParams(channel,
				orderId, amount, appId, userId, secretKey);
		new HttpTask(params).execute();
	}

	public Response toJson(String charge) {
		Response response = null;
		if (Utils.isNull(charge)) {
			return response;
		}
		try {
			if (channel.equalsIgnoreCase(PayChannel.alipay)) {
				response = PayJsonUtls.toJsonAlipay(charge);
			} else if (channel.equalsIgnoreCase(PayChannel.wx)) {
				response = PayJsonUtls.toJsonWeixin(charge);
			} else if (channel.equalsIgnoreCase(PayChannel.upacp)) {
				response = PayJsonUtls.toJsonUpacp(charge);
			} else if (channel.equalsIgnoreCase(PayChannel.mobpex)) {
				response = PayJsonUtls.toJsonMobpex(charge);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private void onExecute(String charge) {
		Response response = toJson(charge);
		if (response == null) {
			showMsg(channel, "", "");
			return;
		}
		if (!response.isSuceed) {
			showMsg(channel, "", response.msg);
			return;
		}

		Intent intent = new Intent();
		String orderId = getRandomString();
		PaymentRequest pr = new PaymentRequest(channel, response.charge);
		String packageName = getPackageName();
		if (pr != null) {
			ComponentName componentName = new ComponentName(packageName,
					packageName + ".wxapi.WXPayEntryActivity");
			intent.setComponent(componentName);
			intent.putExtra(MobpexPaymentActivity.EXTRA_CHARGE, pr);
			startActivityForResult(intent, REQUEST_CODE_PAYMENT);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 支付页面返回处理
		if (requestCode == REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				/*
				 * 处理返回值 "success" - 成功 "cancel" - 取消支付 "fail" - 支付异常
				 */
				String result = data.getExtras().getString("mobpex_result");
				/**
				 * 错误消息
				 */
				String msg = data.getExtras().getString("mobpex_msg");
				/**
				 * 当前渠道
				 */
				String channel = data.getExtras().getString("mobpex_channel");
				showMsg(channel, result, msg);
			}
		}
	}

	public void showMsg(String channel, String result, String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append("channel:" + channel + "\n");
		sb.append("result:" + result + "\n");
		if (null != msg && msg.length() != 0) {
			sb.append("\n" + msg);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(sb.toString());
		builder.setTitle("提示");
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}
}
