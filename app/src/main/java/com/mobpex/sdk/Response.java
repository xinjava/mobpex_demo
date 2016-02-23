package com.mobpex.sdk;

public class Response implements java.io.Serializable {
	public boolean isSuceed;
	public String charge;	//最终渠道签名信息（唤醒支付SDK）
	public String msg;
}
