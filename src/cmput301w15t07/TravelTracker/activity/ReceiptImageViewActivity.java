/*
 *   Copyright 2015 Kirby Banman,
 *                  Stuart Bildfell,
 *                  Elliot Colp,
 *                  Christian Ellinger,
 *                  Braedy Kuzma,
 *                  Ryan Thornhill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cmput301w15t07.TravelTracker.activity;


import java.util.UUID;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
/**
 * Activity for viewing receipt images 
 * @author cellinge
 *
 */

public class ReceiptImageViewActivity extends TravelTrackerActivity {
   
	/** UUID for the Item */
	private UUID itemID; 
	
	/** the current expense item */
	private Item item = null;
	
	/** The menu for the Activity */ 
	private Menu menu = null;
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.receipt_image_view_menu, menu);
		this.menu = menu;
		return true; 
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.receipt_image_view_sign_out:
	        signOut();
	        return true;
	        
	    case android.R.id.home:
	    	onBackPressed();
	    	return true;
	        
	    default:
	        return false;
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_image_view);
        Bundle bundle = getIntent().getExtras();
         itemID = (UUID) bundle.getSerializable(ITEM_UUID);
         datasource.getItem(itemID, new ResultCallback<Item>() {
            
            @Override
            public void onResult(Item result) {
                item = result;
                loadImage(item.getReceipt().getPhoto());
            }
            
            @Override
            public void onError(String message) {
                // TODO Auto-generated method stub
                
            }
        });
         
    }
    
	// There is no dataset in ReceiptImageViewActivity, this needs no implementation.
    @Override
    public void updateActivity() {}
	
    /**
     * Load the image into the imageveiw
     * @param image the bitmap image to be loaded into the imageView 
     */
	protected void loadImage(Bitmap image) {
		ImageView imageView = (ImageView) findViewById(R.id.receipt_image_veiw_imageView);
		if (image != null) {
			imageView.setImageBitmap(image);
		}
		else{
			Toast.makeText(this, "bitmap is null", Toast.LENGTH_SHORT).show();
		}
		
	}
}