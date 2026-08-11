package com.example.pdfsignatureverifier;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_PDF_FILE = 101;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PDFBoxResourceLoader.init(getApplicationContext());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        Button selectButton = new Button(this);
        selectButton.setText("PDF ফাইল সিলেক্ট করুন");

        resultText = new TextView(this);
        resultText.setText("\nকোনো ফাইল সিলেক্ট করা হয়নি");
        resultText.setTextSize(18f);

        layout.addView(selectButton);
        layout.addView(resultText);

        setContentView(layout);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                verifyPdf(uri);
            }
        }
    }

    private void verifyPdf(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            PDDocument document = PDDocument.load(inputStream);

            boolean hasSignature = !document.getSignatureDictionaries().isEmpty();
            document.close();

            if (hasSignature) {
                resultText.setText("ফলাফল: ✔ সিগনেচার পাওয়া গেছে (Verified Signature)");
            } else {
                resultText.setText("ফলাফল: ✘ কোনো সিগনেচার পাওয়া যায়নি");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF পড়তে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
        }
    }
          }
