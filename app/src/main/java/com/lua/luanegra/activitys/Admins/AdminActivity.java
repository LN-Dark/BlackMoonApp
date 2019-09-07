package com.lua.luanegra.activitys.Admins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.Salas.Jogos.SalasJogosAntigaActivity;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.objects.GroupObject;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.OnlineService;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {
    private static String currentUserName;
    private ArrayList<UserObject> userList;
    private ArrayList<GroupObject> groupList;
    private String FCMServer, SinchKey, SinchSecret;
    private ArrayList<String> superAdminsList;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void setTheme(int resId) {
        SharedPreferences prefs = getSharedPreferences("AppTheme", Context.MODE_PRIVATE);
        String tema = " ";
        tema = prefs.getString("AppTheme", " ");
        if(tema != " "){
            if(tema.equals("light")){
                super.setTheme(R.style.AppTheme_Light);
            }else {
                super.setTheme(R.style.AppTheme);
            }
        }else {
            super.setTheme(R.style.AppTheme_Light);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("Admins");
        setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            MaterialButton Patronos = findViewById(R.id.btn_utilizadores_Patronos);
            MaterialButton acessoAdminsBTN = findViewById(R.id.btn_AcessoAdmins);
            MaterialButton notificationButton = findViewById(R.id.btn_notificacoes_activity);
            MaterialButton memesButton = findViewById(R.id.btn_memes_activity);
            MaterialButton logButton = findViewById(R.id.btn_log_admin);
            MaterialButton salaAdminsButton = findViewById(R.id.btn_salaAdmin_activity);
            MaterialButton bloquearUser = findViewById(R.id.btn_BlockUser_admin);
            MaterialButton pedidoNovasFuncionalidadesBTN = findViewById(R.id.btn_VisualizarPedidoFuncionalidade);
            MaterialButton verificarDBBTN = findViewById(R.id.btn_fazerVerificacaoUser);
            verificarDBBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerifyBD();
                }
            });
        pedidoNovasFuncionalidadesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NovasFuncionalidadesAdminActivity.class);
                startActivity(intent);
            }
        });
        Patronos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PatronosAdminActivity.class);
                startActivity(intent);
            }
        });
        acessoAdminsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegistoAcessoAdminsActivity.class);
                startActivity(intent);
            }
        });
        bloquearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), BloquearUtilizadorAdminActivity.class);
                startActivity(intent);
            }
        });
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LogAdminActivity.class);
                startActivity(intent);
            }
        });
        salaAdminsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentGroupName = groupList.get(0).getGroupName();
                Intent groupIntent = new Intent(v.getContext(), SalasJogosAntigaActivity.class);
                groupIntent.putExtra("groupName", currentGroupName);
                groupIntent.putExtra("groupUid", groupList.get(0).getUid());
                v.getContext().startActivity(groupIntent);
            }
        });
        memesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MemesAdminActivity.class);
                startActivity(intent);
            }
        });
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NotificationAdminActivity.class);
                startActivity(intent);
            }
        });
            MaterialButton gerarRelatorio = findViewById(R.id.btn_GerarRelatorio_admin);
        gerarRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeraRelatorio();
            }
        });

        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + e.getCause().toString() + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    int numerodeuserscorrigidos;
    private void VerifyBD(){
        numerodeuserscorrigidos = 0;
        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childUsersSnapShot : dataSnapshot.getChildren()){
                        if(!childUsersSnapShot.getKey().equals("chat")){
                            if(!childUsersSnapShot.child("name").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("name").setValue("UserUnknown");
                            }
                            if(!childUsersSnapShot.child("bio").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("bio").setValue("Too lazy to write a Bio.");
                            }
                            if(!childUsersSnapShot.child("notificationKey").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("notificationKey").setValue("noKey");
                            }
                            if(!childUsersSnapShot.child("online").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("online").setValue("false");
                            }
                            if(!childUsersSnapShot.child("onlineDate").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("onlineDate").setValue("17 May, 2019");
                            }
                            if(!childUsersSnapShot.child("onlineTime").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("onlineTime").setValue("11:50");
                            }
                            if(!childUsersSnapShot.child("patrono").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("patrono").setValue("false");
                            }
                            if(!childUsersSnapShot.child("profile_image").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("profile_image").setValue("https://firebasestorage.googleapis.com/v0/b/bdlua-29c71.appspot.com/o/Profile_Images%2F73R6I0KnEuhFwmSD7WpK3TlVon02%2F-LewX1rv1EreGkzlW1lF?alt=media&token=237cb7cb-48e5-4e10-b4cd-27f20096de6d");
                            }
                            if(!childUsersSnapShot.child("registerDate").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("registerDate").setValue("17 May, 2019");
                            }
                            if(!childUsersSnapShot.child("registerTime").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("registerTime").setValue("11:50");
                            }
                            if(!childUsersSnapShot.child("opcoes").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("opcoes").child("exitConf").setValue("true");
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("opcoes").child("notificacoes").setValue("true");
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("opcoes").child("sociavel").setValue("true");
                            }
                            if(!childUsersSnapShot.child("registerNick").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("registerNick").setValue("UserUnknown");
                            }
                        }
                        numerodeuserscorrigidos++;
                    }
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.verificacaocompleta) + " " + numerodeuserscorrigidos + " " + getString(R.string.verificados), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int numeroUserBloqueadosGlobal = 0;
    int numeroDeSalasPrivadas = 0;
    int numeroDeUtilizadoresRegistados = 0;
    int numeroDeSalasComReport = 0;
    int numerodeSalasCom5ReportNoTempo = 0;
    int numeroDeSalasPorVerificar = 0;
    int numeroDeSalasQueAcabaHojeOTempo = 0;
    int numerodesalasCom5ReportsApagadosNestaSessao = 0;
    int numeroDeUtilizadoresSuspeitos = 0;
    int numeroDePessoasQueFizeramLoginHoje = 0;
    int numeroDeUtilizadoresSuspeitosBloqueadosEstaSessao = 0;
    int numerodeUsersUnknown = 0;
    int numerouserspatrono = 0;
    int numerodeconversasprivadas = 0;
    ArrayList<String> listadeSalasParaApagar, listadeNovosUsersSuspeitos;

    private void GeraRelatorio(){
        numeroUserBloqueadosGlobal = 0;
        numeroDeSalasPrivadas = 0;
        numeroDeUtilizadoresRegistados = 0;
        numeroDeSalasComReport = 0;
        numerodeSalasCom5ReportNoTempo = 0;
        numeroDeSalasPorVerificar = 0;
        numerodesalasCom5ReportsApagadosNestaSessao = 0;
        numeroDeUtilizadoresSuspeitos = 0;
        numeroDeSalasQueAcabaHojeOTempo = 0;
        numeroDePessoasQueFizeramLoginHoje = 0;
        numerouserspatrono = 0;
        numeroDeUtilizadoresSuspeitosBloqueadosEstaSessao = 0;
        numerodeUsersUnknown = 0;
        numerodeconversasprivadas = 0;
        listadeSalasParaApagar = new ArrayList<>();
        listadeNovosUsersSuspeitos = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("bloqued_users").exists()){
                        for(DataSnapshot childSnapShot : dataSnapshot.child("bloqued_users").getChildren()){
                            numeroUserBloqueadosGlobal++;
                        }
                    }
                    if(dataSnapshot.child("salasPrivadas").exists()){
                        for(final DataSnapshot childSnapShot : dataSnapshot.child("salasPrivadas").getChildren()){
                            numeroDeSalasPrivadas++;
                            if(childSnapShot.child("reportsTemporarios").exists()){
                                if(childSnapShot.child("reportsTemporarios").getChildrenCount() > 4){
                                    if(childSnapShot.child("reports").exists()){
                                        numeroDeSalasComReport++;
                                        Calendar CalForDate = Calendar.getInstance();
                                        String dataBaseDados = " ";
                                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                        String currentDate = currentDateFormat.format(CalForDate.getTime());
                                        for(DataSnapshot childreportssnap : childSnapShot.child("reports").getChildren()){
                                            dataBaseDados = childreportssnap.child("data").getValue().toString();
                                        }
                                        Calendar c = Calendar.getInstance();
                                        try {
                                            c.setTime(currentDateFormat.parse(dataBaseDados));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        c.add(Calendar.DAY_OF_MONTH, 5);
                                        String output = currentDateFormat.format(c.getTime());
                                        Date date1 = null;
                                        Date date2 = null;
                                        try {
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                            date1 = sdf.parse(currentDate);
                                             date2 = sdf.parse(output);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (date1.compareTo(date2) > 0) {
                                            for(DataSnapshot childUserSalasSnapShot : childSnapShot.child("users").getChildren()){
                                                listadeNovosUsersSuspeitos.add(childUserSalasSnapShot.getKey());

                                            }
                                            if(!listadeNovosUsersSuspeitos.contains(childSnapShot.child("creator").getValue().toString())){
                                                listadeNovosUsersSuspeitos.add(childSnapShot.child("creator").getValue().toString());
                                            }
                                            listadeSalasParaApagar.add(childSnapShot.getKey());
                                            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(childSnapShot.getKey()).removeValue();
                                            numerodesalasCom5ReportsApagadosNestaSessao++;
                                            numeroDeSalasPrivadas--;
                                        } else if (date1.compareTo(date2) == 0) {
                                            numeroDeSalasQueAcabaHojeOTempo++;
                                        } else if (date1.compareTo(date2) < 0) {
                                            numerodeSalasCom5ReportNoTempo++;
                                        }
                                        if(childSnapShot.child("verifiRequest").exists()){
                                            if(childSnapShot.child("verifiRequest").child("auto").getValue().toString().equals("true")){
                                                numeroDeSalasPorVerificar++;
                                            }
                                        }

                                    }else {
                                        Calendar CalForDate = Calendar.getInstance();
                                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                        String currentDate = currentDateFormat.format(CalForDate.getTime());
                                        String keyReport = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(childSnapShot.getKey()).child("reports").push().getKey();
                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(childSnapShot.getKey()).child("reports").child(keyReport).child("data").setValue(currentDate);
                                        numerodeSalasCom5ReportNoTempo++;
                                    }
                                }else {
                                    numeroDeSalasComReport++;
                                }
                            }

                        }
                    }
                    if(dataSnapshot.child("user").exists()){
                        for(DataSnapshot childuserSnapShot : dataSnapshot.child("user").getChildren()){
                            if(!childuserSnapShot.getKey().equals("chat")){
                                numeroDeUtilizadoresRegistados++;
                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                Calendar c = Calendar.getInstance();
                                try {
                                    c.setTime(sdf.parse(childuserSnapShot.child("onlineDate").getValue().toString()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                String output = sdf1.format(c.getTime());
                                Calendar CalForDate = Calendar.getInstance();
                                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                String currentDate = currentDateFormat.format(CalForDate.getTime());
                                if ( currentDate.equals(output)) {
                                    numeroDePessoasQueFizeramLoginHoje++;
                                }
                                if(childuserSnapShot.child("name").getValue().toString().equals("UserUnknown")){
                                    numerodeUsersUnknown++;
                                }
                                if(childuserSnapShot.child("patrono").getValue().toString().equals("true")){
                                    numerouserspatrono++;
                                }
                                if(listadeSalasParaApagar.size() > 0){
                                    if(listadeSalasParaApagar.contains(childuserSnapShot.getKey())){
                                        JSONObject notification33 = new JSONObject();
                                        JSONObject notifcationBody33 = new JSONObject();
                                        try {
                                            String NotificationComposer = getString(R.string.devidoanaotersidopermitidoaverificacao);
                                            notifcationBody33.put("title", getString(R.string.salaprivadaapagada));
                                            notifcationBody33.put("message", NotificationComposer);

                                            notification33.put("to", "/LuaNegra/" + childuserSnapShot.child("notificationKey").getValue().toString());
                                            notification33.put("data", notifcationBody33);
                                        } catch (Exception e) {
                                            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(dataSnapshot.child("usersSuspeitos").exists()){
                        for(DataSnapshot childuserSuspeitoSnapShot : dataSnapshot.child("usersSuspeitos").getChildren()){
                            if(listadeNovosUsersSuspeitos.contains(childuserSuspeitoSnapShot.getKey())) {
                                int numerodeSuspeitices = Integer.parseInt(childuserSuspeitoSnapShot.getValue().toString());
                                numerodeSuspeitices++;
                                FirebaseDatabase.getInstance().getReference().child("usersSuspeitos").child(childuserSuspeitoSnapShot.getKey()).setValue(numerodeSuspeitices);
                                listadeNovosUsersSuspeitos.remove(listadeNovosUsersSuspeitos.lastIndexOf(childuserSuspeitoSnapShot.getKey()));
                                numeroDeUtilizadoresSuspeitos++;
                            }else {
                                numeroDeUtilizadoresSuspeitos++;
                            }
                            if(Integer.parseInt(childuserSuspeitoSnapShot.getValue().toString()) > 4){
                                FirebaseDatabase.getInstance().getReference().child("bloqued_users").child(childuserSuspeitoSnapShot.getKey()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        numeroDeUtilizadoresSuspeitosBloqueadosEstaSessao++;
                                        numeroUserBloqueadosGlobal++;
                                    }
                                });
                            }
                        }
                        for(int h = 0; h < listadeNovosUsersSuspeitos.size(); h++){
                            FirebaseDatabase.getInstance().getReference().child("usersSuspeitos").child(listadeNovosUsersSuspeitos.get(h)).setValue(1);
                            numeroDeUtilizadoresSuspeitos++;
                        }

                    }else {
                        if(!listadeNovosUsersSuspeitos.isEmpty()) {
                            for(int d = 0; d <listadeNovosUsersSuspeitos.size(); d++){
                                FirebaseDatabase.getInstance().getReference().child("usersSuspeitos").child(listadeNovosUsersSuspeitos.get(d)).setValue(1);
                                numeroDeUtilizadoresSuspeitos++;
                            }
                        }
                    }
                    if(dataSnapshot.child("salasPorVerificar").exists()){
                        for(DataSnapshot childSalasVerificarSnapShot : dataSnapshot.child("salasPorVerificar").getChildren()){
                            numeroDeSalasPorVerificar++;
                        }
                    }
                    for(DataSnapshot childSuperAdminSnapShot : dataSnapshot.child("admins").getChildren()){
                        if(childSuperAdminSnapShot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            JSONObject notification2 = new JSONObject();
                            JSONObject notifcationBody2 = new JSONObject();
                            try {
                                String NotificationComposer = getString(R.string.utilizadoressuspeitosbloqueados) + " " + numeroUserBloqueadosGlobal
                                        + getString(R.string.numerodeutilizadoressuspeitos) + " " + numeroDeUtilizadoresSuspeitos
                                        + getString(R.string.numerodeutilizadoresregistados) + " " + numeroDeUtilizadoresRegistados
                                        + getString(R.string.numerodeutilizadoresdesconhecidos) + " " + numerodeUsersUnknown
                                        + getString(R.string.utilizadorespatrono) + " " + numerouserspatrono
                                        + getString(R.string.numerodeutilizadoresquefizeramlogin) + " " + numeroDePessoasQueFizeramLoginHoje
                                        + getString(R.string.numerodeutilizadoressuspeitosbloqueadossessao) + " " + numeroDeUtilizadoresSuspeitosBloqueadosEstaSessao;
                                notifcationBody2.put("title", getString(R.string.relatoriodeutilizadores));
                                notifcationBody2.put("message", NotificationComposer);

                                notification2.put("to", "/LuaNegra/" + dataSnapshot.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").getValue().toString());
                                notification2.put("data", notifcationBody2);
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                            }
                            SendNotification sendNotification2 = new SendNotification();
                            sendNotification2.sendNotification(notification2, getApplicationContext(), FCMServer);
                            JSONObject notification3 = new JSONObject();
                            JSONObject notifcationBody3 = new JSONObject();
                            try {
                                String NotificationComposer = getString(R.string.numerosalasprivadas) + " " + numeroDeSalasPrivadas
                                        + getString(R.string.numerodesalascomreport) + " " + numeroDeSalasComReport
                                        + getString(R.string.numerodesalascom5reportsnotempo) + " " + numerodeSalasCom5ReportNoTempo
                                        + getString(R.string.numerodesalasqueacabahojeotempo) + " " + numeroDeSalasQueAcabaHojeOTempo
                                        + getString(R.string.numerodesalasporverificar) + " " + numeroDeSalasPorVerificar
                                        + getString(R.string.numerodesalascom5reportapagadosnestasessao) + " " + numerodesalasCom5ReportsApagadosNestaSessao;
                                notifcationBody3.put("title", getString(R.string.relatoriodesalas));
                                notifcationBody3.put("message", NotificationComposer);

                                notification3.put("to", "/LuaNegra/" + dataSnapshot.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").getValue().toString());
                                notification3.put("data", notifcationBody3);
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                            }
                            SendNotification sendNotification3 = new SendNotification();
                            sendNotification3.sendNotification(notification3, getApplicationContext(), FCMServer);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GetAppKeys(){
        DatabaseReference mAppKeysRef = FirebaseDatabase.getInstance().getReference().child("appKeys");
        mAppKeysRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        switch (Objects.requireNonNull(childSnapshot.getKey())) {
                            case "serverFCM":
                                FCMServer = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                break;
                            case "sinchAppKey":
                                SinchKey = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                break;
                            case "sinchAppSecret":
                                SinchSecret = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSuperAdmins() {
        superAdminsList = new ArrayList<>();
        final String key = FirebaseDatabase.getInstance().getReference().child("adminAccess").push().getKey();
        Calendar CalForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
        final String currentDate = currentDateFormat.format(CalForDate.getTime());
        Calendar CalForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
        currentTimeFormat.toLocalizedPattern();
        final String currentTime = currentTimeFormat.format(CalForTime.getTime());
        FirebaseDatabase.getInstance().getReference().child("superAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        superAdminsList.add(Objects.requireNonNull(childSnapshot.getKey()));
                    }
                    FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            if(dataSnapshot2.exists()){

                                for(DataSnapshot childSnapShot2 : dataSnapshot2.getChildren()){
                                    if(!Objects.requireNonNull(childSnapShot2.getKey()).equals("chat")){
                                        UserObject userObject = new UserObject(Objects.requireNonNull(childSnapShot2.child("name").getValue()).toString(), childSnapShot2.getKey());
                                        userObject.setImagemPerfilUri(Objects.requireNonNull(childSnapShot2.child("profile_image").getValue()).toString());
                                        userObject.setNotificationKey(Objects.requireNonNull(childSnapShot2.child("notificationKey").getValue()).toString());
                                        userObject.setName(Objects.requireNonNull(childSnapShot2.child("name").getValue()).toString());
                                        userObject.setSuperAdminsList(superAdminsList);
                                        if(!userObject.getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                            userList.add(userObject);
                                        }

                                    }
                                }
                                for(int h = 0; h < userList.size(); h++){
                                    for(int j = 0; j < superAdminsList.size(); j++){
                                        if(userList.get(h).getUid().equals(superAdminsList.get(j))){
                                            if(!userList.get(h).getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                                JSONObject notification = new JSONObject();
                                                JSONObject notifcationBody = new JSONObject();
                                                try {
                                                    notifcationBody.put("title", getString(R.string.acessoaadmins));
                                                    if (currentUserName == null){
                                                        notifcationBody.put("message", "✶ " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()  + " ✶");
                                                    }else {
                                                        notifcationBody.put("message", "✶ " + currentUserName  + " ✶");
                                                    }
                                                    notification.put("to", "/LuaNegra/" + userList.get(h).getNotificationKey());
                                                    notification.put("data", notifcationBody);
                                                } catch (Exception e) {
                                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                                }
                                                SendNotification sendNotification = new SendNotification();
                                                sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                                            }
                                            FirebaseDatabase.getInstance().getReference().child("adminAccess").child(Objects.requireNonNull(key)).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("data").setValue(currentDate);
                                            FirebaseDatabase.getInstance().getReference().child("adminAccess").child(key).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("hora").setValue(currentTime);
                                            if (currentUserName != null){
                                                FirebaseDatabase.getInstance().getReference().child("adminAccess").child(key).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(currentUserName);
                                            }
                                        }
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getCurrentUser();

    }

    @Override
    protected void onResume() {
        super.onResume();
        userList = new ArrayList<>();
        GetAppKeys();
        getCurrentUser();
        getSuperAdmins();
        startService(new Intent(getBaseContext(), OnlineService.class));
        groupList = new ArrayList<>();
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference();
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    getGroupList(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getCurrentUser() {
        FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getGroupList(DataSnapshot dataSnapshot) {
        try{
        groupList.clear();
        for(DataSnapshot childSnapshot : dataSnapshot.child("salas").getChildren()){
            String groupLogoUri = Objects.requireNonNull(childSnapshot.child("logouri").getValue()).toString();
            String groupNome = Objects.requireNonNull(childSnapshot.child("nome").getValue()).toString();
            GroupObject groupObject = new GroupObject(childSnapshot.getKey(), groupNome, groupLogoUri);
            if(groupNome.equals("Admin")){
                groupList.add(groupObject);
            }
        }
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

}
