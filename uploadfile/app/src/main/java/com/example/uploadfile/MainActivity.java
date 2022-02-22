package com.example.uploadfile;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.widget.Button;
import android.widget.TextView;

import com.example.uploadfile.Model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
//    AssetManager assets = new AssetManager();
    Button uploadButton ;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadButton =  (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        uploadButton.setOnClickListener(v -> openFile());
    }



    private static final int PICK_PDF_FILE = 2;

    public void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

        startActivityForResult(intent,PICK_PDF_FILE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                System.out.println(uri);
                // Perform operations on the document using its URI.
                File auxFile = new File(uri.getPath());
                System.out.println(auxFile);
                try {
                    InputStream iStream =   getContentResolver().openInputStream(uri);
                    float[] floatOut = getFloat(iStream);
                    System.out.println(floatOut.length);
                    predictFromModel("model.tflite", floatOut );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public float[] getFloat(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024*256;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
//        return byteBuffer.toByteArray();

        float floats[] = new float[buffer.length / Float.BYTES];
        ByteBuffer.wrap(buffer).asFloatBuffer().get(floats);
        for(int i=0; i<10;i++) {
            System.out.println(floats[i]);
        }
        return floats;
    }


public void predictFromModel(String mModelPath, float[] inputBuffer) throws IOException {
        Interpreter interpreter = new Interpreter( Model.loadModelFile(getAssets(), mModelPath ) );
        interpreter.resizeInput(0, new int[] {inputBuffer.length});
        interpreter.allocateTensors();
        float[] output = new float[2];
        interpreter.run(inputBuffer,output);
        System.out.println("Not snoring");
        System.out.println( interpreter.getOutputTensor(0).asReadOnlyBuffer().getFloat(0) );
        System.out.println("Snoring");
        System.out.println( interpreter.getOutputTensor(0).asReadOnlyBuffer().getFloat(1) );
        float snoring = interpreter.getOutputTensor(0).asReadOnlyBuffer().getFloat(1);
    if (snoring != 0) {
        textView.setText("Snoring Detected");
    }
    else{
        textView.setText("Snoring Detected");
    }
    }
}
