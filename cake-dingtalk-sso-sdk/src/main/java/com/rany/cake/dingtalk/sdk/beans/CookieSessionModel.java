package com.rany.cake.dingtalk.sdk.beans;


public enum CookieSessionModel {
	/**
	 * SSO服务器与客户端应用session独立，完全级别较高，但是session过期互不影响
	 */
	security,
	/**
	 * SSO服务器与客户端应用session共享，安全级别较低
	 */
	sharing
}
