package com.example.studentfirebase;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etName, etGroup, etAge;
    private AutoCompleteTextView spinnerDay, spinnerMonth, etYear;
    private MaterialButton btnSave, btnLoad;
    private TableLayout tableStudents;

    private DatabaseReference studentsRef;

    private final String[] monthNames = {
            "Қаңтар", "Ақпан", "Наурыз", "Сәуір", "Мамыр", "Маусым",
            "Шілде", "Тамыз", "Қыркүйек", "Қазан", "Қараша", "Желтоқсан"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etGroup = findViewById(R.id.etGroup);
        etAge = findViewById(R.id.etAge);

        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        etYear = findViewById(R.id.etYear);

        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);
        tableStudents = findViewById(R.id.tableStudents);

        studentsRef = FirebaseDatabase.getInstance().getReference("Students");

        setupDropdowns();

        btnSave.setOnClickListener(v -> saveStudent());
        btnLoad.setOnClickListener(v -> loadStudents());
    }

    private void setupDropdowns() {
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) {
            days[i] = String.format("%02d", i + 1);
        }

        String[] years = new String[50];
        int startYear = 1980;
        for (int i = 0; i < 50; i++) {
            years[i] = String.valueOf(startYear + i);
        }

        ArrayAdapter<String> dayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, days);

        ArrayAdapter<String> monthAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, monthNames);

        ArrayAdapter<String> yearAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);

        spinnerDay.setAdapter(dayAdapter);
        spinnerMonth.setAdapter(monthAdapter);
        etYear.setAdapter(yearAdapter);

        spinnerDay.setOnClickListener(v -> spinnerDay.showDropDown());
        spinnerMonth.setOnClickListener(v -> spinnerMonth.showDropDown());
        etYear.setOnClickListener(v -> etYear.showDropDown());

        spinnerDay.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) spinnerDay.showDropDown();
        });

        spinnerMonth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) spinnerMonth.showDropDown();
        });

        etYear.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) etYear.showDropDown();
        });
    }

    private void saveStudent() {
        String name = getText(etName);
        String group = getText(etGroup);
        String age = getText(etAge);

        String day = spinnerDay.getText().toString().trim();
        String month = spinnerMonth.getText().toString().trim();
        String year = etYear.getText().toString().trim();

        if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(group) ||
                TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(day) ||
                TextUtils.isEmpty(month) ||
                TextUtils.isEmpty(year)) {
            Toast.makeText(this, "Барлық өрістерді толтырыңыз", Toast.LENGTH_SHORT).show();
            return;
        }

        String birthDate = day + " " + month + " " + year;

        String id = studentsRef.push().getKey();
        if (id == null) {
            Toast.makeText(this, "ID жасалмады", Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student(id, name, group, age, birthDate);

        studentsRef.child(id).setValue(student)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Firebase-ке сақталды", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Қате: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void loadStudents() {
        int childCount = tableStudents.getChildCount();
        if (childCount > 1) {
            tableStudents.removeViews(1, childCount - 1);
        }

        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "База бос", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Student student = ds.getValue(Student.class);
                    if (student != null) {
                        addTableRow(student);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Оқу қатесі: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTableRow(Student student) {
        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.WHITE);
        row.setPadding(0, 4, 0, 4);

        row.addView(createCell(student.getName()));
        row.addView(createCell(student.getGroup()));
        row.addView(createCell(student.getAge()));
        row.addView(createCell(student.getBirthDate()));

        tableStudents.addView(row);
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(16, 16, 16, 16);
        tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        tv.setSingleLine(true);
        return tv;
    }

    private void clearFields() {
        etName.setText("");
        etGroup.setText("");
        etAge.setText("");

        spinnerDay.setText("", false);
        spinnerMonth.setText("", false);
        etYear.setText("", false);

        etName.requestFocus();
    }

    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}