package com.example.avaliaon1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class CoordinateTypeDialog {

    private final AlertDialog dialog;
    private final String[] coordinateTypes = {
            "Graus [+/-DDD.DDDDD]",
            "Graus-Minutos [+/-DDD:MM.MMMMM]",
            "Graus-Minutos-Segundos [+/-DDD:MM:SS.SSSSS]"
    };

    public CoordinateTypeDialog(@NonNull Context context, SharedPreferences sharedPrefs, String currentType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] selectedType = {sharedPrefs.getString("coordenadas", currentType)};
        int currentSelection = getIndexOfType(selectedType[0]);

        builder.setTitle("Trocar tipo de coordenada:")
                .setSingleChoiceItems(coordinateTypes, currentSelection, (dialog, which) -> selectedType[0] = coordinateTypes[which])
                .setPositiveButton("Salvar", (dialogInterface, which) -> {
                    // Save the selected type in SharedPreferences
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("coordenadas", selectedType[0]);
                    editor.apply();
                })
                .setNegativeButton("Cancelar", (dialogInterface, which) -> {
                    Toast.makeText(context, "Cancelado!", Toast.LENGTH_SHORT).show();
                });

        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

    private int getIndexOfType(String type) {
        for (int i = 0; i < coordinateTypes.length; i++) {
            if (coordinateTypes[i].equals(type)) {
                return i;
            }
        }
        return 0;
    }

    public void setOnTypeSelectedListener(OnTypeSelectedListener listener) {
        // Set the listener for when a type is selected
    }

    public interface OnTypeSelectedListener {
        void onTypeSelected(String selectedType);
    }
}
