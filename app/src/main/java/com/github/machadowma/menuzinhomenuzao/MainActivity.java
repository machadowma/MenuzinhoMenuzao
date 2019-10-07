package com.github.machadowma.menuzinhomenuzao;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public SQLiteDatabase bancoDados;
    public ListView listView;
    public ArrayList<String> nomesArray;
    public ArrayList<Integer> idsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        registerForContextMenu(listView);

        criarBancoDados();
        listarDados();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listarDados();;
    }

    // Configurando menuzão
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menuzao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.adicionar:
                abrirCadastro();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // Configurando menuzinho
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuzinho, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.editar:
                abrirEditar(idsArray.get(info.position));
                return true;
            case R.id.excluir:
                excluir(info.position);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void criarBancoDados(){
        try {
            bancoDados = openOrCreateDatabase("clicklist", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS pessoa(" +
                    "   id INTEGER PRIMARY KEY AUTOINCREMENT" +
                    " , nome VARCHAR" +
                    " ) " );
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirCadastro(){
        Intent intent = new Intent(this,AddActivity.class);
        startActivity(intent);
    }

    public void abrirEditar(Integer id){
        Intent intent = new Intent(this,EditActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void excluir(final Integer position){
        AlertDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir");
        builder.setMessage("Deseja realmente excluir?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                excluirDB(idsArray.get(position));
                listarDados();;
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alerta = builder.create();
        alerta.show();
    }

    private void listarDados() {
        try {
            bancoDados = openOrCreateDatabase("clicklist", MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT id,nome FROM pessoa", null);
            nomesArray = new ArrayList<String>();
            idsArray = new ArrayList<Integer>();
            ArrayAdapter adapter =
                    new ArrayAdapter(this,
                            android.R.layout.simple_list_item_1, nomesArray);
            if(cursor.moveToFirst()) {
                do {
                    nomesArray.add(cursor.getString(cursor.getColumnIndex("nome")));
                    idsArray.add(cursor.getInt(cursor.getColumnIndex("id")));
                } while (cursor.moveToNext());
            }
            listView.setAdapter(adapter);
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluirDB(Integer id){
        try {
            bancoDados = openOrCreateDatabase("clicklist",MODE_PRIVATE ,null);
            String sql = "DELETE FROM pessoa WHERE id = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1, id);
            stmt.executeUpdateDelete();
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
