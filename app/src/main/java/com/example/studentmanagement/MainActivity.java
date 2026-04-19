package com.example.studentmanagement;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studentmanagement.ui.auth.LoginActivity;
import com.example.studentmanagement.ui.matiere.MatiereListActivity;
import com.example.studentmanagement.ui.note.NoteListActivity;
import com.example.studentmanagement.ui.student.StudentListActivity;
import com.example.studentmanagement.utils.PreferencesManager;
import com.example.studentmanagement.viewmodel.MatiereViewModel;
import com.example.studentmanagement.viewmodel.NoteViewModel;
import com.example.studentmanagement.viewmodel.StudentViewModel;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private CardView cardStudents, cardMatieres, cardNotes;
    private Toolbar toolbar;
    private LinearLayout layoutUserInfo;
    private TextView tvUserName, tvUserRole, tvStudentCount, tvSubjectCount, tvAverageGrade;
    private ImageView ivUserAvatar;
    private PreferencesManager preferencesManager;
    private StudentViewModel studentViewModel;
    private MatiereViewModel matiereViewModel;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize PreferencesManager early
        preferencesManager = new PreferencesManager(this);
        // Apply saved theme and locale BEFORE super.onCreate
        applySavedTheme();
        applySavedLocale();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setupToolbar();
        setupViewModels();
        setupClickListeners();

        loadStatistics();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        cardStudents = findViewById(R.id.cardStudents);
        cardMatieres = findViewById(R.id.cardMatieres);
        cardNotes = findViewById(R.id.cardNotes);
        layoutUserInfo = findViewById(R.id.layoutUserInfo);

        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvStudentCount = findViewById(R.id.tvStudentCount);
        tvSubjectCount = findViewById(R.id.tvSubjectCount);
        tvAverageGrade = findViewById(R.id.tvAverageGrade);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupViewModels() {
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);
        matiereViewModel = new ViewModelProvider(this).get(MatiereViewModel.class);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
    }




    private void loadStatistics() {
        studentViewModel.getAllStudents().observe(this, students -> {
            if (students != null) {
                tvStudentCount.setText(String.valueOf(students.size()));
            }
        });

        matiereViewModel.getAllMatieres().observe(this, matieres -> {
            if (matieres != null) {
                tvSubjectCount.setText(String.valueOf(matieres.size()));
            }
        });

        noteViewModel.getAllNotes().observe(this, notes -> {
            if (notes != null && !notes.isEmpty()) {
                double sum = 0;
                for (com.example.studentmanagement.data.local.entity.Note note : notes) {
                    sum += note.getNote();
                }
                double avg = sum / notes.size();
                tvAverageGrade.setText(String.format("%.1f", avg));
            } else {
                tvAverageGrade.setText("0.0");
            }
        });
    }

    private void setupClickListeners() {
        cardStudents.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, StudentListActivity.class)));

        cardMatieres.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MatiereListActivity.class)));

        cardNotes.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, NoteListActivity.class)));

        layoutUserInfo.setOnClickListener(v -> showUserMenu());
    }

    private void showUserMenu() {
        PopupMenu popup = new PopupMenu(this, layoutUserInfo);
        popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

        // Set user info in header
        String fullName = preferencesManager.getUserFullName();
        if (!fullName.trim().isEmpty()) {
            popup.getMenu().findItem(R.id.action_user_name).setTitle(fullName);
        }

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_profile) {
                showProfileDialog();
                return true;
            } else if (itemId == R.id.action_settings) {
                showSettingsDialog();
                return true;
            } else if (itemId == R.id.action_logout) {
                confirmLogout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showSettingsDialog() {
        String[] options = {getString(R.string.change_language), getString(R.string.dark_light_mode)};

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.settings))
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showLanguageDialog();
                    } else if (which == 1) {
                        showThemeDialog();
                    }
                })
                .show();
    }

    private void showThemeDialog() {
        String[] themes = {getString(R.string.light), getString(R.string.dark), getString(R.string.system_default)};
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.choose_theme))
            .setItems(themes, (dialog, which) -> {
                String themeKey;
                switch (which) {
                    case 0:
                        themeKey = "light";
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case 1:
                        themeKey = "dark";
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    default:
                        themeKey = "system";
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                }
                // Persist selected theme
                preferencesManager.setTheme(themeKey);
                // Recreate to apply immediately
                recreate();
            })
            .show();
    }

    private void showLanguageDialog() {
        String[] languages = {getString(R.string.french), getString(R.string.english), getString(R.string.arabic)};
        String[] codes = {"fr", "en", "ar"};

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_language))
                .setItems(languages, (dialog, which) -> {
                    setLocale(codes[which]);
                })
                .show();
    }

    private void setLocale(String langCode) {
        // Persist selected language
        preferencesManager.setLanguage(langCode);
        // Apply locale (including RTL if needed)
        applySavedLocale();
        // Recreate to reflect changes
        recreate();
    }

    // Apply saved locale (including RTL handling)
    private void applySavedLocale() {
        String savedLang = preferencesManager.getLanguage();
        if (savedLang != null && !savedLang.isEmpty()) {
            Locale locale = new Locale(savedLang);
            Locale.setDefault(locale);
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            // Enable RTL for Arabic or other RTL languages
            if (isRtlLanguage(savedLang)) {
                config.setLayoutDirection(locale);
            }
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }

    // Helper to detect RTL languages
    private boolean isRtlLanguage(String lang) {
        return "ar".equalsIgnoreCase(lang) || "iw".equalsIgnoreCase(lang) || "he".equalsIgnoreCase(lang);
    }

    // Apply saved theme
    private void applySavedTheme() {
        String savedTheme = preferencesManager.getTheme();
        if (savedTheme != null) {
            switch (savedTheme) {
                case "light":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "dark":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case "system":
                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }
        }
    }

    private void showProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.profile_information));

        String message = getString(R.string.user_name) + ": " + preferencesManager.getUserFullName() + "\n" +
                getString(R.string.user_id) + ": " + preferencesManager.getUserId() + "\n" +
                getString(R.string.role) + ": " + getString(R.string.administrator);

        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.ok), null);
        builder.setIcon(R.drawable.ic_user_circle);
        builder.show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
                .setPositiveButton(getString(R.string.logout), (dialog, which) -> {
                    preferencesManager.logout();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, getString(R.string.logged_out_successfully), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Inflate the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}