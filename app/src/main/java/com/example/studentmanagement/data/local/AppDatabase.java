package com.example.studentmanagement.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.studentmanagement.data.local.dao.MatiereDao;
import com.example.studentmanagement.data.local.dao.NoteDao;
import com.example.studentmanagement.data.local.dao.StudentDao;
import com.example.studentmanagement.data.local.entity.Matiere;
import com.example.studentmanagement.data.local.entity.Note;
import com.example.studentmanagement.data.local.entity.Student;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Student.class, Matiere.class, Note.class}, 
          version = 2, 
          exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StudentDao studentDao();
    public abstract MatiereDao matiereDao();
    public abstract NoteDao noteDao();
    
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = 
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    // Migration from version 1 to 2 (adding indices)
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create indices for notes table
            database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_studentId ON notes (studentId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_notes_matiereId ON notes (matiereId)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_notes_studentId_matiereId ON notes (studentId, matiereId)");
            
            // Create indices for students table
            database.execSQL("CREATE INDEX IF NOT EXISTS index_students_nom ON students (nom)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_students_prenom ON students (prenom)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_students_email ON students (email)");
            
            // Create indices for matieres table
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_matieres_label ON matieres (label)");
        }
    };
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "student_management_db")
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}