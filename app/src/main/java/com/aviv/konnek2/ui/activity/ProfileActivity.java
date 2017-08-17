package com.aviv.konnek2.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aviv.konnek2.AppImageIdPresenter;
import com.aviv.konnek2.AppProfilePresenter;
import com.aviv.konnek2.Konnnek2;
import com.aviv.konnek2.R;
import com.aviv.konnek2.data.preference.AppPreference;
import com.aviv.konnek2.interfaces.ImageIdView;
import com.aviv.konnek2.interfaces.ProfileView;
import com.aviv.konnek2.models.UserModel;
import com.aviv.konnek2.utils.Common;
import com.aviv.konnek2.utils.Constant;
import com.google.android.gms.common.data.DataHolder;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.sample.chat.utils.chat.ChatHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.sample.groupchatwebrtc.utils.Consts;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements ProfileView, ImageIdView {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private EditText profileName1;
    private EditText profileEmail;
    private EditText ProfileMobile;
    private EditText profileDataOfBirth;
    private EditText profileCountry;
    private EditText profileCity;
    private EditText profileZipCode;

    private String mName;
    private String mMobile;
    private String mEmail;
    private String mDataOfBirth;
    private String mGender;
    private String mCity;
    private String mCountry;
    private String mZipcode;
    RadioGroup genderRadio;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private int mDay;
    private int mMonth;
    private int mYear;
    private String mUserChooseTask;
    ImageView imgCalander;
    ImageView profileImage;
    RelativeLayout baseLayout;
    private ProgressBar progress;
    RelativeLayout parent;
    Toolbar toolbar;
    UserModel userModel;
    UserModel getUserModel;
    private AppProfilePresenter appProfilePresenter;
    AppImageIdPresenter appImageIdPresenter;
    private android.app.AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(Constant.HOME + Constant.GREATER_THAN + Constant.USER_PROFILE);
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        initViews();
        setProfileImage();
    }

    private void setProfileImage() {
        String profileImagePath = AppPreference.getProfileImagePath();
        if (getCheckFile(profileImagePath) && profileImagePath != null) {
            Uri userImage = Uri.parse(AppPreference.getProfileImagePath());
            profileImage.setImageURI(userImage);
        } else {
            profileImage.setImageResource(R.drawable.default_image);

        }

    }

    private boolean getCheckFile(String profileImagePath) {
        File file;
        if (profileImagePath == null)
            return false;
        else {
            file = new File(profileImagePath);
            return file.exists();
        }
    }

    private void initViews() {

        appProfilePresenter = new AppProfilePresenter(this);
        appImageIdPresenter = new AppImageIdPresenter(this);
        progress = (ProgressBar) findViewById(R.id.progress);
        profileName1 = (EditText) findViewById(R.id.et_profile_name1);
        profileEmail = (EditText) findViewById(R.id.et_profile_email);
        ProfileMobile = (EditText) findViewById(R.id.et_profile_mobile);
        profileDataOfBirth = (EditText) findViewById(R.id.et_profile_dataOfbirth);
        profileCity = (EditText) findViewById(R.id.et_profile_city);
        profileCountry = (EditText) findViewById(R.id.et_profile_country);
        profileZipCode = (EditText) findViewById(R.id.et_profile_zipcode);
//        genderRadio = (RadioGroup) findViewById(R.id.radio_profile_gender);
        imgCalander = (ImageView) findViewById(R.id.img_profile_calander);
        profileImage = (ImageView) findViewById(R.id.settings_profileImage);
        baseLayout = (RelativeLayout) findViewById(R.id.profile_base);
        setProfileValues();
        imgCalander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dateOfBirth();
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
        profileZipCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        ValidateServer();
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
                return false;
            }
        });

        try {
            if (userModel != null) {
                profileDataUpload();
                for (int i = 0; i < baseLayout.getChildCount(); i++) {
                    View child = baseLayout.getChildAt(i);
                    child.setEnabled(true);
                }
            } else {
//                ValidateServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePickerAlertBox();

            }
        });
    }

    private void selectImage() {


        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    mUserChooseTask = "Take Photo";
                    Common.displayToast("In Progress");
//                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    mUserChooseTask = "Choose from Library";
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    public void ImagePickerAlertBox() {

//        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        LayoutInflater li = LayoutInflater.from(ProfileActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ProfileActivity.this);
        final View view = inflater.inflate(R.layout.item_image_picker, null);
        TextView takePhoto = (TextView) view.findViewById(R.id.image_take_photo);
        TextView image_gallery = (TextView) view.findViewById(R.id.image_gallery);
        TextView text_gallery = (TextView) view.findViewById(R.id.text_cancel);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.displayToast(Constant.TOAST_MESSAGE);

//                    cameraIntent();
            }
        });

        image_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
                alertDialog.dismiss();
            }
        });

        text_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    public void profileUpload(final String filePath) throws IOException {

        File avatar = new File(filePath);
        Boolean fileIsPublic = true;

        QBContent.uploadFileTask(avatar, fileIsPublic, null).performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                progress.setVisibility(View.GONE);

                QBUser currentUser = ChatHelper.getCurrentUser();
                int uploadedFileID = qbFile.getId();
                QBUser user = new QBUser();
                user.setId(currentUser.getId());
                user.setFileId(uploadedFileID);
                String MobileNumber = AppPreference.getMobileNumber();
                String qbUserId = String.valueOf(user.getId());
                String imageId = String.valueOf(user.getFileId());
                appImageIdPresenter.validateImageIdUpload(MobileNumber, qbUserId, imageId);
//                UpUser(user);
                AppPreference.putProfileImagePath(filePath);
                setProfileImage();


            }

            @Override
            public void onError(QBResponseException e) {
            }
        });
    }


    private void galleryIntent() {
        progress.setVisibility(View.VISIBLE);
        Intent intent = new Intent();
        intent.setType(Constant.IMAGE_INTENT_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
//                onSelectFromGalleryResult(data);

                String fileExtension = getSelectedGalleryImagePath(data);
                if (fileExtension.contains(Constant.IMAGE_FORMAT_PNG) || fileExtension.contains(Constant.IMAGE_FORMAT_JPG))
//
                    try {
                        profileUpload(fileExtension);
//
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                else
                    Common.displayToast(Constant.TOAST_FILE_FORMAT_NOT_SUPPORT);

            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + Constant.IMAGE_FORMAT_JPG);

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profileImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")

    public String getSelectedGalleryImagePath(Intent dataIntent) {
        String filePath = "";
        try {
            if (dataIntent == null) {
                return filePath;
            } else {
                Uri selectedImage = null;
                try {
                    if (dataIntent.getData().toString().contains(Constant.IMAGE_FORMAT_PNG) || dataIntent.getData().toString().contains(Constant.IMAGE_FORMAT_JPG)) {
                        filePath = dataIntent.getData().toString();
                        filePath = filePath.replace("file:///", "");
                        return filePath;
                    } else {
                        selectedImage = Uri.parse(dataIntent.getDataString());
                    }
                } catch (NullPointerException e) {
                    // TODO: handle exception
                    selectedImage = null;
                }

                if (selectedImage == null) {
                } else {

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    if (cursor == null) {

                        filePath = dataIntent.getDataString();
                        filePath = filePath.replace("file:///", "");
                    } else {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                        cursor.close();

                    }
                    return filePath;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return filePath;
    }


    public void dateOfBirth() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        final Date date1 = new Date();
        SimpleDateFormat dFormat = new SimpleDateFormat(Constant.DATE_FORMAT);
        final String dateFormat1 = dFormat.format(date1);
        DatePickerDialog dpd = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
                SimpleDateFormat dFormat1 = new SimpleDateFormat(Constant.DATE_FORMAT);
                String dateFormat = dFormat1.format(date);
                int compare = date.compareTo(date1);
                if (compare != 1) {
                    profileDataOfBirth.setText(dateFormat);
                } else {
                    profileDataOfBirth.setText("");
                    Common.displayToast(Constant.TOAST_DATE_OF_BIRTH);
                }
            }
        }, mYear, mMonth, mDay);
        dpd.show();
    }

    public void ValidateServer() {
        try {

            mName = profileName1.getText().toString().trim();
            mEmail = profileEmail.getText().toString().trim();
            mMobile = ProfileMobile.getText().toString().trim();
            mDataOfBirth = profileDataOfBirth.getText().toString().trim();
            mCity = profileCity.getText().toString().trim();
            mCountry = profileCountry.getText().toString().trim();
            mZipcode = profileZipCode.getText().toString().trim();
            int id = genderRadio.getCheckedRadioButtonId();
            View radioButton = genderRadio.findViewById(id);
            int radioId = genderRadio.indexOfChild(radioButton);
            RadioButton btn = (RadioButton) genderRadio.getChildAt(radioId);
            mGender = (String) btn.getText();
            appProfilePresenter.validateProfile(Constant.TAG_PROFILE_UPDATE, mName, mMobile, mEmail, mDataOfBirth, mGender,
                    mCity, mCountry, mZipcode);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void profileDataUpload() {

        try {
            profileName1.setText(userModel.getName());
            profileEmail.setText(userModel.getEmail());
            ProfileMobile.setText(userModel.getMobileNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setProfileValues() {
        try {
            String userId = AppPreference.getUserId();
            String mobileNumber = AppPreference.getMobileNumber();
            getUserModel = Konnnek2.usersTableDAO.getUserDetails(mobileNumber, userId);

            if (getUserModel != null) {

                if (getUserModel.getName() != null) {
                    profileName1.setText(getUserModel.getName());
                } else {
                    profileName1.setText("");
                }

                if (getUserModel.getMobileNumber() != null) {
                    ProfileMobile.setText(getUserModel.getMobileNumber());
                    ProfileMobile.setEnabled(false);
                } else {
                    if (AppPreference.getMobileNumber() != null) {
                        ProfileMobile.setText(AppPreference.getMobileNumber());
                        ProfileMobile.setEnabled(false);
                    }
                    ProfileMobile.setText("");
                    ProfileMobile.setEnabled(false);
                }
                if (getUserModel.getEmail() != null) {
                    profileEmail.setText(getUserModel.getEmail());
                } else {
                    profileEmail.setText("");
                }
                if (getUserModel.getDateOfBirth() != null) {
                    profileDataOfBirth.setText(getUserModel.getDateOfBirth());
                } else {
                    profileDataOfBirth.setText("");
                }

                if (getUserModel.getZipCode() != null) {
                    profileZipCode.setText(getUserModel.getZipCode());
                } else {
                    profileZipCode.setText("");
                }
            } else {
                Toaster.shortToast(Constant.TOAST_PROFILE_IMAGE_FAILURER);
            }

        } catch (Exception e) {
            e.getMessage();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.profile_edit:
                try {
                    ValidateServer();
                } catch (Exception ex) {

                }
                return true;
            case R.id.profile_skip:
                Intent goToCallingTab = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(goToCallingTab);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean IsValidate() {
        boolean valid = true;
        try {

            if (profileName1.getText().toString().trim().isEmpty() ||
                    profileName1.getText().toString().length() <= 0) {
                profileName1.setError("Enter the FirstName");
                valid = false;
            } else {
                profileName1.setError(null);
            }

            if (profileEmail.getText().toString() == null ||
                    profileEmail.getText().toString().length() <= 0) {
                profileEmail.setError("Enter the Email ID");
                valid = false;
            } else {
                profileEmail.setError(null);
            }
            if (ProfileMobile.getText().toString().length() < 10) {
                ProfileMobile.setError("Enter 10 Digit Valid Mobile Number");
                valid = false;
            } else {
                ProfileMobile.setError(null);
            }

            if (profileDataOfBirth.getText().toString().length() <= 0 || profileDataOfBirth.getText().toString().isEmpty()) {
                profileDataOfBirth.setError("Enter the Date of Birth");
                valid = false;
            } else {
                profileDataOfBirth.setError(null);
            }
            if (genderRadio.getCheckedRadioButtonId() == -1) {
                Common.displayToast("Select the Gender ");
                return false;
            }

            if (profileZipCode.getText().toString().trim().isEmpty() ||
                    profileZipCode.getText().toString().length() <= 0) {
                profileZipCode.setError("Enter the Zipcode");
                valid = false;
            } else {
                profileZipCode.setError(null);
            }
            if (profileCountry.getText().toString().length() <= 0 || profileCountry.getText().toString().isEmpty()) {
                profileZipCode.setError("Enter the Country ");
                valid = false;
            } else {
                profileZipCode.setError(null);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return valid;
    }

    @Override
    public void setNameError() {

    }

    @Override
    public void setEmailError() {

    }

    @Override
    public void setMobileNumberError() {

    }

    @Override
    public void setDataOfBirthError() {

    }

    @Override
    public void setCityError() {

    }

    @Override
    public void setCountryError() {

    }

    @Override
    public void setZipCodeError() {

    }


    @Override
    public void setQBUserError() {

    }

    @Override
    public void setImageIdError() {

    }

}