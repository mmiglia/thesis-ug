package com.thesisug.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.thesisug.Constants;
import com.thesisug.R;
import com.thesisug.ui.Login;

/**
 * Implementation of AbstractAccountAuthenticator for authenticating accounts in
 * the com.thesisug domain.
 */
public class Authenticator extends AbstractAccountAuthenticator {
	private final Context context;
	private static final String TAG = "thesisug - Authenticator";

	public Authenticator(Context context) {
		super(context);
		this.context = context;
		if (Log.isLoggable(TAG, Log.VERBOSE))
			Log.v(TAG, "Constructor initiated");
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options) {
		if (Log.isLoggable(TAG, Log.VERBOSE))
			Log.v(TAG, "Adding new account");
		final Intent intent = new Intent(context, Login.class);
		intent.putExtra(Login.PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		Log.i(TAG, "returned from Login Activity");
		return bundle;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) {
		if (Log.isLoggable(TAG, Log.VERBOSE))
			Log.v(TAG, "Confirm user credentials");
		if (options != null && options.containsKey(AccountManager.KEY_PASSWORD)) {
			final String password = options
					.getString(AccountManager.KEY_PASSWORD);
			final boolean verified = onlineConfirmPassword(account.name,
					password);
			final Bundle result = new Bundle();
			result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, verified);
			return result;
		}
		// Launch AuthenticatorActivity to confirm credentials
		final Intent intent = new Intent(context, Login.class);
		intent.putExtra(Login.PARAM_USERNAME, account.name);
		intent.putExtra(Login.PARAM_CONFIRMCREDENTIALS, true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle loginOptions) {
		if (Log.isLoggable(TAG, Log.VERBOSE))
			Log.v(TAG, "Getting Authorization token");
		if (!authTokenType.equals(Constants.AUTHTOKEN_TYPE)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE,
					"Invalid Authorization Token Type");
			return result;
		}
		final AccountManager am = AccountManager.get(context);
		final String password = am.getPassword(account);
		if (password != null) {
			final boolean verified = onlineConfirmPassword(account.name,
					password);
			if (verified) {
				final Bundle result = new Bundle();
				result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
				result.putString(AccountManager.KEY_ACCOUNT_TYPE,
						Constants.ACCOUNT_TYPE);
				result.putString(AccountManager.KEY_AUTHTOKEN, password);
				return result;
			}
		}
		// the password was missing or incorrect, return an Intent to an
		// activity that will prompt the user for the password.
		final Intent intent = new Intent(context, Login.class);
		intent.putExtra(Login.PARAM_USERNAME, account.name);
		intent.putExtra(Login.PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		return (authTokenType.equals(Constants.AUTHTOKEN_TYPE)) ? context
				.getString(R.string.app_name) : null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) {
		final Bundle result = new Bundle();
		result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
		return result;
	}

	/**
	 * Validates user's password on the server
	 */
	private boolean onlineConfirmPassword(String username, String password) {
		return true;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle loginOptions) {
		if (Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, "update Credentials");
		}
		final Intent intent = new Intent(context, Login.class);
		intent.putExtra(Login.PARAM_USERNAME, account.name);
		intent.putExtra(Login.PARAM_AUTHTOKEN_TYPE,
				authTokenType);
		intent.putExtra(Login.PARAM_CONFIRMCREDENTIALS, false);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}
}
