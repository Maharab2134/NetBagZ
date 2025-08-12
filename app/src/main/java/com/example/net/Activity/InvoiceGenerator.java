package com.example.net.Activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InvoiceGenerator {

    public static void generateInvoice(Context context, String invoiceId, String userName, String paymentAmount, String shopName, int productQuantity, String paymentMethod, String productTitle, String productSize, String address) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());

        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        paint.setTextSize(22);
        float companyNameWidth = paint.measureText("NetBags Pvt. Ltd.");
        canvas.drawText("NetBags Pvt. Ltd.", (595 - companyNameWidth) / 2, 100, paint);
        paint.setTextSize(14);
        canvas.drawText("Date: " + currentDate, 450, 100, paint);

        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(18);
        canvas.drawText("INVOICE", 250, 130, paint);

        paint.setStrokeWidth(2);
        canvas.drawLine(20, 150, 575, 150, paint);

        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(14);
        paint.setColor(Color.DKGRAY);
        canvas.drawText("Invoice ID: " + invoiceId, 30, 170, paint);
        canvas.drawText("Customer Name: " + userName, 30, 190, paint);
        canvas.drawText("Shop Name: " + shopName, 30, 210, paint);
        canvas.drawText("Address: " + address, 30, 230, paint);
        canvas.drawText("Total Quantity: " + productQuantity, 30, 250, paint);

        int gap = 30;

        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        paint.setTextSize(14);
        canvas.drawText("Product Name", 30, 250 + gap, paint);
        canvas.drawText("Size", 220, 250 + gap, paint);
        canvas.drawText("Quantity", 300, 250 + gap, paint);
        canvas.drawText("Price", 400, 250 + gap, paint);
        canvas.drawText("Status", 500, 250 + gap, paint);

        canvas.drawLine(20, 260 + gap, 575, 260 + gap, paint);

        gap += 10;

        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(14);
        canvas.drawText(productTitle, 30, 280 + gap, paint);
        canvas.drawText(productSize, 225, 280 + gap, paint);
        canvas.drawText(String.valueOf(productQuantity), 320, 280 + gap, paint);
        canvas.drawText(paymentAmount + "/-", 400, 280 + gap, paint);
        canvas.drawText("Success", 500, 280 + gap, paint);

        canvas.drawLine(20, 300 + gap, 575, 300 + gap, paint);

        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        paint.setTextSize(16);
        canvas.drawText("Payment Method:", 30, 340 + gap, paint);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(14);
        canvas.drawText(paymentMethod, 30, 360 + gap, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
        canvas.drawText("Approve by NetBags", 420, 720, paint);

        paint.setColor(Color.DKGRAY);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(12);
        canvas.drawText("Thank you for your purchase!", 200, 750, paint);

        document.finishPage(page);

        saveInvoiceToDownloads(context, document, invoiceId);
        document.close();
    }

    private static void saveInvoiceToDownloads(Context context, PdfDocument document, String invoiceId) {
        String fileName = "Invoice_" + invoiceId + ".pdf";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            ContentResolver contentResolver = context.getContentResolver();
            Uri fileUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

            try {
                if (fileUri != null) {
                    OutputStream outputStream = contentResolver.openOutputStream(fileUri);
                    document.writeTo(outputStream);
                    outputStream.close();
                    Toast.makeText(context, "Invoice saved in Downloads", Toast.LENGTH_LONG).show();
                    Log.d("Invoice", "Invoice saved at: " + fileUri.toString());
                }
            } catch (IOException e) {
                Log.e("Invoice", "Error saving invoice", e);
            }
        } else {
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File invoiceFile = new File(downloadsFolder, fileName);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(invoiceFile);
                document.writeTo(fileOutputStream);
                fileOutputStream.close();
                Toast.makeText(context, "Invoice saved in Downloads", Toast.LENGTH_LONG).show();
                Log.d("Invoice", "Invoice saved at: " + invoiceFile.getAbsolutePath());
            } catch (IOException e) {
                Log.e("Invoice", "Error saving invoice", e);
            }
        }
    }
}