package com.example.projectpfe;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.projectpfe.databinding.ActivityRegisterBinding;

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

    // إعداد القائمة المنسدلة للمستوى الدراسي
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
                // الخيار الأول "Select your grade" غير قابل للاختيار
                return position != 0;
            }

            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ?
                        ContextCompat.getColor(RegisterActivity.this, R.color.gray_hint) :
                        ContextCompat.getColor(RegisterActivity.this, R.color.white));
                tv.setTextSize(14);
                return view;
            }

            @Override
            public android.view.View getDropDownView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ?
                        ContextCompat.getColor(RegisterActivity.this, R.color.gray_hint) :
                        ContextCompat.getColor(RegisterActivity.this, R.color.white));
                tv.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.bg_card));
                tv.setPadding(40, 30, 40, 30);
                tv.setTextSize(14);
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGrade.setAdapter(adapter);
    }

    // نص شروط الخدمة مع ألوان مختلفة
    private void setupTermsText() {
        String fullText = "By continuing, you agree to our Terms of Service and\nPrivacy Policy.";
        SpannableString spannable = new SpannableString(fullText);
        int cyan = ContextCompat.getColor(this, R.color.cyan_primary);
        int gray = ContextCompat.getColor(this, R.color.text_secondary);

        // لون رمادي للنص العادي
        spannable.setSpan(
                new ForegroundColorSpan(gray),
                0, fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // لون سماوي لـ "Terms of Service"
        int termsStart = fullText.indexOf("Terms of Service");
        int termsEnd = termsStart + "Terms of Service".length();
        spannable.setSpan(
                new ForegroundColorSpan(cyan),
                termsStart, termsEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // لون سماوي لـ "Privacy Policy"
        int privacyStart = fullText.indexOf("Privacy Policy");
        int privacyEnd = privacyStart + "Privacy Policy".length();
        spannable.setSpan(
                new ForegroundColorSpan(cyan),
                privacyStart, privacyEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        binding.tvTerms.setText(spannable);
    }

    // نص "Already have an account? Log In"
    private void setupLoginText() {
        String fullText = "Already have an account? Log In";
        SpannableString spannable = new SpannableString(fullText);
        int cyan = ContextCompat.getColor(this, R.color.cyan_primary);
        int gray = ContextCompat.getColor(this, R.color.text_secondary);

        // لون رمادي للنص العادي
        spannable.setSpan(
                new ForegroundColorSpan(gray),
                0, fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // لون سماوي لـ "Log In"
        int loginStart = fullText.indexOf("Log In");
        spannable.setSpan(
                new ForegroundColorSpan(cyan),
                loginStart, fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        binding.tvLogin.setText(spannable);
    }

    private void setupClickListeners() {

        // زر الرجوع
        binding.ivBack.setOnClickListener(v -> {
            finish(); // يرجع للصفحة السابقة (Login)
        });

        // زر إنشاء الحساب
        binding.btnCreateAccount.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
            int gradePosition = binding.spinnerGrade.getSelectedItemPosition();

            // التحقق من الحقول
            if (fullName.isEmpty()) {
                Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (gradePosition == 0) {
                Toast.makeText(this, "Please select your grade", Toast.LENGTH_SHORT).show();
                return;
            }

            // هنا يمكنك إرسال البيانات للـ Back-end
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        });

        // رابط تسجيل الدخول
        binding.tvLogin.setOnClickListener(v -> {
            finish(); // يرجع لصفحة Login
        });
    }
}
