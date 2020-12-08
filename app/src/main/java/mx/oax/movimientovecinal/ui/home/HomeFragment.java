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

import mx.oax.movimientovecinal.FormReporte911;
import mx.oax.movimientovecinal.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    LinearLayout ly911;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ly911 = root.findViewById(R.id.ly911);

        /*************************** EVENTO DE LOS BOTONES *******************************/

        ly911.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FormReporte911.class);
                startActivity(i);

            }
        });


        /*********************************************************************************/
        return root;
    }
}