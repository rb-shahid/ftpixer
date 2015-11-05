package com.byteshaft.ftpixer;

    import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Button mScanButton;
    public static Button mPicButton;
    private TextView mPhotoCount;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private ScannerActivity scannerActivity;
    public static EditText scannerEditText;
    private static RadioButton jobNumberRadioButton;
    private static RadioButton regNoRadioButton;
    private RadioGroup radioGroup;
    public static RadioGroup radioGroupTwo;
    private static File sFileSavedImage;
    private ArrayList<String> arrayList;
    private static String sImageNameAccordingToRadioButton;
    private static String sTextFromScannerEditText;
    public static Activity mainActivity;
    private int mPreviousCounterValue;
    private Menu actionBarMenu;
    private EditText employeeEditText;
    private CheckBox employeeCheckBox;
    private String employeeNumber;
    private boolean newSession = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        newSession = true;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0453A2")));
        setContentView(R.layout.activity_main);
        arrayList = new ArrayList<>();
        scannerActivity = new ScannerActivity();
        scannerEditText = (EditText) findViewById(R.id.barCodeEditText);
        employeeEditText = (EditText) findViewById(R.id.employee_edittext);
        employeeCheckBox = (CheckBox) findViewById(R.id.checkbox_supplementary_work);
        scannerEditText.setFocusable(false);
        scannerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editText = scannerEditText.getText().toString().trim();
                employeeNumber = employeeEditText.getText().toString().trim();
                if (employeeNumber.isEmpty() || employeeNumber.length() == 0 || employeeNumber.equals("")) {
                    mPicButton.setVisibility(View.GONE);
                }
                 else if (editText.isEmpty() || editText.length() == 0 || editText.equals("")) {
                    mPicButton.setVisibility(View.GONE);
                    actionBarMenu.findItem(R.id.action_done).setVisible(false);
                } else if (arrayList.size() > 0 && editText.length() >= 6) {
                    mPicButton.setVisibility(View.VISIBLE);
                    actionBarMenu.findItem(R.id.action_done).setVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        jobNumberRadioButton = (RadioButton) findViewById(R.id.job_no_radio_button);
        regNoRadioButton = (RadioButton) findViewById(R.id.reg_no_radio_button);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroupTwo = (RadioGroup) findViewById(R.id.radio_group_one);
        radioGroupTwo.setOnCheckedChangeListener(this);
        radioGroupTwo.setEnabled(false);
        mScanButton = (Button) findViewById(R.id.scan_button);
        mPicButton  = (Button) findViewById(R.id.pic_button);
        mPhotoCount = (TextView) findViewById(R.id.photo_count);
        if (arrayList.size() == 0) {
            mPhotoCount.setVisibility(View.INVISIBLE);
        }
        mScanButton.setOnClickListener(this);
        mPicButton.setOnClickListener(this);
        scannerEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(7)});
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (jobNumberRadioButton.isChecked()) {
                    scannerEditText.setFocusableInTouchMode(true);
                    scannerEditText.setFocusable(true);
                    scannerEditText.setText("");
                    int maxLength = 7;
                    scannerEditText.setFilters(new InputFilter[]
                            {new InputFilter.LengthFilter(maxLength)});
                    System.out.println("Job Number Button Checked");
                } else if (regNoRadioButton.isChecked()) {
                    scannerEditText.setFocusable(true);
                    scannerEditText.setFocusableInTouchMode(true);
                    scannerEditText.setText("");
                    int maxLength = 7;
                    scannerEditText.setFilters(new InputFilter[]
                            {new InputFilter.LengthFilter(maxLength)});
                    System.out.println("Registration Button Checked");
                }
            }
        });
        Helpers.saveCounterValue(0);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        actionBarMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        scannerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if(str.length() > 0 && str.startsWith(" ")){
                    Log.v("","Cannot begin with space");
                    scannerEditText.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

            if (radioGroupTwo.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Claim type must be selected", Toast.LENGTH_LONG).show();
                return false;
            } else if (arrayList.size() == 0) {
                Toast.makeText(this, "You must take atleast one photo", Toast.LENGTH_LONG).show();
                return false;
            }

            if (AppGlobals.isInternetConnected()) {
                String networkPreference = AppGlobals.getInternetPreference();
                if (networkPreference != null) {
                    if ("wifi".equals(networkPreference) && !AppGlobals.isWifiConnected()) {
                        Toast.makeText(this, "Uploading will only work on wifi", Toast.LENGTH_LONG).show();
                        return false;
                    } else if ("data".equals(networkPreference) && !AppGlobals.isMobileDataConnected()) {
                        Toast.makeText(this, "Uploading will only work on Mobile data", Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        Intent intent = new Intent(this, UploadActivity.class);
                        intent.putExtra("images", arrayList);
                        startActivity(intent);
                    }
                }
            } else {
                Toast.makeText(this, "No internet connected enabled, please enable it", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_button:
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivity(intent);
                radioGroup.clearCheck();
                break;
            case R.id.pic_button:
                mPreviousCounterValue = Helpers.getPreviousCounterValue();
                File createNextFolder;
                ArrayList<File> arrayList = new ArrayList<>();
                if (newSession) {
                    Helpers.saveCounterValue(1);
                    File internalFolder = new File(Environment.getExternalStorageDirectory(),
                            File.separator + "Android/data" + File.separator + getPackageName());
                    if (!internalFolder.exists()) {
                        internalFolder.mkdirs();
                    }
                    File[] allFolders = internalFolder.listFiles();
                    for (File file: allFolders) {
                        if (file.isDirectory()) {
                            arrayList.add(file);
                        }
                    }
                    int counter = arrayList.size();
                    String folderName = null;
                    if (!scannerEditText.getText().toString().isEmpty()) {
                        folderName = scannerEditText.getText().toString();
                    }
                    System.out.println(folderName);
                    createNextFolder = new File(Environment.getExternalStorageDirectory(),
                            File.separator + "Android/data" + File.separator + getPackageName() +
                                    File.separator + (folderName));
                    createNextFolder.mkdirs();
                    AppGlobals.setCurrentPath(createNextFolder);
                    newSession = false;
                }
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                sTextFromScannerEditText = scannerEditText.getText().toString();
                if (employeeCheckBox.isChecked()) {
                    File image = new File(AppGlobals.getCurrentPath(),  sTextFromScannerEditText + "_" + employeeNumber + "_" + "S" + "_" +  sImageNameAccordingToRadioButton
                            +  "_" +
                            getPreviousValueAndAddOne(mPreviousCounterValue) +  "_" + Helpers.getTimeStamp() + ".jpg");
                    Uri uriSavedImage = Uri.fromFile(image);
                    sFileSavedImage = image;
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                } else {
                    File image1 = new File(AppGlobals.getCurrentPath(),  sTextFromScannerEditText + "_" + employeeNumber + "_" + "N" + "_" +  sImageNameAccordingToRadioButton
                            + "_" +
                            getPreviousValueAndAddOne(mPreviousCounterValue) + "_" + Helpers.getTimeStamp() + ".jpg");
                    Uri uriSavedImage = Uri.fromFile(image1);
                    sFileSavedImage = image1;
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                }
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(resultCode);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            arrayList.add(sFileSavedImage.getAbsolutePath());
            if (arrayList.size() > 0) {
                Helpers.saveCounterValue(mPreviousCounterValue + 1);
                mPhotoCount.setVisibility(View.VISIBLE);
                mPhotoCount.setText(String.valueOf(arrayList.size()));
                actionBarMenu.findItem(R.id.action_done).setVisible(true);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String string = scannerEditText.getText().toString();
        if (TextUtils.isEmpty(string)) {
            scannerEditText.setError("This field must contain at least 6 characters");
        } else if (!TextUtils.isEmpty(string)){
            mPicButton.setVisibility(View.VISIBLE);
        }
        switch (checkedId) {
            case R.id.radio_workshop_strip:
                sImageNameAccordingToRadioButton = "WST";
                System.out.println(sImageNameAccordingToRadioButton);
                Log.i("RADIO", "Strip selected");
                break;

            case R.id.radio_workshop_panel:
                sImageNameAccordingToRadioButton = "WPN";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_workshop_paint:
                sImageNameAccordingToRadioButton = "WPI";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_workshop_refit:
                sImageNameAccordingToRadioButton = "WRE";
                System.out.println(sImageNameAccordingToRadioButton);
                break;
            case R.id.radio_workshop_tracker:
                sImageNameAccordingToRadioButton = "TRA";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_vda_claim:
                sImageNameAccordingToRadioButton = "VCL";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_vda_non_claim:
                sImageNameAccordingToRadioButton = "VNO";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_vda_extra_work:
                sImageNameAccordingToRadioButton = "VEX";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_pre_inspection_claim_related:
                sImageNameAccordingToRadioButton = "PCL";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_pre_inspection_non_claim:
                sImageNameAccordingToRadioButton = "PNO";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_mobile_claim:
                sImageNameAccordingToRadioButton = "MCL";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_mobile_non_claim:
                sImageNameAccordingToRadioButton = "MNO";
                System.out.println(sImageNameAccordingToRadioButton);
                break;

            case R.id.radio_mobile_extra_work:
                sImageNameAccordingToRadioButton = "MEX";
                System.out.println(sImageNameAccordingToRadioButton);
                break;
            case R.id.radio_pre_inspection:
                sImageNameAccordingToRadioButton = "API";
                System.out.println(sImageNameAccordingToRadioButton);
                break;
            case R.id.radio_courtesy_car:
                sImageNameAccordingToRadioButton = "ACC";
                System.out.println(sImageNameAccordingToRadioButton);

        }
    }

    private String getPreviousValueAndAddOne(int previousValue) {
        switch (String.valueOf(previousValue).length()) {
            case 1:
                return "00" + (previousValue + 1);
            case 2:
                return "0" + (previousValue + 1);
            case 3:
                return String.valueOf(previousValue + 1);
            default:
                return "00" + (previousValue + 1);
        }
    }
}