package cse403.blast;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cse403.blast.Data.Constants;
import cse403.blast.Data.LocationHandler;
import cse403.blast.Model.Event;
import cse403.blast.Model.User;
import cse403.blast.Support.DatePickerFragment;
import cse403.blast.Support.MinMaxInputFilter;
import cse403.blast.Support.TimePickerFragment;

/**
 * CreateEventActivity allows a user to create a new Event. After inputting valid data for each
 * of the Event fields, the user clicks "Blast It!" to publish the Event and make it visible to
 * other users within the appropriate location radius. The Event is then displayed under the device
 * user's "Blasts You Created" section of the main page's drawer.
 */
public class CreateEventActivity extends AppCompatActivity {

    private String formattedAddress = "";
    private Location coordinates = null;
    private Spinner category;
    private Button submitButton;
    private Button cancelButton;
    private EditText titleText;
    private EditText descText;
    private EditText dateText;
    private EditText timeText;
    private EditText locText;
    private EditText limitText;
    private EditText date;
    private EditText time;
    private int userDay;
    private int userMonth;
    private int userYear;
    private int userHour;
    private int userMin;
    private User currentUser;
    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferenceEditor;
    private Intent createEventIntent;

    private final String TAG = "CreateEventActivity";

    /**
     * Displays the user input fields required to create an event. This includes
     * title, category, description, date/time, location, limit, and the button.
     * A tutorial is displayed if the current user is a new user.
     * @param savedInstanceState: saved stated of this Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(0);

        createEventIntent = getIntent();
        if (createEventIntent.getBooleanExtra("edit", false)) {
            setTitle("Edit Event");
        } else {
            setTitle("New Event");
        }


        category = (Spinner) findViewById(R.id.create_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.category_dropdown_item);
        category.setAdapter(adapter);
        addCategoryListener();

        submitButton = (Button) findViewById(R.id.create_submit_button);
        cancelButton = (Button) findViewById(R.id.create_cancel_button);
        titleText = (EditText) findViewById(R.id.create_title);
        descText = (EditText) findViewById(R.id.create_description);
        locText = (EditText) findViewById(R.id.create_location);
        limitText = (EditText) findViewById(R.id.create_limit);
        date = (EditText) findViewById(R.id.create_date);
        time = (EditText) findViewById(R.id.create_time);
        preferenceSettings = getApplicationContext().getSharedPreferences("blastPrefs", 0);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivityIntent = new Intent(CreateEventActivity.this, MainActivity.class);
                startActivity(mainActivityIntent);
                deleteEvent();
            }
        });

        /* TUTORIAL:
         * displays the tutorial once if the current user is a new user
         */
        View tutorialCreate = findViewById(R.id.tutorial_create);
        if (preferenceSettings.getBoolean("initialCreateLaunch", true)) {
            tutorialCreate.setVisibility(View.VISIBLE);
            // Disable all possible input
            submitButton.setEnabled(false);
            cancelButton.setEnabled(false);
            titleText.setEnabled(false);
            descText.setEnabled(false);
            locText.setEnabled(false);
            limitText.setEnabled(false);
            date.setEnabled(false);
            time.setEnabled(false);
            titleText.setFocusable(false);
            descText.setFocusable(false);
            locText.setFocusable(false);
            limitText.setFocusable(false);
            date.setFocusable(false);
            time.setFocusable(false);
        } else {
            tutorialCreate.setVisibility(View.GONE);
        }

        tutorialCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                preferenceSettings.edit().putBoolean("initialCreateLaunch", false).apply();
                // enable all possible input
                submitButton.setEnabled(true);
                cancelButton.setEnabled(true);
                titleText.setEnabled(true);
                descText.setEnabled(true);
                locText.setEnabled(true);
                limitText.setEnabled(true);
                date.setEnabled(true);
                time.setEnabled(true);
                titleText.setFocusable(true);
                titleText.setFocusableInTouchMode(true);
                descText.setFocusable(true);
                descText.setFocusableInTouchMode(true);
                locText.setFocusable(true);
                locText.setFocusableInTouchMode(true);
                limitText.setFocusable(true);
                limitText.setFocusableInTouchMode(true);
                date.setFocusable(true);
                date.setFocusableInTouchMode(true);
                time.setFocusable(true);
                time.setFocusableInTouchMode(true);
            }
        });

        /* REAL CREATE */

        // sets up the listener for displaying the date picker
        dateText = (EditText) findViewById(R.id.create_date);
        dateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    showDatePickerDialog(v);
                return false;
            }
        });

        // sets up the listener for displaying the time picker
        timeText = (EditText) findViewById(R.id.create_time);
        timeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    showTimePickerDialog(v);
                return false;
            }
        });

        // adds validation listeners
        addFieldValidationListeners();

        setFieldHints();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (createEventIntent.getBooleanExtra("edit", true)) {
            Event event = (Event) createEventIntent.getSerializableExtra("event");
            Log.i("EVENT TIME", "" + event.getEventTime().getTime());

            // Disable and enable certain fields, prefill event information
            String eventCat = event.getCategory().toLowerCase();
            eventCat = eventCat.substring(0, 1).toUpperCase() + eventCat.substring(1);
            category.setSelection(((ArrayAdapter)category.getAdapter()).getPosition(eventCat));
            titleText.setEnabled(false);
            titleText.setText(event.getTitle());
            descText.setText(event.getDesc());
            dateText.setText(event.retrieveEventDateString());
            timeText.setText(event.retrieveEventTimeString());
            locText.setEnabled(false);
            locText.setText(event.getLocation());
            limitText.setText("" + event.getLimit());
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText(getString(R.string.create_cancel_button));
            submitButton.setText(getString(R.string.create_save_button));

        } else {
            // enable non-editing fields
            titleText.setEnabled(true);
            locText.setEnabled(true);
            cancelButton.setVisibility(View.GONE);
            submitButton.setText(getString(R.string.create_blast_button));
        }
        Log.i(TAG, "Done creating page");


    }

    /**
     * Gets the latitude and longitude of the current user
     * @return the Location of the current user
     */
    public Location getUserLocation() {
        LocationManager lm = (LocationManager) (getSystemService(Context.LOCATION_SERVICE));

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double lng = -122.3079;
        double lat = 47.6539;
        if (location != null) {
            lng = location.getLongitude();
            lat = location.getLatitude();
        }

        Location userLoc = new Location("userLocation");
        userLoc.setLatitude(lat);
        userLoc.setLongitude(lng);
        return userLoc;
    }

    // VALIDATION METHODS

    // sets up the listeners for all the fields
    private void addFieldValidationListeners() {
        addSubmitButtonClickListener();
        addTitleFocusListener();
        addDescriptionFocusListener();
        addDateFocusListener();
        addTimeFocusListener();
        addLocationFocusListener();
        addLimitFocusListener();
        // restrict limit to be between 1 and 9999
        limitText.setFilters(new InputFilter[]{new MinMaxInputFilter(1, 9999)});
    }

    /**
     * Verifies field input based on the view's field id
     *
     * @param v The view that the field id comes from
     * @return true if the field passes verification, false otherwise
     */
    private boolean verify(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.create_submit_button :   // checks that user has filled in all fields
                if (!isEmpty(titleText) && !isEmpty(descText) && !isEmpty(dateText) &&
                        !isEmpty(timeText) && !isEmpty(locText) && !isEmpty(limitText)) {
                    return true;
                } else {
                    notifyUser("A field is empty");
                    return false;
                }
            case R.id.create_title :
                if (isEmpty(titleText)) {
                    notifyUser("Title is empty");
                    return false;
                }
                return true;
            case R.id.create_description :
                if (isEmpty(descText)) {
                    notifyUser("Description is empty");
                    return false;
                }
                return true;
            case R.id.create_date :
                if (isEmpty(dateText)) {
                    notifyUser("Date is empty");
                    return false;
                }
                return true;
            case R.id.create_time :
                if (isEmpty(timeText)) {
                    notifyUser("Time is empty");
                    return false;
                }
                return true;
            case R.id.create_location :
                if (isEmpty(locText)) {
                    notifyUser("Location is empty");
                    return false;
                }
                return true;
            case R.id.create_limit :   // check that limit is not empty and that it is an integer >= 1
                if (isEmpty(limitText)) {
                    notifyUser("Limit is empty");
                    return false;
                } else {
                    // check that input is a number
                    String input = limitText.getText().toString();
                    for (int i = 0; i < input.length(); i++) {
                        if (!Character.isDigit(input.charAt(i))) {
                            notifyUser("Limit needs to be a number");
                            return false;
                        }
                    }
                    // input is a number, check if number entered is valid
                    int userEnteredLimit = Integer.parseInt(input);
                    if (userEnteredLimit < 1) {
                        notifyUser("Limit needs to be at least 1");
                        return false;
                    }
                }
                return true;
            default :   // should not hit default case, so return false if we do
                return false;
        }
    }

    //Used to hide keyboard on touch elsewhere
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }


    /**
     * Checks if the user field has been filled in
     *
     * @param field The field to check
     * @return true if the field is empty or just whitespace, false otherwise
     */
    private boolean isEmpty(EditText field) {
        String text = field.getText().toString();
        if (text == null) {
            return true;
        }
        return text.trim().length() == 0;
    }

    /**
     * Displays a temporary message at the bottom of the screen
     *
     * @param message The message to be displayed
     */
    private void notifyUser(String message) {
        Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     *  adds click listener to submitButton to trigger verification and add event to database
     */
    private void addSubmitButtonClickListener() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // succeeds if the user has filled in all fields
                if (verify(v)) {
                    Intent mainActivityIntent = new Intent(CreateEventActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    // calls the method that will add the event to the database
                    addEvent();
                }
            }
        });
    }

    /**
     * adds focus change listener to titleText to trigger verification
     */
    private void addTitleFocusListener() {
        titleText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    /**
     * adds focus change listener to descText to trigger verification
     */
    private void addDescriptionFocusListener() {
        descText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    /**
     * adds focus change listener to dateText to trigger verification
     */
    private void addDateFocusListener() {
        dateText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    /**
     * adds focus change listener to timeText to trigger verification
     */
    private void addTimeFocusListener() {
        timeText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    /**
     * adds focus change listener to locText to trigger verification
     */
    private void addLocationFocusListener() {
        locText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);

                    // Location things
                    Location userLocation = getUserLocation();

                    LocationHandler instance = new LocationHandler();
                    final Map<LocationHandler.LocationResult, Location> map = instance.getMatchingLoc(locText.getText().toString(), userLocation);

                    List<LocationHandler.LocationResult> resultList =
                            new ArrayList<LocationHandler.LocationResult>(map.keySet());

                    final ListView listView = (ListView) findViewById(R.id.location_list);
                    ArrayAdapter<LocationHandler.LocationResult> adapter =
                            new ArrayAdapter<LocationHandler.LocationResult>(CreateEventActivity.this,
                            android.R.layout.simple_list_item_1, resultList);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            LocationHandler.LocationResult locationAtPos = (LocationHandler.LocationResult) parent.getItemAtPosition(position);
                            locText.setText(locationAtPos.description);
                            formattedAddress = locationAtPos.formattedAddress;
                            coordinates = map.get(locationAtPos);

                            Log.i(TAG, "got em: " + formattedAddress + " coordinate : " + coordinates);

                            ((ViewManager) listView.getParent()).removeView(listView);
                        }
                    });



                }
            }
        });
    }

    /**
     * adds focus change listener to limitText to trigger verification
     */
    private void addLimitFocusListener() {
        limitText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    // DIALOG METHODS

    /**
     * Appends a red star to the hint text of all mandatory fields
     */
    private void setFieldHints() {
        setFieldHint(titleText);
        setFieldHint(descText);
        setFieldHint(locText);
        setFieldHint(limitText);
        setFieldHint(dateText);
        setFieldHint(timeText);
    }

    /**
     * Appends a red star to the hint text of the given field
     *
     * @param field The field to append to
     */
    private void setFieldHint(EditText field) {
        String hint = field.getHint().toString();
        String extra = " *";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(hint);
        int start = builder.length();
        builder.append(extra);
        int end = builder.length();
        builder.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        field.setHint(builder);
    }

    /**
     * Adds an on-change listener to the category dropdown
     */
    private void addCategoryListener() {
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ImageView image = (ImageView) findViewById(R.id.create_image);
                String selected = category.getSelectedItem().toString();
                if (selected.equals("Social")) {
                    image.setImageResource(R.drawable.social);
                } else if (selected.equals("Active")) {
                    image.setImageResource(R.drawable.active);
                } else if (selected.equals("Food")) {
                    image.setImageResource(R.drawable.food);
                } else if (selected.equals("Entertainment")) {
                    image.setImageResource(R.drawable.entertainment);
                } else {
                    image.setImageResource(R.drawable.other);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // there will never be nothing selected
            }
        });
    }

    /**
     * Displays the date picker
     *
     * @param v The View to display to
     */
    private void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(
                new Date(Calendar.getInstance().getTimeInMillis()), dateSetListener);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Date picker on set listener
     */
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            EditText tackDate = (EditText) findViewById(R.id.create_date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cal.set(Calendar.YEAR, year);

            Date dateRepresentation = cal.getTime();

            String dayOfWeek = dateFormat.format(dateRepresentation);
            String month = monthFormat.format(dateRepresentation);
            tackDate.setText(dayOfWeek + ", " + month + " " + dayOfMonth);
            Log.i("Tag", "set");
            // make the time picker display after choosing a date
            getTimePickerDialog().show(getSupportFragmentManager(), "timePicker");
            userYear = year;
            userMonth = monthOfYear;
            userDay = dayOfMonth;
        }
    };

    /**
     * Displays the time picker
     *
     * @param v The View to display to
     */
    private void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(
                new Date(Calendar.getInstance().getTimeInMillis()), timeSetListener);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * Time picker on set listener
     *
     * @return TimePickerDialog.OnTimeSetListener
     */
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            EditText tackTime = (EditText) findViewById(R.id.create_time);
            String minPrefix = "";
            String timeOfDay = "";
            if (minute < 10) minPrefix = "0";
            if (hourOfDay >= 12) {
                timeOfDay = " PM";
            } else {
                timeOfDay = " AM";
            }
            tackTime.setText("" + (hourOfDay % 12) + ":" + minPrefix + minute + timeOfDay);
            userHour = hourOfDay;
            userMin = minute;
        }
    };

    /**
     * Gets the time picker
     * This helps make the user interaction simpler by directly opening time after date
     *
     * @return DialogFragment
     */
    private DialogFragment getTimePickerDialog() {
        DialogFragment newFragment = TimePickerFragment.newInstance(
                new Date(Calendar.getInstance().getTimeInMillis()), timeSetListener);
        return newFragment;
    }

    // DATABASE METHODS

    /**
     * Adds event to the Events firebase database containing information for:
     * unique date ID
     * event title
     * description
     * guest list limit
     * location
     * date
     */
    public void addEvent() {
        // Get the reference to the root node in Firebase
        Firebase ref = new Firebase(Constants.FIREBASE_URL);

        // Get current event if it is passed in by the intent
        Event ev = (Event) createEventIntent.getSerializableExtra("event");

        // Get user-entered data: title, description, limit, location
        String userEnteredTitle = titleText.getText().toString();
        String userEnteredDesc = descText.getText().toString();
        int userEnteredLimit = Integer.parseInt(limitText.getText().toString());
        String userEnteredLoc = locText.getText().toString();
        String userEnteredCategory = category.getSelectedItem().toString().toUpperCase();

        double userEnteredLat = Double.NaN;
        double userEnteredLong = Double.NaN;
        if (coordinates != null) {
            userEnteredLat = coordinates.getLatitude();
            userEnteredLong = coordinates.getLongitude();
        }

        // Get user-entered date
        Calendar calendar = Calendar.getInstance();
        calendar.set(userYear, userMonth, userDay, userHour, userMin);
        Date userEnteredDate = calendar.getTime();

        // If we're in edit mode, we might have to adjust the userEnteredDate
        if (createEventIntent.getBooleanExtra("edit", true)) {
            //Refill location
            formattedAddress = ev.getFormattedAddress();
            userEnteredLat = ev.getLatitude();
            userEnteredLong = ev.getLongitude();

            Calendar cal = Calendar.getInstance();
            cal.setTime(ev.getEventTime());

            if (userYear == 0 && userHour == 0) {
                userEnteredDate = ev.getEventTime();
            } else {
                if (userYear == 0) {
                    // the date was not edited
                    userYear = cal.get(Calendar.YEAR);
                    userMonth = cal.get(Calendar.MONTH);
                    userDay = cal.get(Calendar.DAY_OF_MONTH);
                }
                if (userHour == 0) {
                    // the time was not edited
                    userHour = cal.get(Calendar.HOUR_OF_DAY);
                    userMin = cal.get(Calendar.MINUTE);
                }
                cal.set(userYear, userMonth, userDay, userHour, userMin);
                userEnteredDate = cal.getTime();
            }
        }

        // Log string for entered date
        Log.i("TestMyDate", userEnteredDate.toString());


        // Grab User object from SharedPreferences file
        preferenceSettings = getApplicationContext().getSharedPreferences("blastPrefs", 0);
        preferenceEditor = preferenceSettings.edit();

        Gson gson = new Gson();
        String json = preferenceSettings.getString("MyUser", "");
        Log.i("DetailActivity", "JSON: " + json);
        currentUser = gson.fromJson(json, User.class);

        // Create event object using user-submitted data
        Event userEvent = new Event(currentUser.getFacebookID(), userEnteredTitle, userEnteredDesc,
                userEnteredLoc, formattedAddress, userEnteredLat, userEnteredLong, userEnteredLimit,
                userEnteredDate, userEnteredCategory);

        // Generate unique ID for event
        Firebase eventRef = ref.child("events");

        Set<String> origAttendees = new HashSet<>();
        if (ev != null)
            origAttendees = ev.getAttendees(); // Original list of attendees

        // Get reference to ID of event (create an ID if one does not exist);
        Firebase newEventRef;
        if (createEventIntent.getBooleanExtra("edit", true)) {
            newEventRef = new Firebase(Constants.FIREBASE_URL).child("events").child(ev.getId());
        } else {
            newEventRef = eventRef.push();
        }

        String eventId = newEventRef.getKey();
        userEvent.setId(eventId);

        // Add event to DB
        newEventRef.setValue(userEvent);

        // updates user's created events
        currentUser.createEvent(userEvent);
        Firebase userCreatedRef = new Firebase(Constants.FIREBASE_URL).child("users").child(currentUser.getFacebookID()).child("eventsCreated");
        userCreatedRef.setValue(currentUser.getEventsCreated());

        String json2 = gson.toJson(currentUser);
        Log.i(TAG, "JSON: " + json2);
        preferenceEditor.putString("MyUser", json2);
        preferenceEditor.commit();


        if (createEventIntent.getBooleanExtra("edit", true)) {
            // if editing, make sure that the attendee list is the same as before
            Firebase eventAttendeesRef = new Firebase(Constants.FIREBASE_URL).child("events").child(ev.getId()).child("attendees");
            eventAttendeesRef.setValue(origAttendees);
        } else {
            // update attendee list for the event that the user just created
            userEvent.addAttendee(currentUser);
            Firebase userAttendingRef = new Firebase(Constants.FIREBASE_URL).child("events").child(userEvent.getId()).child("attendees");
            userAttendingRef.setValue(userEvent.getAttendees());
        }

        if (createEventIntent.getBooleanExtra("edit", true)) {
            Toast.makeText(CreateEventActivity.this, "Event Saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CreateEventActivity.this, "Event Created", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gives the owner of the event the option to delete it
     */
    public void deleteEvent() {
        final Event event = (Event) createEventIntent.getSerializableExtra("event");

        // get attendees
        Set<String> attendees = event.getAttendees();

        // remove event from each attendee's list of attending events
        for (String attendee : attendees) {
            if (!attendee.equals(event.getOwner())) {
                final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("users").child(attendee).child("eventsAttending");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    /**
                     * For each user attending the event, it queries firebase to find the user's "eventAttending"
                     * list, and removes the event from the list.
                     *
                     * @param snapshot: Takes a snapshot of the current state of the database
                     */
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        GenericTypeIndicator<Set<String>> t = new GenericTypeIndicator<Set<String>>() {};
                        Set<String> eventsAttending = snapshot.getValue(t);
                        if (eventsAttending != null) {
                            eventsAttending.remove(event.getId());
                            ref.setValue(eventsAttending);
                        }
                    }

                    /**
                     * Displays an informative message to the user
                     *
                     * @param error: error raised by Firebase
                     */
                    @Override
                    public void onCancelled(FirebaseError error) {
                        Toast.makeText(CreateEventActivity.this, "Unable to connect.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "error: " + error.getMessage());
                    }
                });
            }
        }

        // remove from owner's list of events created
        String owner = event.getOwner();
        final Firebase ownerRef = new Firebase(Constants.FIREBASE_URL).child("users").child(owner).child("eventsCreated");
        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Queries firebase to find the the owner's "eventsCreated" list, and removes
             * the event from the list.
             * @param snapshot: Takes a snapshot of the current state of the database
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GenericTypeIndicator<Set<String>> t = new GenericTypeIndicator<Set<String>>() {};
                Set<String> eventsCreated = snapshot.getValue(t);
                eventsCreated.remove(event.getId());
                ownerRef.setValue(eventsCreated);
            }

            /**
             * Displays an informative message to the user
             *
             * @param firebaseError: error raised by Firebase
             */
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(CreateEventActivity.this, "Unable to connect.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "error: " + firebaseError.getMessage());
            }
        });

        // remove from db
        Firebase eventRef = new Firebase(Constants.FIREBASE_URL).child("events").child(event.getId());
        eventRef.removeValue();

        notifyUser("Event deleted");
    }
}
