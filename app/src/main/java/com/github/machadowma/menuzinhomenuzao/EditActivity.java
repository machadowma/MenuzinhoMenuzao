package com.github.machadowma.menuzinhomenuzao;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    public SQLiteDatabase bancoDados;
    public EditText editText;
    public Button button;
    public Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterar();
            }
        });

        carregarDados();
    }

    public void carregarDados(){
        try {
            bancoDados = openOrCreateDatabase("clicklist", MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT id,nome FROM pessoa WHERE id = " + id.toString(), null);
            if(cursor.moveToFirst()) {
                editText.setText(cursor.getString(cursor.getColumnIndex("nome")));
            }

            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alterar(){
        if(TextUtils.isEmpty(editText.getText().toString())){
            editText.setError("Campo obrigatório!");
        } else {
            try {
                bancoDados = openOrCreateDatabase("clicklist", MODE_PRIVATE, null);
                String sql = "UPDATE pessoa SET nome = ? WHERE id = ?";
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1, editText.getText().toString());
                stmt.bindLong(2, id );
                stmt.executeInsert();
                bancoDados.close();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}