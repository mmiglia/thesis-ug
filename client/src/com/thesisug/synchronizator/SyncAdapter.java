/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.thesisug.synchronizator;

import java.io.IOException;
import java.util.Date;

import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the
 * platform ContactOperations provider.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "thesisug - SyncAdapter";

    private final AccountManager mAccountManager;
    private final Context mContext;

    private Date mLastUpdated;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {
        
         try {
             // use the account manager to request the credentials
             //authtoken = mAccountManager.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE, true /* notifyAuthFailure */);
             // fetch updates from the sample service over the cloud
             //users =  NetworkUtilities.fetchFriendUpdates(account, authtoken, mLastUpdated);
            // update the last synced date.
            //mLastUpdated = new Date();
            // update platform contacts.
            Log.d(TAG, "Calling contactManager's sync contacts");
            //ContactManager.syncContacts(mContext, account.name, users);
            // fetch and update status messages for all the synced users.
            //statuses = NetworkUtilities.fetchFriendStatuses(account, authtoken);
            //ContactManager.insertStatuses(mContext, account.name, statuses);
        }  catch (final ParseException e) {
            syncResult.stats.numParseExceptions++;
            Log.e(TAG, "ParseException", e);
        } 
    }
}
