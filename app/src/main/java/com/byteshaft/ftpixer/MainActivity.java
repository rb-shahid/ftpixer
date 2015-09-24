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
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Button mScanButton;
    private Button mPicButton;
    private TextView mPhotoCount;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private ScannerActivity scannerActivity;
    public static EditText scannerEditText;
    private static RadioButton jobNumberRadioButton;
    private static RadioButton regNoRadioButton;
    private RadioGroup radioGroup;
    private RadioGroup radioGroupTwo;
    private static File sFileSavedImage;
    private ArrayList<String> arrayList;
    private static String sImageNameAccordingToRadioButton;
    private static String sTextFromScannerEditText;
    public static Activity mainActivity;
    private int mPreviousCounterValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0453A2")));
        setContentView(R.layout.activity_main);
        arrayList = new ArrayList<>();
        scannerActivity = new ScannerActivity();
        scannerEditText = (EditText) findViewById(R.id.barCodeEditText);
        scannerEditText.setFocusable(false);
        jobNumberRadioButton = (RadioButton) findViewById(R.id.job_no_radio_button);
        regNoRadioButton = (RadioButton) findViewById(R.id.reg_no_radio_button);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroupTwo = (RadioGroup) findViewById(R.id.radio_group_one);
        radioGroupTwo.setOnCheckedChangeListener(this);
        mScanButton = (Button) findViewById(R.id.scan_button);
        mPicButton  = (Button) findViewById(R.id.pic_button);
        mPhotoCount = (TextView) findViewById(R.id.photo_count);
        if (arrayList.size() == 0) {
            mPhotoCount.setVisibility(View.INVISIBLE);
        }
        mScanButton.setOnClickListener(this);
        mPicButton.setOnClickListener(this);
        scannerEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(6)});
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (jobNumberRadioButton.isChecked()) {
                    scannerEditText.setFocusableInTouchMode(true);
                    scannerEditText.setFocusable(true);
                    scannerEditText.setText("");
                    int maxLength = 6;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, UploadActivity.class);
            intent.putExtra("images", arrayList);
            startActivity(intent);
            System.out.println("done");
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
                break;
            case R.id.pic_button:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/");
                imagesFolder.mkdirs();
                mPreviousCounterValue = 0;
                mPreviousCounterValue = Helpers.getPreviousCounterValue();
                sTextFromScannerEditText = scannerEditText.getText().toString();
                File image = new File(imagesFolder, sImageNameAccordingToRadioButton + "_"
                        + sTextFromScannerEditText +  "_" +
                        getPreviousValueAndAddOne(mPreviousCounterValue) + ".jpg");
                Uri uriSavedImage = Uri.fromFile(image);
                sFileSavedImage = image;
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
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
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mPicButton.setVisibility(View.VISIBLE);
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