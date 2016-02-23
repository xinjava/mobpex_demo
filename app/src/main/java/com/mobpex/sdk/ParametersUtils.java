package com.mobpex.sdk;

import com.mobpex.plug.utils.PayChannel;

import java.util.HashMap;


public class ParametersUtils {
	
	/**
	 * @param channel	渠道名
	 * @param orderId	订单编号
	 * @param amount	订单金额
	 * @param secretKey 商户编号
	 * @param appId     用户ID
	 */
	public static HashMap<String,String> getMapParams(String channel,String orderId,String amount,String appId,String userId,String secretKey){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("payChannel",channel);
		map.put("orderId",orderId);
		map.put("keyType","LS_KEY");
		map.put("amount",amount);
//		map.put("secretKey",secretKey);
		map.put("appId",appId);
//		map.put("userId", userId);
		map.put("payType", getPayType(channel));
		return map;
	}
	
	
	/**
	 * @param payChannel
	 * @return	String
	 */
	public static String getPayType(String payChannel){
		if(payChannel.equalsIgnoreCase(PayChannel.mobpex)){
			return "WAP";
		}
		return "APP";
	}
	

    
}
