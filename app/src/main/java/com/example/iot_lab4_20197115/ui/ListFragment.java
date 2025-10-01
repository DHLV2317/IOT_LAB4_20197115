package com.example.iot_lab4_20197115.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iot_lab4_20197115.R;
import com.example.iot_lab4_20197115.data.Post;
import com.example.iot_lab4_20197115.databinding.FragmentListBinding;
import com.example.iot_lab4_20197115.ui.adapter.PostAdapter;
import com.example.iot_lab4_20197115.vm.PostViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ListFragment extends Fragment {

    private FragmentListBinding b;
    private PostViewModel vm;
    private PostAdapter ad;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentListBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        vm = new ViewModelProvider(this).get(PostViewModel.class);

        ad = new PostAdapter(this::goDetail);
        b.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        b.recycler.setAdapter(ad);

        vm.posts().observe(getViewLifecycleOwner(), ad::submit);

        b.btnExport.setOnClickListener(v1 -> exportToPdf());
    }

    private void goDetail(Post p){
        // Navegación si la necesitas
        Snackbar.make(b.getRoot(), "Post #" + p.id, Snackbar.LENGTH_SHORT).show();
    }

    private void exportToPdf() {
        List<Post> items = ad.getCurrentItems();
        if (items == null || items.isEmpty()) {
            Snackbar.make(b.getRoot(), getString(R.string.no_data), Snackbar.LENGTH_SHORT).show();
            return;
        }

        final int pageWidth = 595;   // ~A4 en 72dpi
        final int pageHeight = 842;
        final int margin = 36;
        final int lineSpace = 16;

        PdfDocument pdf = new PdfDocument();
        Paint titlePaint = new Paint();
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        titlePaint.setTextSize(14);

        Paint bodyPaint = new Paint();
        bodyPaint.setTextSize(12);

        int pageNum = 1;
        int y = margin;

        PdfDocument.Page page = pdf.startPage(
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create());
        Canvas canvas = page.getCanvas();

        y = drawHeader(canvas, pageWidth, margin, y, "Reporte de Posts");

        for (Post p : items) {
            int need = lineSpace * 4;
            if (y + need > pageHeight - margin) {
                pdf.finishPage(page);
                page = pdf.startPage(new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, ++pageNum).create());
                canvas = page.getCanvas();
                y = drawHeader(canvas, pageWidth, margin, margin, "Reporte de Posts (cont.)");
            }

            y += lineSpace;
            canvas.drawText("• #" + p.id + "  " + (p.title == null ? "" : p.title),
                    margin, y, titlePaint);

            y += lineSpace;
            y = drawMultiline(canvas, p.body == null ? "" : p.body,
                    margin, y, bodyPaint, pageWidth - margin * 2, lineSpace);

            y += lineSpace / 2;
            canvas.drawLine(margin, y, pageWidth - margin, y, bodyPaint);
            y += lineSpace / 2;
        }

        pdf.finishPage(page);

        File dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (dir != null && !dir.exists()) dir.mkdirs();
        File out = new File(dir, "reporte_posts.pdf");

        try (FileOutputStream os = new FileOutputStream(out)) {
            pdf.writeTo(os);
        } catch (Exception e) {
            Snackbar.make(b.getRoot(), "Error al generar PDF", Snackbar.LENGTH_LONG).show();
            pdf.close();
            return;
        } finally { pdf.close(); }

        Uri uri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider", out);
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setDataAndType(uri, "application/pdf");
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Snackbar.make(b.getRoot(), "PDF creado", Snackbar.LENGTH_SHORT).show();
        startActivity(Intent.createChooser(it, "Abrir con"));
    }

    private int drawHeader(Canvas c, int pageWidth, int margin, int y, String title) {
        Paint h = new Paint();
        h.setColor(0xFF26C6DA); // celeste
        c.drawRect(0, 0, pageWidth, 28, h);

        Paint t = new Paint();
        t.setColor(0xFFFFFFFF);
        t.setTextSize(14);
        t.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        c.drawText(title, margin, 20, t);

        return margin + 8;
    }

    private int drawMultiline(Canvas canvas, String text, int x, int y, Paint paint, int maxWidth, int lineHeight) {
        if (text == null) return y;
        android.text.TextPaint tp = new android.text.TextPaint(paint);
        android.text.StaticLayout sl = android.text.StaticLayout.Builder
                .obtain(text, 0, text.length(), tp, maxWidth)
                .setLineSpacing(0, 1f)
                .build();
        canvas.save();
        canvas.translate(x, y);
        sl.draw(canvas);
        canvas.restore();
        return y + sl.getHeight();
    }
}