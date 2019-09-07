package com.lua.luanegra.activitys.Salas.Publicas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.tools.OnlineService;

import java.util.Objects;

public class CoresSalaPublicaActivity extends AppCompatActivity {

    private String groupUID;
    private String cor_barraMensagens, cor_chatReciever, cor_chatSender,cor_dataHora,cor_fundo, cor_texto, cor_toolbarInferior, cor_toolbarSuperior, cor_apresentacao;
    private int color;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(getBaseContext(), OnlineService.class));
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
        setContentView(R.layout.activity_cores_sala_privada);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + getResources().getString(R.string.personalizarsalaprivada));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        groupUID = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupUID")).toString();
        final MaterialButton btn_cor_barraMensagens = findViewById(R.id.btn_cor_barraMensagens);
        final MaterialButton btn_cor_chatReciever = findViewById(R.id.btn_cor_chatReciever);
        final MaterialButton btn_cor_chatSender = findViewById(R.id.btn_cor_chatSender);
        final MaterialButton btn_cor_dataHora = findViewById(R.id.btn_cor_dataHora);
        final MaterialButton btn_cor_fundo = findViewById(R.id.btn_cor_fundo);
        final MaterialButton btn_cor_texto = findViewById(R.id.btn_cor_texto);
        final MaterialButton btn_cor_toolbarInferior = findViewById(R.id.btn_cor_toolbarInferior);
        final MaterialButton btn_cor_toolbarSuperior = findViewById(R.id.btn_cor_toolbarSuperior);
        final MaterialButton btn_cor_apresentacao = findViewById(R.id.btn_cor_apresentacao);

        FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    cor_barraMensagens = Objects.requireNonNull(dataSnapshot.child("barraMensagens").getValue()).toString();
                    cor_chatReciever = Objects.requireNonNull(dataSnapshot.child("chatReciever").getValue()).toString();
                    cor_chatSender = Objects.requireNonNull(dataSnapshot.child("chatSender").getValue()).toString();
                    cor_dataHora = Objects.requireNonNull(dataSnapshot.child("dataHora").getValue()).toString();
                    cor_fundo = Objects.requireNonNull(dataSnapshot.child("fundo").getValue()).toString();
                    cor_texto = Objects.requireNonNull(dataSnapshot.child("texto").getValue()).toString();
                    cor_toolbarInferior = Objects.requireNonNull(dataSnapshot.child("toolbarInferior").getValue()).toString();
                    cor_toolbarSuperior = Objects.requireNonNull(dataSnapshot.child("toolbarSuperior").getValue()).toString();
                    cor_apresentacao = Objects.requireNonNull(dataSnapshot.child("apresentacao").getValue()).toString();
                    btn_cor_apresentacao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            color = Color.parseColor(cor_apresentacao);
                            ColorPickerDialogBuilder
                                    .with(CoresSalaPublicaActivity.this)
                                    .setTitle(getResources().getString(R.string.escolheacor))
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onColorSelected(int selectedColor) {

                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.gravar), new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").child("apresentacao").setValue(hexColor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, getResources().getString(R.string.coralterada), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancelarcores), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });
                    btn_cor_toolbarSuperior.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            color = Color.parseColor(cor_toolbarSuperior);
                            ColorPickerDialogBuilder
                                    .with(CoresSalaPublicaActivity.this)
                                    .setTitle(getResources().getString(R.string.escolheacor))
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onColorSelected(int selectedColor) {

                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.gravar), new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").child("toolbarSuperior").setValue(hexColor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, getResources().getString(R.string.coralterada), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancelarcores), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });
                    btn_cor_toolbarInferior.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            color = Color.parseColor(cor_toolbarInferior);
                            ColorPickerDialogBuilder
                                    .with(CoresSalaPublicaActivity.this)
                                    .setTitle(getResources().getString(R.string.escolheacor))
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onColorSelected(int selectedColor) {

                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.gravar), new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").child("toolbarInferior").setValue(hexColor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, getResources().getString(R.string.coralterada), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancelarcores), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });

                    btn_cor_texto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            color = Color.parseColor(cor_texto);
                            ColorPickerDialogBuilder
                                    .with(CoresSalaPublicaActivity.this)
                                    .setTitle(getResources().getString(R.string.escolheacor))
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onColorSelected(int selectedColor) {

                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.gravar), new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").child("texto").setValue(hexColor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, getResources().getString(R.string.coralterada), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancelarcores), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });

                    btn_cor_fundo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            color = Color.parseColor(cor_fundo);
                            ColorPickerDialogBuilder
                                    .with(CoresSalaPublicaActivity.this)
                                    .setTitle(getResources().getString(R.string.escolheacor))
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onColorSelected(int selectedColor) {

                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.gravar), new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").child("fundo").setValue(hexColor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, getResources().getString(R.string.coralterada), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancelarcores), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });

                    btn_cor_dataHora.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            color = Color.parseColor(cor_dataHora);
                            ColorPickerDialogBuilder
                                    .with(CoresSalaPublicaActivity.this)
                                    .setTitle(getResources().getString(R.string.escolheacor))
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onColorSelected(int selectedColor) {

                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.gravar), new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").child("dataHora").setValue(hexColor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, getResources().getString(R.string.coralterada), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancelarcores), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });

                    btn_cor_chatSender.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            color = Color.parseColor(cor_chatSender);
                            ColorPickerDialogBuilder
                                    .with(CoresSalaPublicaActivity.this)
                                    .setTitle(getResources().getString(R.string.escolheacor))
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onColorSelected(int selectedColor) {

                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.gravar), new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").child("chatSender").setValue(hexColor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, getResources().getString(R.string.coralterada), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancelarcores), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });

                    btn_cor_chatReciever.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            color = Color.parseColor(cor_chatReciever);
                            ColorPickerDialogBuilder
                                    .with(CoresSalaPublicaActivity.this)
                                    .setTitle(getResources().getString(R.string.escolheacor))
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onColorSelected(int selectedColor) {

                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.gravar), new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").child("chatReciever").setValue(hexColor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, getResources().getString(R.string.coralterada), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancelarcores), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });

                    btn_cor_barraMensagens.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            color = Color.parseColor(cor_barraMensagens);
                            ColorPickerDialogBuilder
                                    .with(CoresSalaPublicaActivity.this)
                                    .setTitle(getResources().getString(R.string.escolheacor))
                                    .initialColor(color)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                                        @Override
                                        public void onColorSelected(int selectedColor) {

                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.gravar), new ColorPickerClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                            String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor));
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("coresSala").child("barraMensagens").setValue(hexColor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, getResources().getString(R.string.coralterada), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancelarcores), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
