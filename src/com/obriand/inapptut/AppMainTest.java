package com.obriand.inapptut;

import com.obriand.inapptut.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AppMainTest extends Activity {
    
	private static final String TAG = "BillingService";
	
	private Context mContext;
	private ImageView purchaseableItem;
	private Button purchaseButton;
	private Button restoreButton;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BillingService", "Starting");
        setContentView(R.layout.main);
         
        mContext = this;
        
        purchaseButton = (Button) findViewById(R.id.main_purchase_yes);
        //purchaseButton.setOnClickListener(this);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
    			if(BillingHelper.isBillingSupported()){
    				BillingHelper.requestPurchase(mContext, "sub_tut_003"); //product_tut_002
    				// android.test.purchased or android.test.canceled or android.test.refunded or com.blundell.item.passport
    	        } else {
    	        	Log.i(TAG,"Can't purchase on this device");
    	        	purchaseButton.setEnabled(false); // XXX press button before service started will disable when it shouldnt
    	        }
        	}
        });
        restoreButton = (Button) findViewById(R.id.main_restore_yes);
        restoreButton.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
    			if(BillingHelper.isBillingSupported()){
    				long nonce = BillingSecurity.generateNonce();
    				BillingHelper.restoreTransactionInformation(nonce); // set the nonce here
    				// android.test.purchased or android.test.canceled or android.test.refunded or com.blundell.item.passport
    	        } else {
    	        	Log.i(TAG,"Can't purchase/restore on this device");
    	        	purchaseButton.setEnabled(false); // XXX press button before service started will disable when it shouldnt
    	        	restoreButton.setEnabled(false);
    	        }
        	}
        });        
        purchaseableItem = (ImageView) findViewById(R.id.main_purchase_item);
        
        startService(new Intent(mContext, BillingService.class));
        BillingHelper.setCompletedHandler(mTransactionHandler);
    }

    public Handler mTransactionHandler = new Handler(){
    		public void handleMessage(android.os.Message msg) {
    			Log.i(TAG, "Transaction complete");
    			Log.i(TAG, "Transaction status: "+BillingHelper.latestPurchase.purchaseState);
    			Log.i(TAG, "Item purchased is: "+BillingHelper.latestPurchase.productId);
    			
    			if(BillingHelper.latestPurchase.isPurchased()){
    				showItem();
    				// TODO : store the purchase id
    			}
    		};
    	
    };
	
	private void showItem() {
		purchaseableItem.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause())");
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		BillingHelper.stopService();
		super.onDestroy();
	}
}