package com.my.kasirtemeji.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.my.kasirtemeji.R;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CashActivity extends AppCompatActivity {

    private ExecutorService executorService;
    private static final int PERMISSION_BLUETOOTH_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_struk);

        executorService = Executors.newSingleThreadExecutor();

        // TIDAK ADA 'order' di layout, jadi HAPUS baris ini:
        // TextView orderTextView = findViewById(R.id.order); // <-- HAPUS/COMMENT

        // Cari Button dengan ID yang benar (sesuai layout Anda)
        Button btnCetak = findViewById(R.id.btn_cetak_struk); // Pastikan ID ini ada di XML

        // Jika btn_cetak_struk tidak ada, tambahkan di XML atau ganti dengan Button lain
        if (btnCetak == null) {
            // Alternatif: cari button lain atau tambahkan programmatically
            btnCetak = new Button(this);
            btnCetak.setText("CETAK STRUK");
            // Atau gunakan cara lain untuk trigger print
        }

        btnCetak.setOnClickListener(v -> {
            if (cekIzinBluetooth()) {
                cetakStruk();
            } else {
                mintaIzinBluetooth();
            }
        });
    }

    private void cetakStruk() {
        executorService.execute(() -> {
            try {
                // 1. Cari printer yang sudah dipair
                BluetoothPrintersConnections[] printers = BluetoothPrintersConnections.getList();

                if (printers == null || printers.length == 0) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Tidak ada printer terhubung!", Toast.LENGTH_LONG).show()
                    );
                    return;
                }

                // 2. Ambil printer pertama
                BluetoothPrintersConnections printerConnection = printers[0];

                // 3. Setup Printer (PASTIKAN CONSTRUCTOR INI)
                // Parameters: (connection, dpi, paperWidthMM, maxCharPerLine)
                EscPosPrinter printer = new EscPosPrinter(
                        printerConnection, // BluetoothPrintersConnections
                        203,              // DPI: 203 dots per inch
                        48.0f,            // Lebar kertas: 48mm (untuk kertas 58mm)
                        32                // Max karakter per baris
                );

                // 4. Buat bitmap dari layout
                runOnUiThread(() -> {
                    try {
                        Bitmap bitmapStruk = convertXmlToBitmap();

                        // 5. Cetak di thread terpisah
                        executorService.execute(() -> {
                            try {
                                // Konversi bitmap ke format printer
                                String hexImage = PrinterTextParserImg.bitmapToHexadecimalString(
                                        printer, bitmapStruk);

                                // Perintah cetak
                                printer.printFormattedText(
                                        "[C]<img>" + hexImage + "</img>\n" +
                                                "[L]\n" +  // Feed 1 baris
                                                "[L]\n" +  // Feed lagi
                                                "[L]\n"    // Feed lagi
                                );

                                runOnUiThread(() ->
                                        Toast.makeText(this, "✅ Berhasil mencetak!", Toast.LENGTH_SHORT).show()
                                );

                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(() ->
                                        Toast.makeText(this, "❌ Gagal mencetak: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "❌ Gagal membuat gambar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "❌ Error koneksi printer: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    // Fungsi: Konversi XML ke Bitmap (SUDAH SESUAI dengan layout Anda)
    private Bitmap convertXmlToBitmap() {
        // 1. Inflate layout
        View rootView = LayoutInflater.from(this).inflate(R.layout.layout_struk, null);

        // 2. Ambil LinearLayout dengan ID 'struk_content' (sesuai XML Anda)
        LinearLayout strukContent = rootView.findViewById(R.id.struk_content);

        // 3. Gunakan strukContent (sudah ada di XML Anda)
        View viewToPrint = strukContent;

        // 4. Ukur layout (384px untuk kertas 58mm)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(384, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        viewToPrint.measure(widthSpec, heightSpec);

        // 5. Tentukan tinggi
        int measuredHeight = viewToPrint.getMeasuredHeight();
        if (measuredHeight < 100) {
            measuredHeight = 600; // Tinggi minimum
        }

        // 6. Layout view
        viewToPrint.layout(0, 0, 384, measuredHeight);

        // 7. Buat bitmap
        Bitmap bitmap = Bitmap.createBitmap(384, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE); // Background putih

        // 8. Gambar view ke canvas
        viewToPrint.draw(canvas);

        return bitmap;
    }

    // --- BAGIAN PERIZINAN BLUETOOTH ---
    private boolean cekIzinBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void mintaIzinBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN
                    }, PERMISSION_BLUETOOTH_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, PERMISSION_BLUETOOTH_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_BLUETOOTH_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, mulai cetak
                cetakStruk();
            } else {
                Toast.makeText(this, "Izin Bluetooth diperlukan untuk mencetak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}