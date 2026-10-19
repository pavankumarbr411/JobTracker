
package com.example.jobtracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddJobActivity extends AppCompatActivity {

    private AppDatabase database;
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private EditText editCompany;
    private EditText editJobTitle;
    private EditText editLocation;
    private EditText editJobUrl;
    private Spinner spinnerStatus;
    private Button btnSaveJob;

    private int jobId = -1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        database = AppDatabase.getDatabase(getApplicationContext());

        editCompany = findViewById(R.id.editCompany);
        editJobTitle = findViewById(R.id.editJobTitle);
        editLocation = findViewById(R.id.editLocation);
        editJobUrl = findViewById(R.id.editJobUrl);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSaveJob = findViewById(R.id.btnSaveJob);

        Button btnCancelJob = findViewById(R.id.btnCancelJob);

        jobId = getIntent().getIntExtra("job_id", -1);

        if (jobId != -1) {
            isLoading = true;
            btnSaveJob.setEnabled(false);


            loadJobForEditing();
        }

        btnSaveJob.setOnClickListener(v -> saveJob());

        btnCancelJob.setOnClickListener(v -> finish());
    }

    private void loadJobForEditing() {
        executor.execute(() -> {
            JobApplication job =
                    database.jobApplicationDao()
                            .getApplicationById(jobId);

            runOnUiThread(() -> {
                if (job == null) {
                    Toast.makeText(this,
                            "Job application not found",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                editCompany.setText(job.company);
                editJobTitle.setText(job.jobTitle);
                editLocation.setText(job.location);
                editJobUrl.setText(job.jobUrl);

                for (int i = 0; i < spinnerStatus.getCount(); i++) {
                    if (spinnerStatus.getItemAtPosition(i)
                            .toString().equals(job.status)) {
                        spinnerStatus.setSelection(i);
                        break;
                    }
                }

                isLoading = false;
                btnSaveJob.setEnabled(true);
                btnSaveJob.setText("Update Application");
            });
        });
    }

    private void saveJob() {
        if (isLoading) {
            return;
        }

        String company = editCompany.getText().toString().trim();
        String jobTitle = editJobTitle.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String jobUrl = editJobUrl.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        if (company.isEmpty() || jobTitle.isEmpty()) {
            Toast.makeText(this,
                    "Enter company name and job title",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveJob.setEnabled(false);

        executor.execute(() -> {
            try {
                if (jobId == -1) {
                    JobApplication job = new JobApplication(
                            company, jobTitle, location, status, jobUrl
                    );
                    database.jobApplicationDao().insert(job);
                } else {
                    JobApplication job =
                            database.jobApplicationDao()
                                    .getApplicationById(jobId);

                    if (job == null) {
                        runOnUiThread(() -> {
                            btnSaveJob.setEnabled(true);
                            Toast.makeText(this,
                                    "Job application not found",
                                    Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    job.company = company;
                    job.jobTitle = jobTitle;
                    job.location = location;
                    job.status = status;
                    job.jobUrl = jobUrl;

                    database.jobApplicationDao().update(job);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            jobId == -1
                                    ? "Job saved successfully!"
                                    : "Job updated successfully!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnSaveJob.setEnabled(true);
                    Toast.makeText(this,
                            "Could not save changes. Please try again.",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
