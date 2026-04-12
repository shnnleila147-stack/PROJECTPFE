package com.example.projectpfe;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.projectpfe.databinding.ActivityRegisterBinding;
import com.example.projectpfe.api.ApiClient;
import com.example.projectpfe.api.ApiService;
import com.example.projectpfe.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupGradeSpinner();
        setupTermsText();
        setupLoginText();
        setupClickListeners();
    }

    private void setupGradeSpinner() {
        String[] grades = {
                "Select your grade",
                "1st Year",
                "2nd Year",
                "3rd Year",
                "4th Year",
                "5th Year"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                grades
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ?
                        ContextCompat.getColor(getContext(), R.color.gray_hint) :
                        ContextCompat.getColor(getContext(), R.color.white));
                tv.setTextSize(14);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ?
                        ContextCompat.getColor(getContext(), R.color.gray_hint) :
                        ContextCompat.getColor(getContext(), R.color.white));
                tv.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_card));
                tv.setPadding(40, 30, 40, 30);
                tv.setTextSize(14);
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGrade.setAdapter(adapter);
    }

    private void setupTermsText() {
        String fullText = "By continuing, you agree to our Terms of Service and\nPrivacy Policy.";
        SpannableString spannable = new SpannableString(fullText);
        int cyan = ContextCompat.getColor(this, R.color.cyan_primary);
        int gray = ContextCompat.getColor(this, R.color.text_secondary);

        spannable.setSpan(new ForegroundColorSpan(gray), 0, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int termsStart = fullText.indexOf("Terms of Service");
        if (termsStart != -1) {
            spannable.setSpan(new ForegroundColorSpan(cyan), termsStart, termsStart + "Terms of Service".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int privacyStart = fullText.indexOf("Privacy Policy");
        if (privacyStart != -1) {
            spannable.setSpan(new ForegroundColorSpan(cyan), privacyStart, privacyStart + "Privacy Policy".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        binding.tvTerms.setText(spannable);
    }

    private void setupLoginText() {
        String fullText = "Already have an account? Log In";
        SpannableString spannable = new SpannableString(fullText);
        int cyan = ContextCompat.getColor(this, R.color.cyan_primary);
        int gray = ContextCompat.getColor(this, R.color.text_secondary);

        spannable.setSpan(new ForegroundColorSpan(gray), 0, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int loginStart = fullText.indexOf("Log In");
        if (loginStart != -1) {
            spannable.setSpan(new ForegroundColorSpan(cyan), loginStart, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        binding.tvLogin.setText(spannable);
    }

    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnCreateAccount.setOnClickListener(v -> {

            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
            int gradePosition = binding.spinnerGrade.getSelectedItemPosition();

            if (fullName.isEmpty()) {
                showToast("Please enter your full name");
                return;
            }
            if (email.isEmpty()) {
                showToast("Please enter your email");
                return;
            }
            if (password.isEmpty()) {
                showToast("Please enter a password");
                return;
            }
            if (!password.equals(confirmPassword)) {
                showToast("Passwords do not match");
                return;
            }
            if (gradePosition == 0) {
                showToast("Please select your grade");
                return;
            }

            // ⭐⭐ الحل هنا ⭐⭐
            String selectedGrade = binding.spinnerGrade.getSelectedItem().toString();

            ApiService apiService = ApiClient.getService();

            User user = new User(email, password);
            user.setName(fullName);        // ✅ مهم جداً
            user.setGrade(selectedGrade);  // ✅ مهم جداً

            apiService.register(user).enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        showToast("Account created successfully!");

                        Intent intent = new Intent(RegisterActivity.this, PersonalizationActivity.class);
                        startActivity(intent);

                        finish();
                        User registeredUser = response.body();
                        getSharedPreferences("user", MODE_PRIVATE)
                                .edit()
                                .putLong("user_id", registeredUser.getId())
                                .apply();

                    } else {
                        showToast("Error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    showToast("Server error: " + t.getMessage());
                }
            });
        });

        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}