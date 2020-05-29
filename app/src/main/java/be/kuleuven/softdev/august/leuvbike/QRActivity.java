package be.kuleuven.softdev.august.leuvbike;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class QRActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        Snackbar.make(this.findViewById(android.R.id.content)
                , "Please Scan QR CODE of bike", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onScanned(Barcode barcode) {
        // single barcode scanned
        Log.d("TAG", barcode.displayValue);
        Intent i = new Intent(QRActivity.this, RentalActivity.class);
        i.putExtra("URL", barcode.displayValue);
        startActivity(i);
        finish();
    }

    @Override
    public void onScannedMultiple(List<Barcode> list) {
        // multiple barcodes scanned
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String s) {
        // now scan error
    }

    @Override
    public void onCameraPermissionDenied() {
        // camera permission actually denied
    }
}
