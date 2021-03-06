package mx.oax.movimientovecinal.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import mx.oax.movimientovecinal.AlertaAmber;
import mx.oax.movimientovecinal.AltoALaViolencia;
import mx.oax.movimientovecinal.FormReporte911;
import mx.oax.movimientovecinal.R;
import mx.oax.movimientovecinal.TransporteSeguro;
import mx.oax.movimientovecinal.VigilanciaVecinal;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    LinearLayout ly911,lyDestinoSeguro,lyAltoViolencia,lyVigilanciaVecinal,lyAlertaAmber;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ly911 = root.findViewById(R.id.ly911);
        lyDestinoSeguro = root.findViewById(R.id.lyDestinoSeguro);
        lyAltoViolencia = root.findViewById(R.id.lyAltoViolencia);
        lyVigilanciaVecinal = root.findViewById(R.id.lyVigilanciaVecinal);
        lyAlertaAmber = root.findViewById(R.id.lyAlertaAmber);

        /*************************** EVENTO DE LOS BOTONES *******************************/

        ly911.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FormReporte911.class);
                startActivity(i);
            }
        });
        lyDestinoSeguro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TransporteSeguro.class);
                startActivity(i);
            }
        });
        lyAltoViolencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AltoALaViolencia.class);
                startActivity(i);
            }
        });
        lyVigilanciaVecinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), VigilanciaVecinal.class);
                startActivity(i);
            }
        });
        lyAlertaAmber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AlertaAmber.class);
                startActivity(i);
            }
        });

        /*********************************************************************************/
        return root;
    }
}