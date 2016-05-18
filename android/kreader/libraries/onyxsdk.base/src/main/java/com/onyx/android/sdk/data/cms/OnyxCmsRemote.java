/**
 * 
 */
package com.onyx.android.sdk.data.cms;

import com.onyx.android.sync.IOnyxSyncService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.util.Log;

/**
 * @author joy
 *
 */
public class OnyxCmsRemote 
{
    private static IOnyxSyncService mService = null;
    
    public static final String ACTION_SYNC_FINISHED = "com.onyx.android.sync.syncfinished";
    public static final String PARAM_SYNC_ID = "sync_id";
    public static final String PARAM_SYNC_RESULT = "result";
    
    /**
     * Class for interacting with the main interface of the service.
     */
    private static ServiceConnection mConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className,
                IBinder service)
        {
        	Log.i("Remote", "Connected");
            mService = IOnyxSyncService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className)
        {
        	Log.i("Remote", "Disconnected");
            mService = null;
        }
    };
    
	private static void initSyncService(Context context)
	{
		if (mService == null) {
			context.startService(new Intent("com.onyx.android.sync.OnyxSyncService"));
			context.bindService(new Intent("com.onyx.android.sync.OnyxSyncService"), 
					mConnection, Context.BIND_AUTO_CREATE);
			
			int count = 300;
			
			while (count-- > 0) {
				if (mService != null) {
					break;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	
	/**
	 * Sync a book by ISBN
	 * 
	 * NOTE: cannot call this method in message loop thread
	 * 
	 * @param context
	 * @param isbn
	 * @return the sync id
	 */
	public static int sync(Context context, String isbn)
	{
		return sync(context, context.getPackageName(), isbn);
	}
	
	/**
	 * Sync a book by ISBN
	 * 
	 * NOTE: cannot call this method in message loop thread
	 * 
	 * @param context
	 * @param application
	 * @param isbn
	 * @return the sync id
	 */
	public static int sync(Context context, String application, String isbn)
	{
		initSyncService(context);

		try {
			try {
				return mService.sync(application, isbn);
			} catch (DeadObjectException ex) {
				mService = null;
				initSyncService(context);
				return mService.sync(application, isbn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}
	
	/**
	 * Cancel a sync task
	 * 
	 * NOTE: cannot call this method in message loop thread
	 * 
	 * @param context
	 * @param syncId
	 * @return true if the sync canceled
	 */
	public static boolean cancel(Context context, int syncId)
	{
		initSyncService(context);

		try {
			try {
				return mService.cancel(syncId);
			} catch (DeadObjectException ex) {
				mService = null;
				initSyncService(context);
				return mService.cancel(syncId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
}
