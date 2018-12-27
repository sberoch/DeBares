package com.eriochrome.bartime.vistas;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.eriochrome.bartime.R;
import com.eriochrome.bartime.contracts.DistincionContract;
import com.eriochrome.bartime.presenters.DistincionPresenter;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import static com.eriochrome.bartime.utils.Utils.toastShort;

public class DistincionDeUsuarioActivity extends AppCompatActivity implements DistincionContract.View {

    private Button tengoBar;
    private Button comenzar;

    DistincionPresenter presenter;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distincion_de_usuario);

        tengoBar = findViewById(R.id.tengoBar);
        comenzar = findViewById(R.id.comenzar);

        setTypefaces();
        setListeners();

        presenter = new DistincionPresenter();
        presenter.bind(this);
    }


    private void setListeners() {
        comenzar.setOnClickListener(view -> {
            startActivity(new Intent(DistincionDeUsuarioActivity.this, ListadosActivity.class));
            finish();
        });
        tengoBar.setOnClickListener(view -> loginBar());
    }


    private void setTypefaces() {
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Lato-Bold.ttf");
        tengoBar.setTypeface(tf);
        comenzar.setTypeface(tf);
    }


    @Override
    public void loginBar() {
        //TODO: rompe MVP, cuando lo haga custom pensar en eso.
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                if (presenter.esNuevoUsuario()) {
                    presenter.subirUsuarioADatabase();
                }
                //Entro a BarControlActivity
                startActivity(new Intent(DistincionDeUsuarioActivity.this, BarControlActivity.class));
                overridePendingTransition(0,0);
                finish();
            } else {
                toastShort(DistincionDeUsuarioActivity.this, "Ocurrio un error. Intente nuevamente");
            }
        }
    }


    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }
}
