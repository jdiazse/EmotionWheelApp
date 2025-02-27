package model;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.*;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

public class FirebaseManager {
    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static final String FIREBASE_API_KEY = "AIzaSyDqV7w9mTuGyjV8ZfO4NDEyapwUml-jExk";

    public static void initializeFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream("C:\\Users\\jdsjh\\Downloads\\EmotionWheelKEY\\emotionwheel-7181f-firebase-adminsdk-fbsvc-51f8b96d81.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://emotionwheel-7181f-default-rtdb.firebaseio.com")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            database = FirebaseDatabase.getInstance().getReference();
            auth = FirebaseAuth.getInstance();
            System.out.println("✅ Firebase Initialized Successfully!");
        } catch (IOException e) {
            System.err.println("❌ Failed to initialize Firebase: " + e.getMessage());
        }
    }

    public static String registerUser(String email, String password, String role, String username) {
        try {
            DatabaseReference usernameRef = database.child("usernames").child(username);

            final boolean[] usernameExists = {false};
            final CountDownLatch latch = new CountDownLatch(1);

            usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        usernameExists[0] = true;
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("❌ Error checking username: " + databaseError.getMessage());
                    latch.countDown();
                }
            });

            latch.await();

            if (usernameExists[0]) {
                System.err.println("❌ El nombre de usuario ya está en uso");
                return null;
            }

            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            UserRecord userRecord = auth.createUser(request);
            String userId = userRecord.getUid();

            Map<String, Object> userData = new HashMap<>();
            userData.put("role", role);
            userData.put("username", username);

            database.child("users").child(userId).setValueAsync(userData);
            database.child("usernames").child(username).setValueAsync(userId);

            System.out.println("✅ User registered successfully: " + userId);
            return userId;
        } catch (FirebaseAuthException e) {
            System.err.println("❌ Error registering user: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            System.err.println("❌ Thread interrupted while checking username: " + e.getMessage());
            return null;
        }
    }

    public static String loginUser(String email, String password) {
        try {
            String urlString = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            json.put("returnSecureToken", true);

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String userId = jsonResponse.getString("localId");
            System.out.println("✅ Login Successful! User ID: " + userId);
            return userId;

        } catch (Exception e) {
            System.err.println("❌ Error logging in: " + e.getMessage());
            return null;
        }
    }

    public static void getUserIdFromUsername(String username, UsernameListener listener) {
        database.child("usernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onUserIdFound(dataSnapshot.getValue(String.class));
                } else {
                    listener.onUserIdNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    public static void saveEmotionToFirebase(String userId, String emotion, String comment) {
        if (database == null) {
            System.err.println("❌ Firebase not initialized!");
            return;
        }

        System.out.println("ID sanitizado al guardar emoción: " + userId);

        Map<String, Object> data = new HashMap<>();
        data.put("emotion", emotion);
        data.put("comment", comment);
        data.put("timestamp", System.currentTimeMillis());

        database.child("users").child(userId).child("emotions").push().setValueAsync(data);
        System.out.println("✅ Emotion saved to Firebase: " + emotion + " - " + comment);
    }

    public static void fetchEmotionLogs(String userId, EmotionLogsListener listener) {
        if (database == null) {
            System.err.println("❌ Firebase not initialized!");
            return;
        }

        System.out.println("ID sanitizado al recuperar emociones: " + userId);

        database.child("users").child(userId).child("emotions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Map<String, Object>> logs = new ArrayList<>();
                for (DataSnapshot logSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> log = (Map<String, Object>) logSnapshot.getValue();
                    log.put("logId", logSnapshot.getKey());
                    logs.add(log);
                }
                listener.onLogsFetched(logs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("❌ Error fetching logs: " + databaseError.getMessage());
                listener.onLogsFetchFailed(databaseError.getMessage());
            }
        });
    }

    public static void deleteEmotionLog(String userId, String logId) {
        if (database == null) {
            System.err.println("❌ Firebase not initialized!");
            return;
        }

        System.out.println("ID sanitizado al eliminar emoción: " + userId);

        database.child("users").child(userId).child("emotions").child(logId).removeValueAsync();
        System.out.println("✅ Emotion log deleted: " + logId);
    }

    public static void deleteAllEmotionLogs(String userId) {
        if (database == null) {
            System.err.println("❌ Firebase not initialized!");
            return;
        }

        System.out.println("ID sanitizado al eliminar todas las emociones: " + userId);

        database.child("users").child(userId).child("emotions").removeValueAsync();
        System.out.println("✅ All emotion logs deleted for user: " + userId);
    }

    public static void checkUserRole(String userId, UserRoleListener listener) {
        if (database == null) {
            System.err.println("❌ Firebase not initialized!");
            return;
        }

        System.out.println("ID sanitizado al recuperar rol: " + userId);

        database.child("users").child(userId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String role = dataSnapshot.getValue(String.class);
                if (role == null) {
                    System.err.println("❌ Role not found for user: " + userId);
                    role = "Paciente";
                }
                listener.onRoleFetched(role);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("❌ Error fetching user role: " + databaseError.getMessage());
                listener.onRoleFetchFailed(databaseError.getMessage());
            }
        });
    }

    public static void fetchPatientsLogs(String therapistUserId, PatientsLogsListener listener) {
        if (database == null) {
            System.err.println("❌ Firebase not initialized!");
            return;
        }

        System.out.println("ID sanitizado al recuperar pacientes: " + therapistUserId);

        database.child("users").child(therapistUserId).child("patients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> patientIds = new ArrayList<>();
                for (DataSnapshot patientSnapshot : dataSnapshot.getChildren()) {
                    patientIds.add(patientSnapshot.getValue(String.class));
                }
                listener.onPatientsFetched(patientIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("❌ Error fetching patients: " + databaseError.getMessage());
                listener.onPatientsFetchFailed(databaseError.getMessage());
            }
        });
    }

    public static void sendDiagnosis(String therapistUserId, String username, String diagnosis) {
        getUserIdFromUsername(username, new UsernameListener() {
            @Override
            public void onUserIdFound(String patientUserId) {
                database.child("users").child(patientUserId).child("diagnosis").child(therapistUserId)
                        .setValueAsync(diagnosis);
                System.out.println("✅ Diagnosis sent successfully!");
            }

            @Override
            public void onUserIdNotFound() {
                System.err.println("❌ Username not found");
            }

            @Override
            public void onError(String errorMessage) {
                System.err.println("❌ Error sending diagnosis: " + errorMessage);
            }
        });
    }

    public static void fetchDiagnosis(String patientUserId, DiagnosisListener listener) {
        if (database == null) {
            System.err.println("❌ Firebase not initialized!");
            return;
        }

        System.out.println("ID sanitizado al recuperar diagnóstico: " + patientUserId);

        database.child("users").child(patientUserId).child("diagnosis").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder diagnosisText = new StringBuilder();
                for (DataSnapshot therapistSnapshot : dataSnapshot.getChildren()) {
                    diagnosisText.append("Therapist ID: ").append(therapistSnapshot.getKey()).append("\n")
                            .append("Diagnosis: ").append(therapistSnapshot.getValue(String.class)).append("\n\n");
                }
                listener.onDiagnosisFetched(diagnosisText.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("❌ Error fetching diagnosis: " + databaseError.getMessage());
                listener.onDiagnosisFetchFailed(databaseError.getMessage());
            }
        });
    }

    public static void editEmotionLog(String userId, String logId, String newComment) {
        if (database == null) {
            System.err.println("❌ Firebase not initialized!");
            return;
        }

        System.out.println("ID sanitizado al editar emoción: " + userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("comment", newComment);

        database.child("users").child(userId).child("emotions").child(logId).updateChildrenAsync(updates);
        System.out.println("✅ Emotion log updated: " + logId);
    }

    public interface EmotionLogsListener {
        void onLogsFetched(List<Map<String, Object>> logs);
        void onLogsFetchFailed(String errorMessage);
    }

    public interface UserRoleListener {
        void onRoleFetched(String role);
        void onRoleFetchFailed(String errorMessage);
    }

    public interface PatientsLogsListener {
        void onPatientsFetched(List<String> patientIds);
        void onPatientsFetchFailed(String errorMessage);
    }

    public interface DiagnosisListener {
        void onDiagnosisFetched(String diagnosis);
        void onDiagnosisFetchFailed(String errorMessage);
    }

    public interface UsernameListener {
        void onUserIdFound(String userId);
        void onUserIdNotFound();
        void onError(String errorMessage);
    }
}