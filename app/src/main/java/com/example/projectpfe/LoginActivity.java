package com.example.projectpfe;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.projectpfe.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSignUpText();
        setupPasswordToggle();
        setupClickListeners();
    }

    // نص "Don't have your account? sign up"
    private void setupSignUpText() {
        String fullText = "Don't have your account? sign up";
        SpannableString spannable = new SpannableString(fullText);
        int cyan = ContextCompat.getColor(this, R.color.cyan_primary);

        spannable.setSpan(
                new ForegroundColorSpan(cyan),
                fullText.indexOf("sign up"),
                fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        binding.tvSignUp.setText(spannable);
        binding.tvSignUp.setTextColor(
                ContextCompat.getColor(this, R.color.text_secondary)
        );
    }

    // إظهار/إخفاء كلمة المرور
    private void setupPasswordToggle() {
        binding.ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;

            if (isPasswordVisible) {
                binding.etPassword.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                binding.etPassword.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye);
            }

            // ضعي الكيرسور في النهاية
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });
    }

    private void setupClickListeners() {

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            // هنا ستتواصلين مع صديقتك (Back-end)
        });

        binding.tvForgot.setOnClickListener(v -> {
            // الانتقال لصفحة Forgot Password
        });

        binding.tvSignUp.setOnClickListener(v -> {
            // الانتقال لصفحة Sign Up
        });

        binding.btnGoogle.setOnClickListener(v -> {
            // تسجيل بـ Google
        });

        binding.btnApple.setOnClickListener(v -> {
            // تسجيل بـ Apple
        });
    }
}