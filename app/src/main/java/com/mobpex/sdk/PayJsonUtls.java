package com.mobpex.sdk;

import com.tencent.mm.sdk.modelpay.PayReq;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: xin.wu
 * @create time: 2016/1/13 10:05
 * @TODO: 解析服务端验签数据
 */
public class PayJsonUtls {

	/**
	 *
	 * @param jsonData
	 * @return
	 */
	public static Response toJsonAlipay(String jsonData) {
		Response res = new Response();
		if (!Utils.isEmpty(jsonData)) {
			try {
				JSONObject jsonObj = new JSONObject(jsonData);
				if (jsonObj.has("result")) {
					String str = jsonObj.getString("result");
					JSONObject resObj = new JSONObject(str);
					if (resObj.has("paymentParams")) {
	                    String paramsStr = resObj.getString("paymentParams");
	                    JSONObject paramObj = new JSONObject(paramsStr);
	                    if (paramObj.has("orderInfo")) {
	                    	res.charge = paramObj.getString("orderInfo");
	                    	res.isSuceed = true;
	                    }
	                }
				}
				if (!res.isSuceed || Utils.isNull(res.charge)) {
					res.msg = toErrorMsg(jsonObj);
				}
			} catch (JSONException e) {
				// TODO: handle exception
				res.isSuceed = false;
				res.msg = "The server is being processed, please try again later!";
			}
			
		}
		return res;
	}

	public static Response toJsonWeixin(String jsonData){
		Response res = new Response();
		if (!Utils.isEmpty(jsonData)) {
			try {
				JSONObject jsonObj = new JSONObject(jsonData);
				if (jsonObj.has("result")) {
					String str = jsonObj.getString("result");
					JSONObject resObj = new JSONObject(str);
					if (resObj.has("paymentParams")) {
						res.charge = resObj.getString("paymentParams");
						res.isSuceed = true;
					} 
				}
				if (!res.isSuceed || Utils.isNull(res.charge)) {
					res.msg = toErrorMsg(jsonObj);
				}
			} catch (JSONException e) {
				// TODO: handle exception
				res.isSuceed = false;
				res.msg = "The server is being processed, please try again later!";
			}
		}
		return res;
	}


	public static Response toJsonUpacp(String jsonData){
		Response res = new Response();
		if (!Utils.isEmpty(jsonData)) {
			try {
				JSONObject jsonObj = new JSONObject(jsonData);
				if (jsonObj.has("result")) {
	                String str = jsonObj.getString("result");
	                JSONObject resObj = new JSONObject(str);
	                if (resObj.has("paymentParams")) {
						String paramsStr = resObj.getString("paymentParams");
						JSONObject paramObj = new JSONObject(paramsStr);
						if (paramObj.has("tn")) {
							res.charge = paramObj.getString("tn");
							res.isSuceed = true;
						}
					}
	            }
				if (!res.isSuceed || Utils.isNull(res.charge)) {
					res.msg = toErrorMsg(jsonObj);
				}
			} catch (JSONException e) {
				// TODO: handle exception
				res.isSuceed = false;
				res.msg = "The server is being processed, please try again later!";
			}
			
		}
		return res;
	}

	public static Response toJsonMobpex(String jsonData){
		Response res = new Response();
		if (!Utils.isEmpty(jsonData)) {
			try {
				JSONObject jsonObj = new JSONObject(jsonData);
				if (jsonObj.has("result")) {
	                String str = jsonObj.getString("result");
	                JSONObject resObj = new JSONObject(str);
	                if (resObj.has("paymentParams")) {
						String paramsStr = resObj.getString("paymentParams");
						JSONObject paramObj = new JSONObject(paramsStr);
						if (paramObj.has("transUrl")) {
							res.charge = paramObj.getString("transUrl");
							res.isSuceed = true;
						}
					}
	            }
				if (!res.isSuceed || Utils.isNull(res.charge)) {
					res.msg = toErrorMsg(jsonObj);
				}
			} catch (JSONException e) {
				// TODO: handle exception
				res.isSuceed = false;
				res.msg = "The server is being processed, please try again later!";
			}
			
		}
		return res;
	}
	
	public static String toErrorMsg(JSONObject jsonObj) throws JSONException {
		String str = null;
		if (jsonObj.has("error")) {
			JSONObject resObj = new JSONObject(jsonObj.getString("error"));
			if (resObj.has("message")) {
				str = resObj.getString("message");
			}
		}
		return str;
	}
	
	/**
	 * @param jsonData
	 * @throws JSONException
	 */
	public static PayReq toRequesWeixin(String jsonData) throws JSONException {
		PayReq req = null;
		JSONObject json = new JSONObject(jsonData);
//		if (null != jsonData && !json.has("retcode")) {
			req = new PayReq();
			req.appId = json.getString("appid");
			req.partnerId = json.getString("partnerid");
			req.prepayId = json.getString("prepayid");
			req.nonceStr = json.getString("noncestr");
			req.timeStamp = json.getString("timestamp");
			req.packageValue = json.getString("package");
			req.sign = json.getString("sign");
//			req.extData = "app data"; // optional
//		}
		return req;
	}
}
