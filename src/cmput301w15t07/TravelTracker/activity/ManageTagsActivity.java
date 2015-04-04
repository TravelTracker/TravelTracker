package cmput301w15t07.TravelTracker.activity;

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

import java.util.ArrayList;
import java.util.Collection;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.serverinterface.MultiCallback;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ManageTagsListAdapter;
import cmput301w15t07.TravelTracker.util.MultiSelectListener;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.util.Observer;
import cmput301w15t07.TravelTracker.util.MultiSelectListener.multiSelectMenuListener;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity for a Claimant to manage his/her Tags.
 * 
 * @author kdbanman,
 *         colp,
 *         therabidsquirel,
 *         braedy
 *
 */
public class ManageTagsActivity extends TravelTrackerActivity implements Observer<DataSource> {
    /** Multicallback key for tags. */
    private static final int MULTI_TAGS_KEY = 0;
    
    /** Multicallback key for user. */
    private static final int MULTI_USER_KEY = 1;

    /** Data about the logged-in user. */
    private UserData userData;
    
    /** The actual user. */
    private User user;
    
    /** Adapter for the list. */
    private ManageTagsListAdapter adapter;

    /** The EditText field where new Tag titles are entered. */
    private EditText titleEditText;

    /** The button pressed to add a Tag. */
    private Button addTagButton;

    /** The list view of Tags */
    private ListView tagListView;
    
    /**
     * The collection of current tags, used to make sure no it tags are duplicated.
     */
    private Collection<Tag> tags;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_tags_menu, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.manage_tags_sign_out:
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
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);
        appendNameToTitle(userData.getName());
        
        // Make adapter
        adapter = new ManageTagsListAdapter(this);
        
        datasource.addObserver(this);
	}

    /**
     * Update the activity when the dataset changes.
     * Called in onResume() and update(DataSource observable).
     */
    @Override
    public void updateActivity() {
        // Show loading circle
        setContentView(R.layout.loading_indeterminate);
        
        // Multicallback to get tags and user, then update UI and adapter
        MultiCallback multi = new MultiCallback(new UpdateDataCallback());
        
        // Get tags and user
        datasource.getAllTags(multi.<Collection<Tag>>createCallback(MULTI_TAGS_KEY));
        datasource.getUser(userData.getUUID(), multi.<User>createCallback(MULTI_USER_KEY));
        
        // Notify ready
        multi.ready();
    }

    /**
     * Sets the content view to the activity view and then gets all of the
     * widgets of the UI.
     */
    public void updateUI() {
        setContentView(R.layout.manage_tags_activity);
        
        titleEditText = (EditText) findViewById(R.id.manageTagsNewTagEditText);
        
        addTagButton = (Button) findViewById(R.id.manageTagsAddButton);
        addTagButton.setOnClickListener(new AddTagOnClickListener());
        
        tagListView = (ListView) findViewById(R.id.manageTagsTagListView);
        tagListView.setAdapter(adapter);
        tagListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        tagListView.setMultiChoiceModeListener(new MultiSelectListener(new ContextMenuListener(),
                                                                       R.menu.tags_list_context_menu));
        
        tagListView.setOnItemClickListener(new TagRenameListener());
        
        onLoaded();
    }

    public void deleteTags(ArrayList<Integer> selectedItems) {
        // Delete in place is bad
        ArrayList<Tag> delete = new ArrayList<Tag>();
        for (Integer i : selectedItems) {
            delete.add(adapter.getItem(i));
        }
        
        // Real delete loop
        ResultCallback<Void> cb = new DeleteTagCallback();
        for (Tag t : delete) {
            datasource.deleteTag(t.getUUID(), cb);
        }
    }
    
    /**
     * Multicallback intended to get all data necessary for this activity upon
     * update or resume.
     */
    public class UpdateDataCallback implements 
    ResultCallback<SparseArray<Object>> {
        /** 
         * Save the user, ask the adapter to rebuild the list, and then update
         * the UI.
         */
        @SuppressWarnings("unchecked")
        @Override
        public void onResult(SparseArray<Object> result) {
            // Save data
            user = (User) result.get(MULTI_USER_KEY);
            tags = (Collection<Tag>) result.get(MULTI_TAGS_KEY);

            // Update UI
            adapter.rebuildList(tags, userData.getUUID());
            ManageTagsActivity.this.updateUI();
        }

        @Override
        public void onError(String message) {
            Toast.makeText(ManageTagsActivity.this, message,
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Callback for deleting Tags. Does nothing but ignore currently.
     *
     */
    class DeleteTagCallback implements ResultCallback<Void> {
        // Do nothing
        @Override
        public void onResult(Void result) {}

        @Override
        public void onError(String message) {
            Toast.makeText(ManageTagsActivity.this, message,
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Listener for add Tag button.
     */
    public class AddTagOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            final String title = titleEditText.getText().toString();
            for (Tag t : tags) {
                if (title.equals(t.getTitle())) {
                    Toast.makeText(ManageTagsActivity.this,
                            title + getString(R.string.manage_tags_tag_exists),
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
            
            datasource.addTag(user, new ResultCallback<Tag>() {
                @Override
                public void onResult(Tag result) {
                    result.setTitle(titleEditText.getText().toString());
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(ManageTagsActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    /**
     * Listens to items being clicked in the Tag ListView. Calls up the rename
     * AlertDialog.
     */
    public class TagRenameListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            Tag selected = adapter.getItem(position);
            
            // Begin making alert dialog
            AlertDialog.Builder adb =
                    new AlertDialog.Builder(ManageTagsActivity.this);
            
            // Create edit text and limit to one line of text
            EditText input = new EditText(ManageTagsActivity.this);
            input.setText(selected.getTitle());
            input.setMaxLines(1);
            input.setLines(1);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            
            adb.setView(input);
            
            // Don't give listener here, see lower comment for why
            adb.setPositiveButton(
                    getString(R.string.manage_tags_dialog_rename), null);
            
            // No listener at all, just want default dismiss dialog behaviour
            adb.setNegativeButton(
                    getString(R.string.manage_tags_dialog_cancel), null);
            
            AlertDialog dialog = adb.create();
            dialog.show();
            
            /* http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
             * Override onClickListener here to prevent the dialog from being
             * closed before we're done with it, link above has better
             * explanation.
             */
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(
                        new RenameDialogListener(dialog, input, selected));
        }
    }
    
    /**
     * Listens for the positive (rename) button in the tag rename dialog.
     * 
     * Replaces the default listener which always calls dialog.dismiss and so
     * there must be a case where this dismisses the dialog (the inputted name
     * is not a duplicate).
     * 
     * For more info:
     * http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
     */
    public class RenameDialogListener implements OnClickListener {
        private AlertDialog dialog;
        private EditText input;
        private Tag selected;

        public RenameDialogListener(AlertDialog dialog, EditText input, 
                Tag selected) {
            this.dialog = dialog;
            this.input = input;
            this.selected = selected;
        }
        
        @Override
        public void onClick(View v) {
            String title = input.getText().toString();
            for (Tag t : tags) {
                // Ignore renaming to yourself? Same as cancel.
                if (selected.equals(t)) continue;
                
                // Notify on duplicate name and don't close
                if (title.equals(t.getTitle())) {
                    Toast.makeText(ManageTagsActivity.this,
                            title + getString(R.string.manage_tags_tag_exists),
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
            
            // Set to new title
            selected.setTitle(title);
            
            // Dismiss dialog
            dialog.dismiss();
        }
    }

    
    /**
     *  Listener for Context menu 
     */
    class ContextMenuListener implements multiSelectMenuListener {
        @Override
        public void menuButtonClicked(ArrayList<Integer> selectedTags,
                MenuItem item) {
            switch (item.getItemId()) {
            case R.id.tags_list_context_delete:
                deleteTags(selectedTags);
                break;
            default:
                break;
            }
        }
    }
}
