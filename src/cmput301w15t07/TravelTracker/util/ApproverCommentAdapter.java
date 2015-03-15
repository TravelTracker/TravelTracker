package cmput301w15t07.TravelTracker.util;

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

import java.util.List;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.ApproverComment;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter to show approver comments in a list.  Must know a DataSource to retrieve Approver names.
 * 
 * @author kdbanman
 *
 */
public class ApproverCommentAdapter extends ArrayAdapter<ApproverComment> {

	private DataSource ds;

	public ApproverCommentAdapter(Context context, DataSource ds, List<ApproverComment> comments) {
		super(context, R.layout.claim_info_comments_list_item, comments);
		
		this.ds = ds;
	}
	
	@Override
	public View getView(int position, View listItemView, ViewGroup listView) {
		
		// if not already done, grab the view from xml and inflate it
		if (listItemView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			listItemView = inflater.inflate(R.layout.claim_info_comments_list_item, listView, false);
		}
		
		ApproverComment comment = this.getItem(position);
		
		if (comment != null) {
			
			// set list item text components
			setText(listItemView, R.id.claimInfoCommentsListItemCommentTextView, comment.getComment());
			setText(listItemView, R.id.claimInfoCommentsListItemDateTextView, ClaimUtilities.formatDate(comment.getDate()));
			
		} else {
			Log.e("adapter", "comment " + position + " not found!");
		}
		return listItemView;
	}
	
	/**
	 * Fault-tolerant TextView text set wrapper.
	 */
	private boolean setText(View v, int id, String text) {
		TextView t = (TextView) v.findViewById(id);
		
		if (t != null) {
			t.setText(text);
			return true;
		} else {
			Log.e("adapter", "could not set text " + text + " for claim list item");
			return false;
		}
	}

}
