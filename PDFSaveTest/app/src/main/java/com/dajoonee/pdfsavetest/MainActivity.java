package com.dajoonee.pdfsavetest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText nemo_width, nemo_height, fname;
    Button save_btn, nemo_btn;
    LinearLayout sketchbook;
    ArrayList<PrintedPdfDocument> document = new ArrayList<PrintedPdfDocument>();
    boolean created = false;
    boolean btnOn = false;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nemo_width = findViewById(R.id.nemo_width);
        nemo_height = findViewById(R.id.nemo_height);
        fname = findViewById(R.id.fname);

        save_btn = findViewById(R.id.save_btn);
        nemo_btn = findViewById(R.id.nemo_btn);
        sketchbook = findViewById(R.id.sketchbook);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE);

//        if(nemo_width.isFocused() == true ){
//            if(nemo_height.isFocused() == true){
//                btnOn = true;
//            }
//        }
        System.out.println("created : "+ created);
        Button.OnClickListener onclick = new Button.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.nemo_btn :
                        System.out.println("가로 입력 사이즈"+nemo_width.getText().length());
                        if(nemo_width.getText().length() > 0 && nemo_height.getText().length() > 0  ) {
                            sketchbook.addView(new CreateNemo(MainActivity.this));
                            created = true;
                        }else{
                            Toast.makeText(getApplicationContext(),"가로입력하라규!",Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.save_btn :
                        if(created){
                            File filepath = new File("/sdcard/"+fname.getText().toString()+".pdf");
                           try{
                               document.get(index).writeTo(new FileOutputStream(filepath));

                           }catch (Exception e){
                               e.printStackTrace();
                               Toast.makeText(getApplicationContext(),"PDF 저장 오류!",Toast.LENGTH_SHORT).show();
                           }
                            document.get(index).close();
                               index++;

                        }
                        break;

                }
            }
        };

        nemo_btn.setOnClickListener(onclick);
        save_btn.setOnClickListener(onclick);

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    class CreateNemo extends View {
        int widthpx, heightpx;

        PdfDocument.Page page;

        CreateNemo(Context con){
            super(con);
            this.widthpx = Integer.parseInt(nemo_width.getText().toString());
            this.heightpx = Integer.parseInt(nemo_height.getText().toString());
            System.out.println("CreateNemo 들어옴");

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);

            Rect nemo = new Rect(30,30,30+(widthpx*72),30+(heightpx*72));

            canvas.drawRect(nemo,paint);
            makePdf(nemo,paint);

//            canvas.drawRect(nemo,paint);


        }

        private Canvas makePdf (Rect rect, Paint paint) {
            System.out.println("makePdf 들어옴");
            PrintAttributes setPaper = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setMinMargins(new PrintAttributes.Margins(20,20,20,20))
                    .build();

            document.add(new PrintedPdfDocument(MainActivity.this, new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setMinMargins(new PrintAttributes.Margins(20,20,20,20))
            .build()));

            page = document.get(index).startPage(0);
            Canvas canvas = page.getCanvas();
            canvas.drawRect(rect,paint);
            document.get(index).finishPage(page);


            return canvas;

        }
    }

//    private void createNemo ()

}
