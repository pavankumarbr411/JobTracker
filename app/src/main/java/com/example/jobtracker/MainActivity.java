
package com.example.jobtracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.text.Editable;
import android.text.TextWatcher;

public class MainActivity extends AppCompatActivity {

    private AppDatabase database;

    private TextView totalApplications;
    private TextView interviews;
    private TextView emptyState;
    private LinearLayout jobsContainer;

    private EditText editSearchJobs;
    private Spinner spinnerFilterStatus;

    private List<JobApplication> allApplications = new ArrayList<>();

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getDatabase(getApplicationContext());

        totalApplications = findViewById(R.id.txtTotalApplications);
        interviews = findViewById(R.id.txtInterviews);
        emptyState = findViewById(R.id.txtEmptyState);
        jobsContainer = findViewById(R.id.jobsContainer);

        editSearchJobs = findViewById(R.id.editSearchJobs);
        spinnerFilterStatus = findViewById(R.id.spinnerFilterStatus);

        Button addJobButton = findViewById(R.id.btnAddJob);

        addJobButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddJobActivity.class
            );
            startActivity(intent);
        });

        // Search by company name or job title
        editSearchJobs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                displayFilteredApplications();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Filter by application status
        spinnerFilterStatus.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        displayFilteredApplications();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
    }

    private void loadApplications() {
        executor.execute(() -> {
            JobApplicationDao dao = database.jobApplicationDao();

            int total = dao.getApplicationCount();
            int interviewCount = dao.getInterviewCount();

            List<JobApplication> applications =
                    dao.getAllApplications();

            runOnUiThread(() -> {
                totalApplications.setText(String.valueOf(total));
                interviews.setText(String.valueOf(interviewCount));

                allApplications = new ArrayList<>(applications);
                displayFilteredApplications();
            });
        });
    }

    private void displayFilteredApplications() {
        if (jobsContainer == null || editSearchJobs == null
                || spinnerFilterStatus == null) {
            return;
        }

        String query = editSearchJobs.getText()
                .toString().trim().toLowerCase(Locale.ROOT);

        Object selectedItem = spinnerFilterStatus.getSelectedItem();

        String selectedStatus = selectedItem == null
                ? "All Statuses"
                : selectedItem.toString();

        List<JobApplication> filteredApplications = new ArrayList<>();

        for (JobApplication job : allApplications) {

            String company = job.company == null
                    ? "" : job.company.toLowerCase(Locale.ROOT);

            String jobTitle = job.jobTitle == null
                    ? "" : job.jobTitle.toLowerCase(Locale.ROOT);

            boolean matchesSearch =
                    company.contains(query) || jobTitle.contains(query);

            boolean matchesStatus =
                    selectedStatus.equals("All Statuses")
                            || selectedStatus.equals(job.status);

            if (matchesSearch && matchesStatus) {
                filteredApplications.add(job);
            }
        }

        jobsContainer.removeAllViews();

        if (filteredApplications.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);

            if (allApplications.isEmpty()) {
                emptyState.setText(
                        "No applications yet.\nTap + Add Job to get started."
                );
            } else {
                emptyState.setText(
                        "No matching applications found.\nTry another search or status."
                );
            }
        } else {
            emptyState.setVisibility(View.GONE);

            for (JobApplication job : filteredApplications) {
                createJobCard(job);
            }
        }
    }

    private void createJobCard(JobApplication job) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 18, 20, 18);
        card.setBackgroundResource(R.drawable.job_card_background);
        card.setElevation(3);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 16);
        card.setLayoutParams(cardParams);

        TextView companyText = new TextView(this);
        companyText.setText(job.company);
        companyText.setTextColor(Color.rgb(45, 35, 65));
        companyText.setTextSize(18);
        companyText.setTypeface(null, Typeface.BOLD);

        TextView titleText = new TextView(this);
        titleText.setText(job.jobTitle);
        titleText.setTextColor(Color.rgb(95, 88, 110));
        titleText.setTextSize(15);
        titleText.setPadding(0, 4, 0, 10);

        TextView statusText = new TextView(this);
        statusText.setText("  " + job.status + "  ");
        statusText.setTextSize(12);
        statusText.setTypeface(null, Typeface.BOLD);
        statusText.setPadding(0, 6, 0, 6);

        int statusColor;

        switch (job.status) {
            case "Interview":
                statusColor = Color.rgb(30, 110, 75);
                break;
            case "Offer":
                statusColor = Color.rgb(40, 90, 180);
                break;
            case "Rejected":
                statusColor = Color.rgb(180, 55, 55);
                break;
            case "Assessment":
                statusColor = Color.rgb(150, 95, 20);
                break;
            default:
                statusColor = Color.rgb(105, 75, 180);
                break;
        }

        statusText.setTextColor(statusColor);
        statusText.setBackgroundColor(Color.rgb(245, 241, 252));

        TextView locationText = new TextView(this);
        locationText.setText(
                job.location == null || job.location.trim().isEmpty()
                        ? "Location not specified"
                        : job.location
        );
        locationText.setTextColor(Color.rgb(110, 105, 120));
        locationText.setTextSize(13);
        locationText.setPadding(0, 10, 0, 0);

        TextView actionHint = new TextView(this);
        actionHint.setText("Tap to edit  •  Long-press to delete");
        actionHint.setTextColor(Color.rgb(125, 112, 155));
        actionHint.setTextSize(12);
        actionHint.setGravity(Gravity.END);
        actionHint.setPadding(0, 12, 0, 0);

        card.addView(companyText);
        card.addView(titleText);
        card.addView(statusText);
        card.addView(locationText);
        card.addView(actionHint);

        // Tap to edit
        card.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddJobActivity.class
            );
            intent.putExtra("job_id", job.id);
            startActivity(intent);
        });

        // Long-press to delete
        card.setOnLongClickListener(v -> {
            showDeleteConfirmation(job);
            return true;
        });

        jobsContainer.addView(card);
    }

    private void showDeleteConfirmation(JobApplication job) {

        new AlertDialog.Builder(this)
                .setTitle("Delete application?")
                .setMessage(
                        "Are you sure you want to delete the "
                                + job.company + " application?"
                )
                .setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    executor.execute(() -> {
                        database.jobApplicationDao().delete(job);

                        runOnUiThread(() -> {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Application deleted",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadApplications();
                        });
                    });
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
