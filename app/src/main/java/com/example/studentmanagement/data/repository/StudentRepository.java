package com.example.studentmanagement.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.studentmanagement.data.local.AppDatabase;
import com.example.studentmanagement.data.local.dao.MatiereDao;
import com.example.studentmanagement.data.local.dao.NoteDao;
import com.example.studentmanagement.data.local.dao.StudentDao;
import com.example.studentmanagement.data.local.entity.Matiere;
import com.example.studentmanagement.data.local.entity.Note;
import com.example.studentmanagement.data.local.entity.Student;
import com.example.studentmanagement.data.remote.ApiService;
import com.example.studentmanagement.data.remote.AuthResponse;
import com.example.studentmanagement.data.remote.RetrofitClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository gérant la logique d'accès aux données.
 * Il sert de médiateur entre les sources de données locales (Room) et distantes (Retrofit).
 */
public class StudentRepository {
    // Accès aux DAOs pour la base de données locale Room
    private final StudentDao studentDao;
    private final MatiereDao matiereDao;
    private final NoteDao noteDao;

    // Service pour les appels API réseau
    private final ApiService apiService;

    // Service d'exécution pour gérer les tâches en arrière-plan
    private final ExecutorService executorService;

    /**
     * Constructeur : Initialise la base de données, les DAOs et le client API.
     */
    public StudentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        studentDao = db.studentDao();
        matiereDao = db.matiereDao();
        noteDao = db.noteDao();
        apiService = RetrofitClient.getClient().create(ApiService.class);
        executorService = Executors.newSingleThreadExecutor();
    }

    // --- API Authentication ---

    /**
     * Effectue une requête de connexion via l'API distante.
     */
    public void login(String username, String password, final AuthCallback callback) {
        apiService.login(username, password).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                // Vérifie si la réponse HTTP est un succès (code 200-299)
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Authentication failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                // Erreur de réseau ou de connexion
                callback.onError(t.getMessage());
            }
        });
    }

    // --- Student operations (Opérations sur les Étudiants) ---

    // Récupère tous les étudiants (mise à jour en temps réel via LiveData)
    public LiveData<List<Student>> getAllStudents() {
        return studentDao.getAllStudents();
    }

    // Récupère un étudiant spécifique par son ID
    public LiveData<Student> getStudentById(int id) {
        return studentDao.getStudentById(id);
    }

    // Recherche des étudiants par nom ou critères via une requête
    public LiveData<List<Student>> searchStudents(String query) {
        return studentDao.searchStudents(query);
    }

    // Insère un étudiant dans la base locale (exécuté sur un thread secondaire)
    public void insertStudent(Student student) {
        AppDatabase.databaseWriteExecutor.execute(() -> studentDao.insert(student));
    }

    // Met à jour les informations d'un étudiant existant
    public void updateStudent(Student student) {
        AppDatabase.databaseWriteExecutor.execute(() -> studentDao.update(student));
    }

    // Supprime un étudiant de la base de données
    public void deleteStudent(Student student) {
        AppDatabase.databaseWriteExecutor.execute(() -> studentDao.delete(student));
    }

    // --- Matiere operations (Opérations sur les Matières) ---

    // Récupère la liste de toutes les matières
    public LiveData<List<Matiere>> getAllMatieres() {
        return matiereDao.getAllMatieres();
    }

    // Récupère une matière par son ID
    public LiveData<Matiere> getMatiereById(int id) {
        return matiereDao.getMatiereById(id);
    }

    // Ajoute une nouvelle matière
    public void insertMatiere(Matiere matiere) {
        AppDatabase.databaseWriteExecutor.execute(() -> matiereDao.insert(matiere));
    }

    // Modifie une matière existante
    public void updateMatiere(Matiere matiere) {
        AppDatabase.databaseWriteExecutor.execute(() -> matiereDao.update(matiere));
    }

    // Supprime une matière
    public void deleteMatiere(Matiere matiere) {
        AppDatabase.databaseWriteExecutor.execute(() -> matiereDao.delete(matiere));
    }

    // --- Note operations (Opérations sur les Notes) ---

    // Récupère toutes les notes d'un étudiant précis
    public LiveData<List<Note>> getNotesByStudent(int studentId) {
        return noteDao.getNotesByStudent(studentId);
    }

    // Récupère l'intégralité des notes de la base de données
    public LiveData<List<Note>> getAllNotes() {
        return noteDao.getAllNotes();
    }

    // Calcule la moyenne des notes pour un étudiant donné
    public LiveData<Double> getAverageNoteForStudent(int studentId) {
        return noteDao.getAverageNoteForStudent(studentId);
    }

    // Récupère une note spécifique par son identifiant
    public LiveData<Note> getNoteById(int id) {
        return noteDao.getNoteById(id);
    }

    // Ajoute une note en base de données
    public void insertNote(Note note) {
        AppDatabase.databaseWriteExecutor.execute(() -> noteDao.insert(note));
    }

    // Met à jour une note
    public void updateNote(Note note) {
        AppDatabase.databaseWriteExecutor.execute(() -> noteDao.update(note));
    }

    // Supprime une note
    public void deleteNote(Note note) {
        AppDatabase.databaseWriteExecutor.execute(() -> noteDao.delete(note));
    }

    /**
     * Interface de rappel (Callback) pour gérer les résultats de l'authentification API.
     */
    public interface AuthCallback {
        void onSuccess(AuthResponse response);
        void onError(String error);
    }
}