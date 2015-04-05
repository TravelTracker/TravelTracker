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

import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;

public class ReceiptImageViewActivity extends TravelTrackerActivity {
    /** String used to retrieve URI reference from intent */
    public static final String URI_DATA = "cmput301w15t07.TravelTracker.uriData";
	
	/** Immutable URI reference of the image. */
	private Uri imageUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receipt_image_view);
		Bundle bundle = getIntent().getExtras();
		 imageUri = (Uri) bundle.getSerializable(URI_DATA);
		 //Toast.makeText(this, imageUri.toString(), Toast.LENGTH_LONG).show();
		 try {
			loadImage(imageUri);
		} catch (FileNotFoundException e) {
			Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /**
     * There is no dataset in LoginActivity.
     * Called in onResume() and update(DataSource observable).
     */
    @Override
    public void updateActivity() {
        // Do nothing
    }
	
	protected void loadImage(Uri uri) throws FileNotFoundException, IOException{
		ImageView imageView = (ImageView) findViewById(R.id.receipt_image_veiw_imageView);
		Bitmap bitmap = null;
			//bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
		if (bitmap != null) {
			//imageView.setImageBitmap(bitmap);
		}
		else{
			Toast.makeText(this, "bitmap is null", Toast.LENGTH_SHORT).show();
		}
		
	}
}