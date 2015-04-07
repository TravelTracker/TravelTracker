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

package cmput301w15t07.TravelTracker.util;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

/**
 * A fragment for picking the date.
 * 
 * Referenced
 * http://stackoverflow.com/a/20923857
 * http://stackoverflow.com/a/14808425
 * on 09/03/15
 * 
 * @author colp
 */
public class DatePickerFragment extends DialogFragment {
	/** Result code for when data is returned properly. */
	static final int RESULT_OK = 0;
	
	/** Extra string ID for date return. */
	static final String DATE_CALENDAR = "cmput301w15t07.TravelTracker.dateCalendar";
	
	/**
	 * Callback interface for results from DatePickerFragment.
	 */
	public interface ResultCallback {
		/**
		 * Called when the date is picked.
		 * @param date The new date picked.
		 */
		void onDatePickerFragmentResult(Date date);
		
		/**
		 * Called when the dialog is cancelled.
		 */
		void onDatePickerFragmentCancelled();
	}
	
	/** The result listener. */
	private ResultCallback callback;
	
	/** The calendar used to manipulate the date. */
	Calendar calendar;
	
	/** Whether the dialog was cancelled. */
	boolean cancelled;
	
	/**
	 * Construct the DatePickerFragment.
	 * 
	 * @param date The date to start with.
	 * @param callback The callback for when the date is set.
	 */
	public DatePickerFragment(Date date, ResultCallback callback) {
	    this.callback = callback;
	    calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    cancelled = false;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    Context context = getActivity();
		
		// Create the dialog
		DatePickerDialog dialog = new DatePickerDialog(context, null,
			calendar.get(Calendar.YEAR),
			calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DAY_OF_MONTH));
		
		// Multi-button setup based on code from:
		// http://stackoverflow.com/a/21529892
		// Accessed on 19/01/15
		
		dialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				    DatePickerDialog dpd = (DatePickerDialog) dialog;
				    DatePicker picker = dpd.getDatePicker();
				    
                    calendar.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
                    callback.onDatePickerFragmentResult(calendar.getTime());
				}
			});
		
		dialog.setButton(Dialog.BUTTON_NEGATIVE, context.getString(android.R.string.cancel),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
                    callback.onDatePickerFragmentCancelled();
				}
			});
		
		return dialog;
	}
}
