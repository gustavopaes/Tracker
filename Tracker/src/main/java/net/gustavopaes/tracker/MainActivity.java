package net.gustavopaes.tracker;

import android.location.LocationListener;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.EditText;
import android.location.Location;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {

    TextView latitude;
    TextView longitude;
    Switch statusApp;
    EditText contaApp;
    TextView logArea;
    SharedPreferences preferences;
    TrackerLocation location;
    TrackerWebservice webservice;

    private static String PREF_EMAIL = "appEmailConta";
    private static String PREF_STATUS = "appState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity instance = this;

        // Lê as preferências do app
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtém os elementos do formulário
        latitude = (TextView) findViewById(R.id.valueLatitude);
        longitude = (TextView) findViewById(R.id.valueLongitude);
        statusApp = (Switch) findViewById(R.id.statusApp);
        contaApp = (EditText) findViewById(R.id.valueEmailConta);
        logArea = (TextView) findViewById(R.id.logArea);

        // Armazena a instancia em uma variável para ser acessada internamente em funções
        // fora desse escopo.
        location = new TrackerLocation(instance);

        // Instancia o Webservice
        webservice = new TrackerWebservice(instance);

        // define evento para ativacao/desativacao do app
        statusApp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                instance.changeAppState(isChecked);

                // salva a definição na configuração do app
                SharedPreferences.Editor edit = instance.preferences.edit();
                edit.putBoolean(PREF_STATUS, isChecked);
                edit.commit();
            }
        });

        // define valor aos campos de definição do formulário, baseado nas configurações salvas
        statusApp.setChecked(preferences.getBoolean(PREF_STATUS, false));
        contaApp.setText(preferences.getString(PREF_EMAIL, ""));
    }

    /**
     * Obtém a posição do usuário, via network ou GPS, o que estiver disponível.
     *
     * Registra a posição na variável global 'lastKnowLocation' para que possa ser
     * exibido no view e usada pelo service.
     *
     * Esse método também registra um Listener para obter uma posição mais detalhada
     * e registra um Listener para o Service atualizar a posição no servidor.
     */
    private void getUserLocation() {
        this.workWithLocation(location.getKnowLocation());
    }

    /**
     * Escreve a nova localização nos labels destinados para isso e envia via HTTP POST
     * a hora e a localização para o webserivce.
     * @param location
     */
    public void workWithLocation(Location location) {
        try {
            String x = String.valueOf(location.getLatitude());
            String y = String.valueOf(location.getLongitude());
            latitude.setText( x );
            longitude.setText( y );

            //Log.i("Nova latitude", x);
            //Log.i("Nova longitude", y);

            //logArea.setText("Definindo novas posicoes\n"+logArea.getText());
            //logArea.setText("Nova latitude: "+String.valueOf(x)+"\n"+logArea.getText());
            //logArea.setText("Nova longitude: "+String.valueOf(y)+"\n"+logArea.getText());
        }
        catch (NullPointerException e) {
            logArea.setText("Location veio NULL\n"+logArea.getText());
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */

    /**
     * Salva o e-mail da conta.
     */
    public void saveEmailConta(View view) {
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString(PREF_EMAIL, String.valueOf(this.contaApp.getText()));
        edit.commit();
    }

    /**
     * Faz todas as mudanças visuais e executa os métodos necessários
     * quando o status do App é alterado.
     * É executado também no onCreate();
     */
    public void changeAppState(Boolean state) {
        Log.i("App state", String.valueOf(state));

        if(state) {
            logArea.setText("Status Internet: "+(String.valueOf(webservice.isNetworkAvailable()))+"\nAtivado!");
            this.getUserLocation();

            // inicia
            location.startListenerLocation();
        }
        else {
            // para
            location.stopListenerLocation();

            logArea.setText("Desativado!");
            latitude.setText("---");
            longitude.setText("---");
        }
    }
}
