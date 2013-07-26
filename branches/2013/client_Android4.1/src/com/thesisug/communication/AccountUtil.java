package com.thesisug.communication;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.util.Log;

public class AccountUtil {
	private static final String TAG = "thesisug - AccountUtil";
	private AccountManager accountManager;
	private Account[] accounts;
	
	public String getToken(Context c) {
		try {
			accountManager = AccountManager.get(c);
			accounts = accountManager.getAccountsByType(com.thesisug.Constants.ACCOUNT_TYPE);
			return (accounts.length>0)? accountManager.blockingGetAuthToken(accounts[0],com.thesisug.Constants.ACCOUNT_TYPE, true):"";
		} catch (OperationCanceledException e) {
			e.printStackTrace();
			Log.i(TAG, "OperationCanceledException caught");
		} catch (AuthenticatorException e) {
			e.printStackTrace();
			Log.i(TAG, "AuthenticatorException caught");
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG, "IOException caught");
		}
		return "";
	}
	
	public String getUsername(Context c) {
		accountManager = AccountManager.get(c);
		accounts = accountManager.getAccountsByType(com.thesisug.Constants.ACCOUNT_TYPE);
		return (accounts.length>0)? accounts[0].name:"";
	}
}
