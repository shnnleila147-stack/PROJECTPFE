package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.projectpfe.databinding.ActivityLoginBinding;
import com.example.projectpfe.api.ApiClient;
import com.example.projectpfe.api.ApiService;
import com.example.projectpfe.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        binding.tvSignUp.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
    }

    private void setupPasswordToggle() {
        binding.ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getService();
            User user = new User(email, password);

            apiService.login(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User loggedUser = response.body();

                        // ✅ حفظ userId في SharedPreferences
                        getSharedPreferences("user", MODE_PRIVATE)
                                .edit()
                                .putLong("user_id", loggedUser.getId())
                                .apply();

                        // ✅ التوجيه الصحيح حسب التخصيص
                        if (loggedUser.isPersonalized()) {
                            // مستخدم قديم أكمل التخصيص → Home
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        } else {
                            // مستخدم جديد لم يكمل التخصيص → Personalization
                            startActivity(new Intent(LoginActivity.this, PersonalizationActivity.class));
                        }

                        finish(); // ✅ إغلاق LoginActivity بعد الانتقال
                    } else {
                        Toast.makeText(LoginActivity.this, "Wrong email/password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Server error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        binding.tvForgot.setOnClickListener(v -> {
            // يمكنك إضافة نداء إعادة تعيين كلمة المرور هنا
        });

        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        binding.btnGoogle.setOnClickListener(v -> {
            // تسجيل الدخول عبر Google
        });
        binding.btnApple.setOnClickListener(v -> {
            // تسجيل الدخول عبر Apple
        });
    }
}