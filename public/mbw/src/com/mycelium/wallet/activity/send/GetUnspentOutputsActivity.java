/*
 * Copyright 2013 Megion Research and Development GmbH
 *
 * Licensed under the Microsoft Reference Source License (MS-RSL)
 *
 * This license governs use of the accompanying software. If you use the software, you accept this license.
 * If you do not accept the license, do not use the software.
 *
 * 1. Definitions
 * The terms "reproduce," "reproduction," and "distribution" have the same meaning here as under U.S. copyright law.
 * "You" means the licensee of the software.
 * "Your company" means the company you worked for when you downloaded the software.
 * "Reference use" means use of the software within your company as a reference, in read only form, for the sole purposes
 * of debugging your products, maintaining your products, or enhancing the interoperability of your products with the
 * software, and specifically excludes the right to distribute the software outside of your company.
 * "Licensed patents" means any Licensor patent claims which read directly on the software as distributed by the Licensor
 * under this license.
 *
 * 2. Grant of Rights
 * (A) Copyright Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free copyright license to reproduce the software for reference use.
 * (B) Patent Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free patent license under licensed patents for reference use.
 *
 * 3. Limitations
 * (A) No Trademark License- This license does not grant you any rights to use the Licensor’s name, logo, or trademarks.
 * (B) If you begin patent litigation against the Licensor over patents that you think may apply to the software
 * (including a cross-claim or counterclaim in a lawsuit), your license to the software ends automatically.
 * (C) The software is licensed "as-is." You bear the risk of using it. The Licensor gives no express warranties,
 * guarantees or conditions. You may have additional consumer rights under your local laws which this license cannot
 * change. To the extent permitted under your local laws, the Licensor excludes the implied warranties of merchantability,
 * fitness for a particular purpose and non-infringement.
 */

package com.mycelium.wallet.activity.send;

import android.app.Activity;
import android.os.Bundle;

import com.mycelium.wallet.MbwManager;
import com.mycelium.wallet.R;
import com.mycelium.wallet.Utils;
import com.mycelium.wallet.Wallet;
import com.mycelium.wallet.Wallet.SpendableOutputs;
import com.mycelium.wallet.Wallet.WalletUpdateHandler;
import com.mycelium.wallet.activity.send.SendActivityHelper.SendContext;
import com.mycelium.wallet.api.AsyncTask;

public class GetUnspentOutputsActivity extends Activity {

   private AsyncTask _task;
   private MbwManager _mbwManager;
   private SendContext _context;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.get_unspent_outputs_activity);

      // Get intent parameters
      _context = SendActivityHelper.getSendContext(this);

      _mbwManager = MbwManager.getInstance(getApplication());

      // Update wallet from server
      _task = _context.wallet.requestUpdate(_mbwManager.getBlockChainAddressTracker(), new MyWalletUpdateHandler());
   }

   @Override
   protected void onDestroy() {
      cancelEverything();
      super.onDestroy();
   }

   private void cancelEverything() {
      if (_task != null) {
         _task.cancel();
      }
   }

   class MyWalletUpdateHandler implements WalletUpdateHandler {

      @Override
      public void walletUpdatedCallback(Wallet wallet, boolean success) {
         Activity me = GetUnspentOutputsActivity.this;
         if (!success) {
            Utils.toastConnectionError(me);
            _task = null;
            me.finish();
         } else {
            SpendableOutputs spendable;
            spendable = wallet.getLocalSpendableOutputs(_mbwManager.getBlockChainAddressTracker());
            SendActivityHelper.startNextActivity(me, spendable);
         }
      }

   }

}