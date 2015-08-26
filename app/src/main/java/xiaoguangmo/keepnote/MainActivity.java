package xiaoguangmo.keepnote;

/**
 * Created by Xiaoguang Mo on 3/01/15.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MainActivity extends Activity implements OnClickListener {

    private final String tag = "MainActivity";

    private ImageView eraser;
    private ImageButton btnClear, btnSave, btnShare, btnCamera, btnSize, btnColor;

    private DrawingView drawingView;

    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 1888;
    private int whichColor = 0, whichSize = 0;

    /**
     * To initialize variable and buttons on the screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            setContentView(R.layout.activity_main);

            drawingView = (DrawingView) findViewById(R.id.drawing);

            btnSize = (ImageButton) findViewById(R.id.btnSizePicker);
            btnSize.setOnClickListener(this);

            btnColor = (ImageButton) findViewById(R.id.btnColorPicker);
            btnColor.setOnClickListener(this);

            btnClear = (ImageButton) findViewById(R.id.btnClear);
            btnClear.setOnClickListener(this);

            btnSave = (ImageButton) findViewById(R.id.btnSave);
            btnSave.setOnClickListener(this);

            btnShare = (ImageButton) findViewById(R.id.btnShare);
            btnShare.setOnClickListener(this);

            btnCamera = (ImageButton) findViewById(R.id.btnCamera);
            btnCamera.setOnClickListener(this);

            eraser = (ImageView) findViewById(R.id.eraser);
            eraser.setOnClickListener(this);
        }
    }

    /**
     * To create events for every button clicked on the tool bar
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == eraser) {
            drawingView.onClickUndo();
        } else if (v == btnClear) {
            drawingView.reset();
        } else if (v == btnSave) {
            saveImage();
            drawingView.reset();
        } else if (v == btnCamera) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);

        } else if (v == btnShare) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/png");

            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(saveImage().getAbsolutePath()));
            startActivity(Intent.createChooser(share, "Share Image"));

        } else if (v == btnColor) {
            Dialog mDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("ColorPicker")
                    .setSingleChoiceItems(new String[]{"Black","Red","Yellow","Blue"}, whichColor, new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            switch(which)
                            {
                                case 0:
                                {
                                    drawingView.setPaintColor(0);
                                    whichColor = 0;
                                    break;
                                }
                                case 1:
                                {
                                    drawingView.setPaintColor(2);
                                    whichColor = 1;
                                    break;
                                }
                                case 2:
                                {
                                    drawingView.setPaintColor(3);
                                    whichColor = 2;
                                    break;
                                }
                                case 3:
                                {
                                    drawingView.setPaintColor(4);
                                    whichColor = 3;
                                    break;
                                }
                            }
                        }
                    })
                    .setPositiveButton("Save", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    })
                    .create();
            mDialog.show();
        } else if (v == btnSize) {
            Dialog mDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("SizePicker")
                    .setSingleChoiceItems(new String[]{"1","2","3","4"}, whichSize, new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            switch(which)
                            {
                                case 0:
                                {
                                    drawingView.setPaintSize(5);
                                    whichSize = 0;
                                    break;
                                }
                                case 1:
                                {
                                    drawingView.setPaintSize(10);
                                    whichSize = 1;
                                    break;
                                }
                                case 2:
                                {
                                    drawingView.setPaintSize(15);
                                    whichSize = 2;
                                    break;
                                }
                                case 3:
                                {
                                    drawingView.setPaintSize(20);
                                    whichSize = 3;
                                    break;
                                }
                            }
                        }
                    })
                    .setPositiveButton("Save", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    })
                    .create();
            mDialog.show();
        }
    }

    /**
     * This function is used to save image to sd card, with system time as file name
     * @return saved file
     */
    public File saveImage() {
        drawingView.setDrawingCacheEnabled(true);
        Bitmap bm = drawingView.getDrawingCache();

        File fPath = Environment.getExternalStorageDirectory();

        File f = null;

        f = new File(fPath, Calendar.getInstance().getTime().toString() + ".png");

        try {
            FileOutputStream strm = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 80, strm);
            strm.close();

            Toast.makeText(getApplicationContext(), "Image is saved successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    /**
     * To use the system method to call camera and set background canvas as photo taken
     * @param requestCode define which call is used
     * @param resultCode to show result is success or fail
     * @param imageReturnedIntent parse the image taken
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {

//            case SELECT_PHOTO:
//                if (resultCode == RESULT_OK) {
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    InputStream imageStream = null;
//                    try {
//                        imageStream = getContentResolver().openInputStream(selectedImage);
//                        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
//
//                        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
//
//                        if(Build.VERSION.SDK_INT >= 16)
//                        {
//                            drawingView.setBackground(ob);
//                        }else {
//                            drawingView.setBackgroundDrawable(ob);
//                        }
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {

                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");

                    BitmapDrawable ob = new BitmapDrawable(getResources(), photo);

                    if(Build.VERSION.SDK_INT >= 16)
                    {
                        drawingView.setBackground(ob);
                    }else {
                        drawingView.setBackgroundDrawable(ob);
                    }
                }
        }
    }
}
