package com.tec.bufeo.capitan.Activity.DetalleEquipo.TabTorneosDeEquipos.Views;

import android.app.Application;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tec.bufeo.capitan.Activity.DetalleEquipo.TabTorneosDeEquipos.Models.TorneosDeEquipos;
import com.tec.bufeo.capitan.Activity.DetalleEquipo.TabTorneosDeEquipos.Repository.TequiposWebServiceRepository;
import com.tec.bufeo.capitan.Activity.DetalleEquipo.TabTorneosDeEquipos.ViewModels.TequiposViewModel;
import com.tec.bufeo.capitan.MVVM.Torneo.TabTorneo.MisTorneos.Models.Torneo;
import com.tec.bufeo.capitan.MVVM.Torneo.TabTorneo.OtrosTorneos.ViewModels.OtrosTorneosViewModel;
import com.tec.bufeo.capitan.MVVM.Torneo.TabTorneo.Views.AdaptadorOtrosTorneos;
import com.tec.bufeo.capitan.R;
import com.tec.bufeo.capitan.Util.Preferences;

import java.util.List;


public class TorneoDequiposFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {



    TequiposViewModel tequiposViewModel;
    RecyclerView rcv_torneosEquipo;
    AdaptersTorneoEquipos adaptersTorneoEquipos;
    String id_equipo;
    Preferences preferences;
    SwipeRefreshLayout swipeTorneosEquipo;
    public TorneoDequiposFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tequiposViewModel= ViewModelProviders.of(getActivity()).get(TequiposViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_torneo_dequipos, container, false);
        preferences= new Preferences(getActivity());

        final Bundle bdl = getArguments();


        id_equipo = bdl.getString("id_equipo");
        initViews(view);
        cargarvista();
        setAdapter();
        return view;
    }

    private void initViews(View view) {
        rcv_torneosEquipo=view.findViewById(R.id.rcv_torneosEquipo);
        swipeTorneosEquipo = view.findViewById(R.id.swipeTorneosEquipo);

        swipeTorneosEquipo.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        swipeTorneosEquipo.setOnRefreshListener(this);
    }
    private void cargarvista() {
        tequiposViewModel.getAllOtrosTorneos(id_equipo,preferences.getToken()).observe(this, new Observer<List<TorneosDeEquipos>>() {
            @Override
            public void onChanged(List<TorneosDeEquipos> torneosDeEquipos) {
                adaptersTorneoEquipos.setWords(torneosDeEquipos);
            }
        });

    }
    private void setAdapter() {
        adaptersTorneoEquipos = new AdaptersTorneoEquipos(getActivity(), new AdaptersTorneoEquipos.OnItemClickListener() {
            @Override
            public void onItemClick(TorneosDeEquipos comments, int position) {

            }
        });
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 1);
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        rcv_torneosEquipo.setLayoutManager(linearLayoutManager);
        rcv_torneosEquipo.setAdapter(adaptersTorneoEquipos);
    }


    Application application;
    @Override
    public void onRefresh() {
        TequiposWebServiceRepository tequiposWebServiceRepository =  new TequiposWebServiceRepository(application);
        tequiposWebServiceRepository.providesWebService(id_equipo,preferences.getToken());

        swipeTorneosEquipo.setRefreshing(false);
    }
}